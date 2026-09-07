package com.tamin.taminhamrah.feature.developerOptions.debugLogin

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.developerOptions.debugLogin.contract.DebugLoginEvent
import com.tamin.taminhamrah.feature.developerOptions.debugLogin.contract.DebugLoginIntent
import com.tamin.taminhamrah.feature.developerOptions.debugLogin.contract.DebugLoginUiState
import com.tamin.taminhamrah.feature.developerOptions.debugLogin.contract.DebugLoginUiState.PartialState
import com.tamin.taminhamrah.model.auth.DebugLoginResultDN
import com.tamin.taminhamrah.useCases.auth.DebugClientCredentialsLoginUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Debug-only "back-to-back" login (client_credentials grant, no PKCE, no username/password) — a
 * trusted client authenticates itself with its own client_id/client_secret. Its token endpoint
 * is not entered here: it's derived from whatever the "سرویس احراز هویت" (ACCOUNT) entry on the
 * Developer Options screen is set to, same as the PKCE authorization_code flow in
 * [com.tamin.taminhamrah.ui.MainViewModel] — switching that one entry to a pilot/test
 * environment routes both login methods there together.
 */
class DebugLoginViewModel(
    private val debugClientCredentialsLoginUseCase: DebugClientCredentialsLoginUseCase
) : BaseViewModel<DebugLoginUiState, PartialState, DebugLoginEvent, DebugLoginIntent>(
    initialState = DebugLoginUiState()
) {

    override fun handleIntent(intent: DebugLoginIntent): Flow<PartialState> = flow {
        when (intent) {
            is DebugLoginIntent.OnClientIdChanged -> emit(PartialState.SetClientId(intent.value))
            is DebugLoginIntent.OnClientSecretChanged -> emit(PartialState.SetClientSecret(intent.value))

            DebugLoginIntent.OnBackClicked -> sendEvent(DebugLoginEvent.NavigateBack)

            DebugLoginIntent.OnLoginClicked -> {
                emit(PartialState.LoginStarted)
                val state = uiState.value
                val result = debugClientCredentialsLoginUseCase(
                    clientId = state.clientId,
                    clientSecret = state.clientSecret
                )
                emit(PartialState.LoginFinished(statusText = result.toRawStatusText()))
            }
        }
    }

    override fun reduceState(
        currentState: DebugLoginUiState,
        partialState: PartialState
    ): DebugLoginUiState = when (partialState) {
        is PartialState.SetClientId -> currentState.copy(clientId = partialState.value)
        is PartialState.SetClientSecret -> currentState.copy(clientSecret = partialState.value)
        PartialState.LoginStarted -> currentState.copy(isLoading = true, statusText = null)
        is PartialState.LoginFinished -> currentState.copy(isLoading = false, statusText = partialState.statusText)
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.LoginFinished(statusText = "خطا: $message")
}

private fun DebugLoginResultDN.toRawStatusText(): String = buildString {
    appendLine(if (isSuccess) "SUCCESS" else "FAILED")
    accessToken?.let { appendLine("access_token: $it") }
    tokenType?.let { appendLine("token_type: $it") }
    expiresIn?.let { appendLine("expires_in: $it") }
    error?.let { appendLine("error: $it") }
    errorDescription?.let { appendLine("error_description: $it") }
}.trimEnd()
