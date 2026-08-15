package com.tamin.taminhamrah.feature.settings.ui.contract

import androidx.compose.runtime.Immutable

enum class FontSizeOption {
    SMALL,
    MEDIUM,
    LARGE,
}

@Immutable
data class SettingsUiState(
    val fontSize: FontSizeOption = FontSizeOption.MEDIUM,
) {
    sealed interface PartialState {
        data class SetFontSize(val fontSize: FontSizeOption) : PartialState
        data object NoOp : PartialState
    }
}

sealed interface SettingsIntent {
    data class ToggleNightMode(val isDark: Boolean) : SettingsIntent
    data class SelectFontSize(val option: FontSizeOption) : SettingsIntent
    data object OnBackClicked : SettingsIntent
}

sealed interface SettingsEvent {
    data object NavigateBack : SettingsEvent
}
