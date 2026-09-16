---
tags: [architecture, domain]
---

# AI Agent — Architecture

Design notes for rewriting the AI assistant on Kotlin Multiplatform. The goal is to remove the architectural bottlenecks of the native Android version and reach a modular, scalable system that is integrated with the rest of the app. Absorbed from the former `documents/agent.md`.

The wire format is documented separately in [[AI-Agent-API-Contract]]. How answers are written and drawn: [[Agent-Markdown]]. How its buttons open screens: [[Deep-Links]].

## 0. What is built (September 2026)

- **Answers are markdown.** Server-rendered entities and every client service produce `ChatBubbleContent.Markdown`; the renderer lives in `feature/agent/.../markdown` (pure parser) and `ui/markdown` (Compose).
- **Access and token.** `CheckChatAllowedUseCase` caches the chat-allowed answer in `AgentAccessStore` (core-datastore; cleared on logout). `ObserveAgentAvailabilityUseCase` = `FeatureFlag.AGENT` enabled **and** the cached permission — it drives the bottom-bar orb, and home refreshes the permission each time it is shown. Prompts read the token from the store; an expired token is refreshed once by `SendAgentPromptUseCase`.
- **Screen.** A refusal shows the reason and creates no conversation; a failed check leaves the chat offline until Retry checks again; Cancel stops polling and calls `request/cancel`; the mic follows `canSendVoice`.
- **Links.** Buttons go through `LocalDeepLinkHandler`, so the flag is checked when tapped. The old `AgentDestination` ids are gone.
- **Still a fake.** `RemoteModule` binds `AgentRemoteDataSourceFakeImpl`; the real implementation is kept in step but not switched on.

## 1. Form generation without duplication (SDUI and embedded items)

**Problem in the old version:** many use cases needed data collected through a form, which meant a feature (say, the marriage grant request) was implemented twice — once natively and once inside the AI flow. That produced a lot of boilerplate and made multi-step handling difficult.

**Approach in KMP:**

- **Server-Driven UI (SDUI):** a dynamic shared form model lives in the domain layer of the shared project, describing field structure and steps.
- **Embedding in chat:** with Compose Multiplatform, the same form used in the feature screens is rendered directly inside the chat bubble.
- Business logic and validation are handled once in a shared ViewModel or UseCase, so the form component is fed by the same source and the same logic whether it appears in chat or on its own screen.

## 2. Modern streams instead of traditional polling

**Problem in the old version:** `while` loops with `delay` in the repository, which made error handling and lifecycle management awkward.

**Approach in KMP:**

- Use **Kotlin Flows** (`flow {}`) for reactive polling.
- Operators such as `retryWhen`, `delay` and `takeWhile` make polling cleaner, more testable and free of memory leaks when tied to a coroutine scope.
- *Forward-looking:* the architecture is arranged so that if the backend later offers Server-Sent Events or WebSockets, only the data layer changes and the Flow exposed to the UI stays the same.

## 3. Dynamic binding to `menu.json`

**Problem in the old version:** actions returned by the AI were kept as hardcoded enums, and checking service availability was difficult.

**Approach in KMP:**

- Instead of fixed keys, the AI can address actions by ids matching `menu.json`.
- `ActionDispatcher` maps the incoming id to a `FeatureFlag`.
- Before running any service in chat, its status is checked through `FeatureManager` — the same component the main app uses (`status == ACTIVE`). If the service is disabled or not permitted for this user (for example a `showRole` restriction), the app shows the appropriate message from `menu.json` instead of a raw server error. See [[Feature-Flags]].

## 4. Shared form models

**Problem in the old version:** the form data models in chat and in the main feature were different.

**Approach in KMP:**

- All form contracts and data classes move to the shared domain package.
- The exact model the main app's form works with is passed as the payload processed by the AI, so the backend business logic is identical for both and no extra parsing code is needed.

## 5. Wrapper pattern for model alignment

**Problem in the old version:** separating presentation models had made the architecture messy.

**Approach in KMP:**

- Use a generic wrapper, `ChatMessage<T>`, where `T` is the shared domain model.
- **Example:** the history web service returns `InsuranceHistoryModel`. Chat wraps it as `ChatMessage<InsuranceHistoryModel>`.
- In the Compose layer, a dispatch pattern (a simple `when` or a dispatcher composable) recognizes the type and embeds the same card component built for the history feature itself. This keeps the architecture clean and makes new features fast to add.

## 6. Multi-modal messaging

**Problem in the old version:** the focus was only on text and simple voice.

**Approach in KMP:** the chat data structure is flexible from the start so every format can be supported later:

```kotlin
enum class MediaType { TEXT, IMAGE, VIDEO, AUDIO, DOCUMENT }

data class MessageAttachment(
    val uri: String,
    val mediaType: MediaType,
    val metadata: Map<String, String>? = null
)

data class AiChatMessage(
    val id: String,
    val content: String?,                        // for text
    val attachments: List<MessageAttachment>     // supports N attachments
)
```

Both the user and the Agent can send and receive a mix of image, voice, form and text. Chat bubbles render from the `attachments` list, so image, video and document support comes naturally.

## 7. Dynamic status card and chat animations (Extension Card)

**Requirement:** show the AI's thinking and processing steps transparently (similar to ChatGPT) in a card attached under the chat bubble, with smooth, polished animation.

**Approach in the UI/Compose layer:**

- **Step-by-step status:** a model of the live processing state, updated through a Flow from the backend or local logic. Steps can include:
  1. sending request…
  2. processing by the AI…
  3. response received
  4. requesting data from Social Security…
  5. final processing (with a progress bar on the active step)

```kotlin
data class AgentProcessingState(
    val steps: List<String>,
    val currentActiveIndex: Int,
    val isCompleted: Boolean
)
```

- **Extension card:** a secondary component under the chat bubble renders the step list with an animated indicator next to the running step.
- **Animations:** when the process finishes (`isCompleted == true`), the card is hidden with `AnimatedVisibility` combining `slideOutVertically` (upward, behind the parent card) and `fadeOut`. At the same time the final answer appears on the parent card with `fadeIn` plus a character-by-character typing effect.

## 8. Chained actions and step dependencies

**Problem in the old version:** given the AI output structure (see `agent_response.json`), the `entities` list often carries values such as `step_number` and filter arrays inside `payload` (e.g. `educationCode:1234567890`). The system must handle sequential execution and dependencies between use cases automatically — calling one service only after another service or a form has produced its data.

**Approach in KMP:**

- **Pipeline / Action Coordinator:** a coordinator class manages the operation pipeline. When a list of actions arrives from the AI, they are sorted by `step_number`.
- **Session context:** a temporary shared state is kept per chat session in the domain layer. When step 1 (say, fetching identity information) runs, its results are stored in that context.
- **Dynamic payload injection:** when step 2 needs a specific value in its payload filter (a child's national id, an `educationCode`), the coordinator reads it from the previous step's context and injects it into the request.
- **Pause and resume:** if a step needs user input (for example filling in the maternity allowance form), the pipeline pauses on that step and shows the form capsule in chat. As soon as the user submits, the context is updated and the remaining steps resume automatically.
- **Visual sync:** this binds directly to the Extension Card from section 7 — each pipeline stage pushes a new state to the UI so the progress bar advances and the corresponding step activates.

Related: [[AI-Agent-API-Contract]] · [[Feature-Flags]] · [[Modules]]
