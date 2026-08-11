package com.tamin.taminhamrah.feature.security.ui.contract

data class SecurityUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isBiometricEnabled: Boolean = false,
    val isBiometricAvailable: Boolean = false
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState
        data class SetBiometricEnabled(val enabled: Boolean) : PartialState
        data class SetBiometricAvailable(val available: Boolean) : PartialState
    }
}

sealed interface SecurityIntent {
    data object OnBackClicked : SecurityIntent
    data class UpdateBiometricEnabled(val enabled: Boolean) : SecurityIntent
    data class UpdateBiometricAvailability(val available: Boolean) : SecurityIntent
    data class SetBiometricEnabled(val enabled: Boolean) : SecurityIntent
}

sealed interface SecurityEvent {
    data object NavigateBack : SecurityEvent
}
