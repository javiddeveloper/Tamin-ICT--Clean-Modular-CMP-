package com.tamin.taminhamrah.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.DarkThemeConfig
import com.tamin.taminhamrah.model.FontSizeOption

@Immutable
data class MainUiState(
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val fontSizeOption: FontSizeOption = FontSizeOption.MEDIUM,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val hasAskedToEnableBiometric: Boolean = false,
    val isBiometricUnlocked: Boolean = false
) {
    sealed class PartialState {
        data class SetDarkThemeConfig(val config: DarkThemeConfig) : PartialState()
        data class SetFontSizeOption(val fontSizeOption: FontSizeOption) : PartialState()
        data class SetLoginStatus(val isLoggedIn: Boolean) : PartialState()
        data class SetAuthProcessing(val isProcessing: Boolean) : PartialState()
        data class SetBiometricEnabled(val enabled: Boolean) : PartialState()
        data class SetHasAskedToEnableBiometric(val hasAsked: Boolean) : PartialState()
        data object SetBiometricUnlocked : PartialState()
        data object Loading : PartialState()
    }
}

sealed class MainIntent {
    data class UpdateDarkThemeConfig(val config: DarkThemeConfig) : MainIntent()
    data class UpdateFontSizeOption(val fontSizeOption: FontSizeOption) : MainIntent()
    data object Login : MainIntent()
    data class SetAuthStatus(val isLoggedIn: Boolean) : MainIntent()
    data class SetAuthProcessing(val isProcessing: Boolean) : MainIntent()
    data class UpdateBiometricEnabled(val enabled: Boolean) : MainIntent()
    data class UpdateHasAskedToEnableBiometric(val hasAsked: Boolean) : MainIntent()
    data object BiometricUnlockSucceeded : MainIntent()
}

sealed class MainEvent {
    data class OpenUrl(val url: String) : MainEvent()
    data object PromptEnableBiometric : MainEvent()
}
