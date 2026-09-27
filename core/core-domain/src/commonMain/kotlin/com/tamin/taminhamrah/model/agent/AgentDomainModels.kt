package com.tamin.taminhamrah.model.agent

import kotlinx.serialization.json.JsonElement

/**
 * Different states of a polling request.
 *
 * This sealed class is emitted from the repository to higher layers.
 *
 * Flow lifecycle:
 * [Pending] → repeated as needed → [Done] or [Failed] or [Cancelled]
 */
sealed class AgentPollingState {
    /** Request registered and in queue — ETA provided */
    data class Pending(
        val requestId: String,
        val etaSeconds: Int,
        val attempt: Int = 1,
        val maxAttempts: Int = 5
    ) : AgentPollingState()

    /** Processing complete and response is ready */
    data class Done(val response: AgentResponseDN) : AgentPollingState()

    /**
     * Processing failed. [message] is the server's own explanation when it gave one; the UI
     * falls back to its generic failure text when it is null.
     */
    data class Failed(val message: String? = null) : AgentPollingState()

    /** User or system cancelled the request */
    object Cancelled : AgentPollingState()
}

/**
 * Model for the complete Agent response after polling is finished.
 *
 * [state] and [history] are the server's conversation memory as the JSON it sent; the app never
 * reads them, it only sends them back with the next prompt.
 */
data class AgentResponseDN(
    val sessionId: String?,
    val lastEntity: String?,
    val entities: List<AiEntityDN>,
    val message: String? = null,
    val renderMode: AgentRenderMode = AgentRenderMode.CLIENT,
    val state: String? = null,
    val history: String? = null,
)

/**
 * Each Entity is an executable action in the Pipeline.
 *
 * @param action Service identifier as [AgentActionKey]
 * @param stepNumber Execution order in the pipeline (sorted ascending)
 * @param payload Data provided by AI for service execution
 * @param data Direct output (if AI has full data)
 * @param message Optional text message
 * @param itemType What the entity carries; [AgentItemType.MARKDOWN] means it is already rendered
 * @param markdown The non-blank markdown texts of a [AgentItemType.MARKDOWN] entity, in order
 */
data class AiEntityDN(
    val action: AgentActionKey,
    val stepNumber: Int,
    val payload: JsonElement?,
    val data: JsonElement?,
    val message: String?,
    val itemType: AgentItemType?,
    val markdown: List<String> = emptyList(),
)

/**
 * Result of the chat authorization check.
 */
data class ChatAllowedDN(
    val canStartChat: Boolean,
    val chatToken: String?,
    val errorMessage: String?,
    val canSendVoice: Boolean = false,
)

/**
 * Developer Options: stands a fake in for the assistant's backend so every answer shape and every
 * access outcome can be looked at without a server. Same guard as
 * [com.tamin.taminhamrah.model.payment.PaymentMockMode] — a release build always reads [DISABLED].
 */
enum class AgentMockMode {
    /** Talk to the real assistant. */
    DISABLED,

    /** Chat is allowed with voice; a prompt is answered from local fixtures (keywords pick a scenario). */
    RESPONSES,

    /** As [RESPONSES], but the server says voice prompts are not allowed (microphone hidden). */
    NO_VOICE,

    /** `chat-allowed` refuses this user, with a reason — the screen shows the refusal. */
    ACCESS_DENIED,

    /** `chat-allowed` cannot be reached — the screen opens offline on the cached conversation. */
    OFFLINE,
}
