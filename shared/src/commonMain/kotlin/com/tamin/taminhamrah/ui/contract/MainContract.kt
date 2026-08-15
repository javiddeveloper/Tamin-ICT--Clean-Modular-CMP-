package com.tamin.taminhamrah.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.DarkThemeConfig
import com.tamin.taminhamrah.model.FontSizeOption

@Immutable
data class MainUiState(
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val fontSizeOption: FontSizeOption = FontSizeOption.MEDIUM,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false
) {
    sealed class PartialState {
        data class SetDarkThemeConfig(val config: DarkThemeConfig) : PartialState()
        data class SetFontSizeOption(val fontSizeOption: FontSizeOption) : PartialState()
        data class SetLoginStatus(val isLoggedIn: Boolean) : PartialState()
        data class SetAuthProcessing(val isProcessing: Boolean) : PartialState()
        data object Loading : PartialState()
    }
}

sealed class MainIntent {
    data class UpdateDarkThemeConfig(val config: DarkThemeConfig) : MainIntent()
    data class UpdateFontSizeOption(val fontSizeOption: FontSizeOption) : MainIntent()
    data object Login : MainIntent()
    data class SetAuthStatus(val isLoggedIn: Boolean) : MainIntent()
    data class SetAuthProcessing(val isProcessing: Boolean) : MainIntent()
}

sealed class MainEvent {
    data class OpenUrl(val url: String) : MainEvent()
}
