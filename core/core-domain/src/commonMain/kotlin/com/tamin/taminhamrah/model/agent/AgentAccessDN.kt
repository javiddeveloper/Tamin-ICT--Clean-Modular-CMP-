package com.tamin.taminhamrah.model.agent

/**
 * The last answer the server gave to "may this user chat with the assistant?".
 *
 * Kept across launches so the assistant's entry point can be shown or hidden without waiting on
 * the network, the same way the native app did. A refusal is stored too: it is what hides the
 * entry point. A transport failure never overwrites it.
 */
data class AgentAccessDN(
    val canStartChat: Boolean,
    val canSendVoice: Boolean,
    val chatToken: String?,
    val errorMessage: String?,
)
