package com.tamin.taminhamrah.model.agent

/**
 * Model for requests sent to the repository.
 *
 * @param prompt User prompt text
 * @param sessionId Session identifier (null for the first session message)
 * @param lastEntity Last entity returned from the server (to maintain conversation context)
 * @param chatToken Chatbot authentication token
 * @param isLawPrompt If true, sent to the laws endpoint
 */
data class AgentRequest(
    val prompt: String,
    val sessionId: String? = null,
    val lastEntity: String? = null,
    val chatToken: String? = null,
    val isLawPrompt: Boolean = false
)
