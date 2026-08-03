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
    val isLawPrompt: Boolean = false,
    /** Raw bytes of a voice recording to upload as a multipart "file" part (null for text). */
    val voiceBytes: ByteArray? = null,
    /** File name for the uploaded voice part, e.g. "voice.m4a". */
    val voiceFileName: String? = null
)
