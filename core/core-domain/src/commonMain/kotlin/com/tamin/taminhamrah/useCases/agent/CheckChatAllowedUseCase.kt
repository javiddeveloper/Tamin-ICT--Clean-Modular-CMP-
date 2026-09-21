package com.tamin.taminhamrah.useCases.agent

import com.tamin.taminhamrah.model.agent.AgentAccessDN
import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import com.tamin.taminhamrah.model.auth.TokenSlot
import com.tamin.taminhamrah.repository.AgentAccessStore
import com.tamin.taminhamrah.repository.AgentRepository
import com.tamin.taminhamrah.repository.TokenStoreManager

/**
 * Asks the server whether this user may chat with the assistant, and records the answer.
 *
 * Follows the native app's rules:
 * - The cached chat token is dropped before asking, so a token the server may have retired is
 *   never sent again. The cached permission stays, so the entry point does not flicker.
 * - The server's answer is stored whatever it is — a refusal is what hides the entry point.
 * - A transport failure stores nothing: the last known answer is still the best one available.
 */
class CheckChatAllowedUseCase(
    private val agentRepository: AgentRepository,
    private val tokenStoreManager: TokenStoreManager,
    private val agentAccessStore: AgentAccessStore,
) {
    suspend operator fun invoke(): Result<ChatAllowedDN> {
        agentAccessStore.clearChatToken()
        return agentRepository.checkChatAllowed().onSuccess { allowed ->
            agentAccessStore.save(
                AgentAccessDN(
                    canStartChat = allowed.canStartChat,
                    canSendVoice = allowed.canSendVoice,
                    chatToken = allowed.chatToken?.takeIf { allowed.canStartChat },
                    errorMessage = allowed.errorMessage,
                )
            )
            // Mirrored into the Developer Options slot so that screen shows the token in play.
            allowed.chatToken?.takeIf { it.isNotBlank() }?.let {
                tokenStoreManager.saveToken(TokenSlot.AGENT, it)
            }
        }
    }
}
