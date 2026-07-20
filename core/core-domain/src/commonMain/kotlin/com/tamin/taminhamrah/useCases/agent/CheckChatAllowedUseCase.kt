package com.tamin.taminhamrah.useCases.agent

import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import com.tamin.taminhamrah.repository.AgentRepository

/**
 * Checks if the user is authorized to use the AI chatbot.
 *
 * Should be called before displaying the chat screen.
 * If not authorized, [ChatAllowedDN.errorMessage] will contain the message.
 */
class CheckChatAllowedUseCase(
    private val agentRepository: AgentRepository
) {
    suspend operator fun invoke(): Result<ChatAllowedDN> =
        agentRepository.checkChatAllowed()
}
