package com.tamin.taminhamrah.feature.settings.ui

import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.settings.ui.contract.SettingsEvent
import com.tamin.taminhamrah.feature.settings.ui.contract.SettingsIntent
import com.tamin.taminhamrah.feature.settings.ui.contract.SettingsUiState
import com.tamin.taminhamrah.feature.settings.ui.contract.SettingsUiState.PartialState
import com.tamin.taminhamrah.model.DarkThemeConfig
import com.tamin.taminhamrah.useCases.common.SetThemeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val setThemeUseCase: SetThemeUseCase
) : BaseViewModel<SettingsUiState, PartialState, SettingsEvent, SettingsIntent>(
    initialState = SettingsUiState()
) {

    override fun handleIntent(intent: SettingsIntent): Flow<PartialState> = when (intent) {
        is SettingsIntent.ToggleNightMode -> handleToggleNightMode(intent.isDark)
        is SettingsIntent.SelectFontSize -> flow { emit(PartialState.SetFontSize(intent.option)) }
        SettingsIntent.OnBackClicked -> flow { sendEvent(SettingsEvent.NavigateBack) }
    }

    // Mirrors ProfileViewModel.handleToggleTheme: the effective theme is read back from
    // LocalTaminColors at the composable level, so this screen keeps no dark-mode state of its own.
    private fun handleToggleNightMode(isDark: Boolean): Flow<PartialState> {
        viewModelScope.launch {
            val config = if (isDark) DarkThemeConfig.DARK else DarkThemeConfig.LIGHT
            setThemeUseCase(config)
        }
        return emptyFlow()
    }

    override fun reduceState(
        currentState: SettingsUiState,
        partialState: PartialState
    ): SettingsUiState = when (partialState) {
        is PartialState.SetFontSize -> currentState.copy(fontSize = partialState.fontSize)
        PartialState.NoOp -> currentState
    }

    override fun createErrorState(message: String): PartialState = PartialState.NoOp
}
