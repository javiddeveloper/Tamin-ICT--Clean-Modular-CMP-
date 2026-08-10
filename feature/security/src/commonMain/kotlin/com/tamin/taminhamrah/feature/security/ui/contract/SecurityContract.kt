package com.tamin.taminhamrah.feature.security.ui.contract

data class SecurityUiState(
    val isLoading: Boolean = false,
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState
    }
}

sealed interface SecurityIntent {
    data object OnBackClicked : SecurityIntent
}

sealed interface SecurityEvent {
    data object NavigateBack : SecurityEvent
}
