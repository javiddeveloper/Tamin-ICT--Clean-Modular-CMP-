package com.tamin.taminhamrah.feature.developerOptions.debugLogin.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.developerOptions.debugLogin.BackToBackCredentials

@Immutable
data class DebugLoginUiState(
    val clientId: String = BackToBackCredentials.CLIENT_ID,
    val clientSecret: String = BackToBackCredentials.CLIENT_SECRET,
    val isLoading: Boolean = false,
    val statusText: String? = null,
) {
    sealed interface PartialState {
        data class SetClientId(val value: String) : PartialState
        data class SetClientSecret(val value: String) : PartialState
        data object LoginStarted : PartialState
        data class LoginFinished(val statusText: String) : PartialState
    }
}

sealed interface DebugLoginIntent {
    data class OnClientIdChanged(val value: String) : DebugLoginIntent
    data class OnClientSecretChanged(val value: String) : DebugLoginIntent
    data object OnLoginClicked : DebugLoginIntent
    data object OnBackClicked : DebugLoginIntent
}

sealed interface DebugLoginEvent {
    data object NavigateBack : DebugLoginEvent
}
