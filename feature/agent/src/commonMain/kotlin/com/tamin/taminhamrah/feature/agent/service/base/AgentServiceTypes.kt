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
    val sessionContext: AgentSessionContext
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
    data class FeatureDisabled(val message: String) : AgentServiceResult()

    /** Execution error */
    data class Error(val message: String, val cause: Throwable? = null) : AgentServiceResult()
}

/**
 * Content of a chat bubble.
 *
 * This sealed class represents the content of each chat item without UI information.
 * The Compose layer renders it based on its type.
 */
sealed class ChatBubbleContent {
    /** Plain text message */
    data class Text(val message: String) : ChatBubbleContent()

    /** List of key-value pairs (e.g., history details) */
    data class KeyValue(
        val title: String?,
        val items: List<Pair<String, String>>
    ) : ChatBubbleContent()

    /** Internal link to an app destination */
    data class DeepLink(
        val title: String,
        val destination: String
    ) : ChatBubbleContent()

    /** Web link */
    data class WebLink(
        val title: String,
        val url: String
    ) : ChatBubbleContent()

    /** Suggested prompts for the next interaction */
    data class SuggestedPrompts(
        val prompts: List<String>
    ) : ChatBubbleContent()

    /** Agent processing steps (Extension Card) to be shown inline in chat history */
    data class ProcessingSteps(
        val steps: List<String>,
        val currentActiveIndex: Int,
        val isCompleted: Boolean
    ) : ChatBubbleContent()

    /** Voice message (can be a local file path from the user or a remote URL from AI) */
    data class Voice(
        val path: String,
        val durationMs: Long? = null
    ) : ChatBubbleContent()

    /** Image response */
    data class Image(
        val url: String,
        val caption: String? = null
    ) : ChatBubbleContent()

    /** Chart representation (passes the chart domain model to be rendered in UI) */
    data class Chart(
        val chartModel: Any
    ) : ChatBubbleContent()

    /** Dynamic Form representation to be embedded inside the chat */
    data class DynamicForm(
        val formModel: Any
    ) : ChatBubbleContent()

    /** Embedded Feature Component for complex UIs (e.g., WageHistoryModel) */
    data class EmbeddedModel(val model: Any) : ChatBubbleContent()

    /** Error in service execution */
    data class ServiceError(val message: String) : ChatBubbleContent()
}
