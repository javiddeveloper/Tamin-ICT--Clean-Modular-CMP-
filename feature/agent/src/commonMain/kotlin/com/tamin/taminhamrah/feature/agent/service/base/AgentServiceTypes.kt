package com.tamin.taminhamrah.feature.agent.service.base

import com.tamin.taminhamrah.model.agent.AgentActionKey
import kotlinx.serialization.json.JsonElement

/**
 * Input parameters for each Agent service use case.
 *
 * @param payload AI data for service execution (e.g., `{"year": "1402"}`)
 * @param rawData Raw output data from AI (if data is pre-prepared)
 * @param message Optional text message from AI
 * @param sessionContext Current session context for dependency injection between steps
 */
data class AgentServiceParams(
    val payload: JsonElement?,
    val rawData: JsonElement?,
    val message: String?,
    val sessionContext: AgentSessionContext,
    /**
     * The concrete action the AI requested. A handler can support several keys
     * (e.g. `_ALL` and `_LAST` variants), so it needs to know which one triggered it.
     */
    val requestedKey: AgentActionKey? = null
)

/**
 * Results of an Agent service use case execution.
 */
sealed class AgentServiceResult {
    /** Successful execution — [bubbles] is the list of items to be displayed in the UI */
    data class Success(val bubbles: List<ChatBubbleContent>) : AgentServiceResult()

    /** Service not found — no handler exists */
    object NoHandler : AgentServiceResult()

    /** This service is disabled for the current user (FeatureFlag) */
    data class FeatureDisabled(val message: String?) : AgentServiceResult()

    /** Execution error */
    data class Error(val message: String, val cause: Throwable? = null) : AgentServiceResult()
}


// Chat bubble content lives in ChatBubbleContent.kt, grouped by answer type.
