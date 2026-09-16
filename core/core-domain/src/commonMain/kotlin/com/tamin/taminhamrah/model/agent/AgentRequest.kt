package com.tamin.taminhamrah.model.agent

/**
 * Model for requests sent to the repository.
 *
 * @param prompt User prompt text
 * @param sessionId The local conversation id; the server keys its memory of the chat on it
 * @param lastEntity Last entity returned from the server (to maintain conversation context)
 * @param state The server's conversation state from its last answer, as the JSON it sent
 * @param history The server's conversation history from its last answer, as the JSON it sent
 * @param personalInfo Who is asking; null when the user's identity is unknown
 * @param chatToken Chatbot authentication token
 * @param isLawPrompt If true, sent to the laws endpoint
 */
data class AgentRequest(
    val prompt: String,
    val sessionId: String? = null,
    val lastEntity: String? = null,
    val state: String? = null,
    val history: String? = null,
    val personalInfo: AgentPersonalInfoDN? = null,
    val chatToken: String? = null,
    val userType: String? = null,
    val isLawPrompt: Boolean = false,
    val voiceBytes: ByteArray? = null,
    /** File name for the uploaded voice part, a WAV recording, e.g. "<uuid>.wav". */
    val voiceFileName: String? = null
) {
    val isVoice: Boolean get() = voiceBytes != null && voiceBytes.isNotEmpty()
}
