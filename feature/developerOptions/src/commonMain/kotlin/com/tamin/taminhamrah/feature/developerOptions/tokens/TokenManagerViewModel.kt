package com.tamin.taminhamrah.feature.developerOptions.tokens

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.developerOptions.debugLogin.BackToBackCredentials
import com.tamin.taminhamrah.feature.developerOptions.tokens.contract.TokenCardUi
import com.tamin.taminhamrah.feature.developerOptions.tokens.contract.TokenManagerEvent
import com.tamin.taminhamrah.feature.developerOptions.tokens.contract.TokenManagerIntent
import com.tamin.taminhamrah.feature.developerOptions.tokens.contract.TokenManagerUiState
import com.tamin.taminhamrah.feature.developerOptions.tokens.contract.TokenManagerUiState.PartialState
import com.tamin.taminhamrah.model.auth.TokenSlot
import com.tamin.taminhamrah.repository.AuthRepository
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.useCases.agent.CheckChatAllowedUseCase
import com.tamin.taminhamrah.useCases.auth.DebugClientCredentialsLoginUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Debug-only token screen: hold the real login and the back-to-back login side by side, switch
 * which one the app sends, and refresh any of them by hand.
 *
 * Refresh means something different per slot, because the three tokens are issued differently:
 * the user slot trades its refresh token in, the back-to-back slot re-runs client_credentials
 * (that grant has no refresh token), and the agent slot re-asks `chat-allowed` for a chat token.
 */
class TokenManagerViewModel(
    private val tokenStoreManager: TokenStoreManager,
    private val authRepository: AuthRepository,
    private val debugClientCredentialsLogin: DebugClientCredentialsLoginUseCase,
    private val checkChatAllowed: CheckChatAllowedUseCase,
) : BaseViewModel<TokenManagerUiState, PartialState, TokenManagerEvent, TokenManagerIntent>(
    initialState = TokenManagerUiState()
) {

    init {
        sendIntent(TokenManagerIntent.Refresh)
    }

    override fun handleIntent(intent: TokenManagerIntent): Flow<PartialState> = flow {
        when (intent) {
            TokenManagerIntent.Refresh -> emit(PartialState.CardsLoaded(readCards()))

            TokenManagerIntent.OnBackClicked -> sendEvent(TokenManagerEvent.NavigateBack)

            is TokenManagerIntent.OnActivateClicked -> {
                authRepository.switchTokenSlot(intent.slot)
                emit(PartialState.StatusChanged("ACTIVE SLOT: ${intent.slot.name}"))
                emit(PartialState.CardsLoaded(readCards()))
            }

            is TokenManagerIntent.OnClearClicked -> {
                tokenStoreManager.saveToken(intent.slot, null)
                tokenStoreManager.saveRefreshToken(intent.slot, null)
                // Clearing the slot the app is authenticating with leaves it with no token at all,
                // so fall back to the real login rather than sending an empty bearer.
                if (intent.slot == tokenStoreManager.getActiveSlot() && intent.slot != TokenSlot.USER) {
                    authRepository.switchTokenSlot(TokenSlot.USER)
                }
                emit(PartialState.StatusChanged("CLEARED: ${intent.slot.name}"))
                emit(PartialState.CardsLoaded(readCards()))
            }

            is TokenManagerIntent.OnRefreshClicked -> {
                emit(PartialState.BusyChanged(intent.slot))
                emit(PartialState.StatusChanged(null))
                val status = try {
                    refresh(intent.slot)
                } catch (e: Exception) {
                    "FAILED: ${e.message ?: e.toString()}"
                }
                emit(PartialState.StatusChanged(status))
                emit(PartialState.CardsLoaded(readCards()))
                emit(PartialState.BusyChanged(null))
            }
        }
    }

    private suspend fun refresh(slot: TokenSlot): String = when (slot) {
        TokenSlot.USER ->
            if (authRepository.refreshTokenSlot(TokenSlot.USER)) {
                "REFRESHED: USER"
            } else {
                "FAILED: USER — no refresh token stored, or the auth server refused it"
            }

        // No refresh_token exists for client_credentials, so "refresh" is a fresh login with the
        // same client. It deliberately does not switch slots — use «فعال‌سازی» for that.
        TokenSlot.BACK_TO_BACK -> {
            val result = debugClientCredentialsLogin(
                clientId = BackToBackCredentials.CLIENT_ID,
                clientSecret = BackToBackCredentials.CLIENT_SECRET,
                activate = false,
            )
            if (result.isSuccess) {
                "RE-ISSUED: BACK_TO_BACK (expires_in=${result.expiresIn})"
            } else {
                "FAILED: BACK_TO_BACK — ${result.error.orEmpty()} ${result.errorDescription.orEmpty()}".trim()
            }
        }

        // The chat token is issued against whichever slot is active, so this doubles as a check
        // that the active token is accepted by the assistant's gateway.
        TokenSlot.AGENT -> checkChatAllowed().fold(
            onSuccess = { allowed ->
                tokenStoreManager.saveToken(TokenSlot.AGENT, allowed.chatToken)
                if (allowed.chatToken.isNullOrBlank()) {
                    "FAILED: AGENT — canStartChat=${allowed.canStartChat}, ${allowed.errorMessage.orEmpty()}".trim()
                } else {
                    "REFRESHED: AGENT (canStartChat=${allowed.canStartChat})"
                }
            },
            onFailure = { "FAILED: AGENT — ${it.message ?: it.toString()}" },
        )
    }

    private fun readCards() = TokenSlot.entries.map { slot ->
        TokenCardUi(
            slot = slot,
            token = tokenStoreManager.getToken(slot),
            expiry = readJwtExpiry(tokenStoreManager.getToken(slot)),
            isActive = slot != TokenSlot.AGENT && slot == tokenStoreManager.getActiveSlot(),
            canRefreshWithRefreshToken = slot == TokenSlot.USER &&
                !tokenStoreManager.getRefreshToken(slot).isNullOrBlank(),
        )
    }.toImmutableList()

    override fun reduceState(
        currentState: TokenManagerUiState,
        partialState: PartialState,
    ): TokenManagerUiState = when (partialState) {
        is PartialState.CardsLoaded -> currentState.copy(cards = partialState.cards)
        is PartialState.BusyChanged -> currentState.copy(busySlot = partialState.slot)
        is PartialState.StatusChanged -> currentState.copy(statusText = partialState.statusText)
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.StatusChanged("FAILED: $message")
}
