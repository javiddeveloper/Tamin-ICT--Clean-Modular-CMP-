package com.tamin.taminhamrah.feature.settings.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.FontSizeOption

@Immutable
data class SettingsUiState(
    val fontSize: FontSizeOption = FontSizeOption.MEDIUM,
    val isFollowSystem: Boolean = false,
) {
    sealed interface PartialState {
        data class SetFontSize(val fontSize: FontSizeOption) : PartialState
        data class SetFollowSystem(val isFollowSystem: Boolean) : PartialState
        data object NoOp : PartialState
    }
}

sealed interface SettingsIntent {
    data class ToggleNightMode(val isDark: Boolean) : SettingsIntent
    data class ToggleFollowSystem(val enabled: Boolean) : SettingsIntent
    data class SelectFontSize(val option: FontSizeOption) : SettingsIntent
    data class UpdateFontSize(val fontSize: FontSizeOption) : SettingsIntent
    data class UpdateFollowSystem(val isFollowSystem: Boolean) : SettingsIntent
    data object OnBackClicked : SettingsIntent
}

sealed interface SettingsEvent {
    data object NavigateBack : SettingsEvent
}
