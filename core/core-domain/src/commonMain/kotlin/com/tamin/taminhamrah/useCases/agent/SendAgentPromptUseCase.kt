package com.tamin.taminhamrah.useCases.agent

import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.ChatTokenExpiredException
import com.tamin.taminhamrah.model.common.UserType
import com.tamin.taminhamrah.repository.AgentAccessStore
import com.tamin.taminhamrah.repository.AgentRepository
import com.tamin.taminhamrah.repository.TokenStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Sends a prompt to the assistant and streams its polling states.
 *
 * Every request carries the newest chat token on record and the caller's user type, both read at
 * send time rather than taken from the screen, so a token refreshed by an earlier prompt is used
 * by the next one. Who is asking ([GetAgentPersonalInfoUseCase]) is added when the caller did not
 * set it. When the server rejects the token, the chat permission is checked again once
 * and the prompt is resent with the new token; a refusal ends the prompt with the server's reason.
 */
class SendAgentPromptUseCase(
    private val agentRepository: AgentRepository,
    private val checkChatAllowed: CheckChatAllowedUseCase,
    private val agentAccessStore: AgentAccessStore,
    private val tokenStoreManager: TokenStoreManager,
    private val getPersonalInfo: GetAgentPersonalInfoUseCase,
) {
    operator fun invoke(request: AgentRequest): Flow<AgentPollingState> = flow {
        val prepared = request.copy(
            chatToken = agentAccessStore.access.value?.chatToken ?: request.chatToken,
            userType = resolveUserType(),
            personalInfo = request.personalInfo ?: getPersonalInfo(),
        )
        try {
            emitAll(agentRepository.sendPrompt(prepared))
        } catch (expired: ChatTokenExpiredException) {
            retryWithFreshToken(prepared)
        }
    }

    private suspend fun FlowCollector<AgentPollingState>.retryWithFreshToken(request: AgentRequest) {
        val refreshed = refreshLock.withLock { freshToken(staleToken = request.chatToken) }
        val token = refreshed.token
        if (token.isNullOrBlank()) {
            emit(AgentPollingState.Failed(refreshed.errorMessage))
            return
        }
        try {
            emitAll(agentRepository.sendPrompt(request.copy(chatToken = token)))
        } catch (stillExpired: ChatTokenExpiredException) {
            emit(AgentPollingState.Failed(null))
        }
    }

    /**
     * A token newer than [staleToken]. Prompts that hit the expired token together queue on the
     * lock; the first one asks the server, and the ones behind it find its token already stored and
     * use that instead of asking again.
     */
    private suspend fun freshToken(staleToken: String?): RefreshedToken {
        val stored = agentAccessStore.access.value
        if (stored != null && stored.canStartChat && !stored.chatToken.isNullOrBlank() && stored.chatToken != staleToken) {
            return RefreshedToken(token = stored.chatToken, errorMessage = null)
        }
        val allowed = checkChatAllowed().getOrNull()
        return RefreshedToken(
            token = allowed?.takeIf { it.canStartChat }?.chatToken,
            errorMessage = allowed?.errorMessage,
        )
    }

    private class RefreshedToken(val token: String?, val errorMessage: String?)

    /** The native app's rule: no session means anonymous; otherwise the stored user type. */
    private fun resolveUserType(): String {
        val userType = if (tokenStoreManager.getToken().isNullOrBlank()) {
            UserType.ANONYMOUS
        } else {
            UserType.fromNameOrNull(tokenStoreManager.getUserType()) ?: UserType.ANONYMOUS
        }
        return when (userType) {
            UserType.PENSIONER -> "PENSIONER"
            UserType.INSURED -> "INSURED"
            UserType.ANONYMOUS -> "ANONYMOUS"
            UserType.TEMPORARY -> "temporary"
        }
    }

    private companion object {
        /** One token refresh at a time, however many prompts hit an expired token together. */
        val refreshLock = Mutex()
    }
}
