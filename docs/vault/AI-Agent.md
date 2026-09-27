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
- **History.** Saved conversations open in `ChatHistoryDrawer` (`feature/agent/.../ui/ChatHistoryDrawer.kt`), a right-edge drawer over the chat (not a bottom sheet), drawn with the same `AgentGlass` frosted glass as the bars. It is always composed and driven by `isHistoryVisible` so the close animation plays; the `HazeState` therefore lives in `AgentContent`, not `ChatLayout`. Because the app is hardcoded RTL, the drawer uses `AbsoluteAlignment` / `AbsoluteRoundedCornerShape` and raw-pixel `slideInHorizontally` — `Alignment.End` would put it on the *left*. Rename/delete prompts are plain `Dialog`s with a glass card, since Material `AlertDialog` picks up the light theme over the dark backdrop.
- **Links.** Buttons go through `LocalDeepLinkHandler`, so the flag is checked when tapped. The old `AgentDestination` ids are gone.
- **Backend.** `RemoteModule` binds `AgentRemoteDataSourceSelector`: the real `AgentRemoteDataSourceImpl`, unless Developer Options puts the fixture-backed `AgentRemoteDataSourceFakeImpl` in front of it — see [[#Mock mode (Developer Options)]].

## 0.1 Mock mode (Developer Options)

**Developer Options → «شبیه‌سازی دستیار هوشمند»** answers the assistant's API from local fixtures, so every bubble the app can draw and every way a request can end are reviewable without a backend. It mirrors the payment mock ([[Payments]]): an `AgentMockMode` stored by `DeveloperOptionsRepository` (always `DISABLED` in a release build), read **per call** by the selector, so switching takes effect on the next check or prompt. The screen's «باز کردن دستیار هوشمند» entry opens the assistant directly — the access modes hide its normal entry point (a refused user has no orb).

| Mode | `chat-allowed` |
|---|---|
| غیرفعال | the real server |
| پاسخ‌های ساختگی | allowed, voice on |
| پاسخ‌های ساختگی، بدون پیام صوتی | allowed, `canSendVoice: false` — no microphone |
| عدم دسترسی | `canStartChat: false` with a reason → the refusal screen (reopen the assistant to see it) |
| آفلاین | the call throws → offline banner, sending disabled, Retry re-checks |

With chat allowed, a prompt picks an `AgentMockScenario` by keyword (`AgentRemoteDataSourceFakeImpl`); **a prompt with no keyword returns every fixture in one answer** — `FAKE_AGENT_MARKDOWN_RESPONSE` (server-rendered markdown: headings, lists, wide table, formulas, every link outcome) + `FAKE_AGENT_SHOWCASE_RESPONSE` (one of every payload type) + `FAKE_AGENT_EDGE_CASES_RESPONSE` (pie chart, non-retryable error, a throwing service, unknown payload type, unknown key with link items, `law`/`appoinmet`/`message` items, screen entry key, broken image, media without waveform/poster, short table row, divider row, long text, standalone suggestions). Send «راهنما» for the list in chat:

| Keyword | Outcome |
|---|---|
| `راهنما` / `help` | the scenario list as a markdown bubble |
| `خطا` / `failed` | `FAILED` with a server message → error bubble with retry + toast |
| `خطای بی‌پیام` / `failed-silent` | `FAILED` without a message → the client's generic text |
| `تایم‌اوت` / `timeout` | `PENDING` on all 5 polls → generic failure |
| `آهسته` / `slow` | `PENDING` twice, then a short answer (attempt counter moves) |
| `لغو` / `cancel` | `CANCEL` |
| `قطع` / `network` | the track call throws → generic failure |
| `توکن` / `token` | first send raises `ChatTokenExpiredException`; `SendAgentPromptUseCase` re-checks and resends |
| `خالی` / `empty` | `DONE` with no entities |
| `مارک‌داون` / `markdown`, `ویترین` / `showcase`, `حالت لبه` / `edge` | one fixture only |
| `سرویس‌ها` / `services` | `FAKE_AGENT_ONE_RESPONSE`: every client-service key — these call the app's own use cases, so a real login is needed |

A voice prompt with no text is acknowledged with the file name and size (no transcription). The showcase's `error` payload takes `"retryable": false` for the no-retry shape, and `"type": "throw"` makes the service fail so the dispatcher's error path (retry re-runs the service) is reachable. The one path a fixture cannot reach is `AgentServiceResult.FeatureDisabled`: every key's flag is active in `MockMenuData`, so it needs a disabled menu row.

Fixtures are decoded by `AgentFakeDataTest` with the app's `Json`, which also pins the keyword routing and the token flow.

## 0.2 Colours — one palette, no theme

The assistant is the app's one screen that does **not** follow the light/dark theme. It always
draws on `AgentBackground`, a fixed dark navy/violet composition, so every colour it uses is
absolute. Three objects hold all of them:

| Object | File | Holds |
|---|---|---|
| `AgentPalette` | `ui/AgentColors.kt` | The raw hues — **the only place a hex literal may appear in this feature**. Re-skinning is an edit to this object. |
| `AgentColors` | `ui/AgentColors.kt` | What each hue is *for*: backdrop stops, blob tints, orb corners, user-bubble gradient, status, chart, composer. Palette entry + alpha, never a fresh literal. |
| `AgentGlass` | `ui/AgentGlass.kt` | The frosted-surface roles (sheen, border, tiles, text on glass) plus the `Modifier` helpers. Sources from `AgentPalette`. |

Geometry stays where it was: `AgentBackground.kt` and `AgentOrb.kt` still own their Figma-derived
ramps and blob positions, and now pull only the colours from `AgentColors`.

**Reading `LocalTaminColors` or `MaterialTheme.colorScheme` in this feature is a bug**, even when it
looks right today. The whole feature was migrated off them in September 2026 because:

- `taminTopAppBarGradient()` resolves to `profileGradientStops`, which is navy (`#173D7E → #1F4FA3`)
  in light and teal (`#10AEB9 → #1E6FD0`) in dark. `UserBubbleCard` was built on it, so the user's
  chat bubble changed colour with the system theme while the backdrop behind it did not. It is now
  frozen to the light theme's brand navy as `AgentColors.bubbleGradient`.
- `dangerBg` is `TaminLightSurface` in light — a near-white block on a dark backdrop. `OfflineBanner`
  now uses the purpose-built `AgentGlass.danger*` tokens instead.
- `blueText` / `greenText` / `chevron` and the `*Bg` tints are tuned for a white page and read as dark
  smears here. Two call sites had already been hand-patched to white with a comment saying exactly that.

Tokens that happened to be identical in both themes (`onGradient`, `buttonGradient`, `aiAssistantTint`)
were frozen too — they were one edit to `SemanticColors.kt` away from breaking this screen from a distance.

`AgentColors.orbCore` is *computed* as the mean of `orbCorners` rather than pinned to `#7A6FDB`, so a
palette change keeps the orb's blurred centre consistent instead of leaving a stale dot at the
most-looked-at pixel on the screen.

Call sites keep their own alphas — `AgentColors.ink.copy(alpha = 0.14f)` and the like. Those are depth
decisions, not palette ones, and there are nineteen distinct white alphas; naming each would have been
a rename, not a design system. Only the base hue lives in the palette.

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


## 9. Switching conversations while an answer is in flight

Tapping **new chat** (top bar or history drawer) or opening a saved chat while a prompt is
still being answered does **not** cancel the request. The in-flight flow in
`AgentViewModel.handleSendPrompt` *detaches* instead:

- `conversationEpoch` is bumped by `detachInFlightGeneration()` on every conversation switch.
  The prompt captured the epoch it started with, so `isDetached()` is a race-free test for
  "the chat I was answering is no longer on screen". Comparing `cacheSessionId` would race —
  the ids are swapped several suspension points into the switch, and `BaseViewModel` merges
  intents with `flatMapMerge`, so the old flow can emit in between.
- Once detached, every UI emission goes through `emitUi`/`sendUiEvent` and is dropped, while
  `cacheBubble(item, targetSessionId = originSessionId)` and the session-context update keep
  writing into the conversation the prompt was asked in. The answer is therefore complete when
  that chat is reopened from history.
- The detached flow releases `generationJob`, so a **Stop** tap in the new chat cannot kill it,
  and skips the typing/step animation delays since nothing is on screen.
- `sessionContext` is *replaced* (not `clear()`ed) on a switch, so the detached pipeline keeps
  the chained-action context (section 8) it started with.
- Deleting the conversation on screen is the one case that really cancels: the answer would
  have nowhere to land, so `handleDeleteSession` runs the full `handleCancelGeneration()` path.

Known limitation: a detached answer is not re-attached if the user reopens that chat while it
is still running — the bubbles already cached are shown, the rest only appear on the next open.

Before this, the late answer leaked into whatever chat was on screen: the user saw "processing"
stuck in a brand-new empty chat and the old conversation's bubbles trickling into it.

Related: [[AI-Agent-API-Contract]] · [[Feature-Flags]] · [[Modules]]
