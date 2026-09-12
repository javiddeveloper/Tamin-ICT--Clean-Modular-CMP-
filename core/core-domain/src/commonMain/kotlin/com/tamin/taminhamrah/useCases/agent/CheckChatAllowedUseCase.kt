package com.tamin.taminhamrah.useCases.agent

import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import com.tamin.taminhamrah.model.auth.TokenSlot
import com.tamin.taminhamrah.repository.AgentRepository
import com.tamin.taminhamrah.repository.TokenStoreManager

/**
 * Checks if the user is authorized to use the AI chatbot.
 *
 * Should be called before displaying the chat screen.
 * If not authorized, [ChatAllowedDN.errorMessage] will contain the message.
 */
class CheckChatAllowedUseCase(
    private val agentRepository: AgentRepository,
    private val tokenStoreManager: TokenStoreManager,
) {
    suspend operator fun invoke(): Result<ChatAllowedDN> =
        agentRepository.checkChatAllowed().onSuccess { allowed ->
            // Kept only so Developer Options can show the chat token that is actually in play —
            // the agent screen still drives itself from its own in-memory copy, and the retry
            // inside AiChatTokenPlugin fetches its replacement without coming through here, so a
            // token refreshed mid-conversation reaches the slot on the next check rather than at
            // once.
            allowed.chatToken?.takeIf { it.isNotBlank() }?.let {
                tokenStoreManager.saveToken(TokenSlot.AGENT, it)
            }
        }
}
