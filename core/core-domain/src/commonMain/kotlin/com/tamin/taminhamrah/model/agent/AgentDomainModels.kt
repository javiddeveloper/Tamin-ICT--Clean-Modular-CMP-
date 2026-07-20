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
    data class Pending(val requestId: String, val etaSeconds: Int) : AgentPollingState()

    /** Processing complete and response is ready */
    data class Done(val response: AgentResponseDN) : AgentPollingState()

    /** Processing failed */
    data class Failed(val message: String) : AgentPollingState()

    /** User or system cancelled the request */
    object Cancelled : AgentPollingState()
}

/**
 * Model for the complete Agent response after polling is finished.
 */
data class AgentResponseDN(
    val sessionId: String?,
    val lastEntity: String?,
    val entities: List<AiEntityDN>,
    val message: String? = null
)

/**
 * Each Entity is an executable action in the Pipeline.
 *
 * @param action Service identifier as [AgentActionKey]
 * @param stepNumber Execution order in the pipeline (sorted ascending)
 * @param payload Data provided by AI for service execution
 * @param data Direct output (if AI has full data)
 * @param message Optional text message
 * @param itemType Display type: "button" | "key_value" | "message" | "form" | ...
 */
data class AiEntityDN(
    val action: AgentActionKey,
    val stepNumber: Int,
    val payload: JsonElement?,
    val data: JsonElement?,
    val message: String?,
    val itemType: String?
)

/**
 * Result of the chat authorization check.
 */
data class ChatAllowedDN(
    val canStartChat: Boolean,
    val chatToken: String?,
    val errorMessage: String?
)
