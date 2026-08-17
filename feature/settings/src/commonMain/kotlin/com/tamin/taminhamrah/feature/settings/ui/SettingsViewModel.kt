package com.tamin.taminhamrah.feature.settings.ui

import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.settings.ui.contract.SettingsEvent
import com.tamin.taminhamrah.feature.settings.ui.contract.SettingsIntent
import com.tamin.taminhamrah.feature.settings.ui.contract.SettingsUiState
import com.tamin.taminhamrah.feature.settings.ui.contract.SettingsUiState.PartialState
import com.tamin.taminhamrah.model.DarkThemeConfig
import com.tamin.taminhamrah.model.FontSizeOption
import com.tamin.taminhamrah.repository.UserPreferencesRepository
import com.tamin.taminhamrah.useCases.common.SetFontSizeUseCase
import com.tamin.taminhamrah.useCases.common.SetThemeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val setThemeUseCase: SetThemeUseCase,
    private val setFontSizeUseCase: SetFontSizeUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<SettingsUiState, PartialState, SettingsEvent, SettingsIntent>(
    initialState = SettingsUiState()
) {

    init {
        viewModelScope.launch {
            userPreferencesRepository.observeFontSize.collect { fontSize ->
                sendIntent(SettingsIntent.UpdateFontSize(fontSize))
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.observeDarkThemeConfig.collect { config ->
                sendIntent(SettingsIntent.UpdateFollowSystem(config == DarkThemeConfig.FOLLOW_SYSTEM))
            }
        }
    }

    override fun handleIntent(intent: SettingsIntent): Flow<PartialState> = when (intent) {
        is SettingsIntent.ToggleNightMode -> handleToggleNightMode(intent.isDark)
        is SettingsIntent.ToggleFollowSystem -> handleToggleFollowSystem(intent.enabled)
        is SettingsIntent.SelectFontSize -> handleSelectFontSize(intent.option)
        is SettingsIntent.UpdateFontSize -> flow { emit(PartialState.SetFontSize(intent.fontSize)) }
        is SettingsIntent.UpdateFollowSystem -> flow { emit(PartialState.SetFollowSystem(intent.isFollowSystem)) }
        SettingsIntent.OnBackClicked -> flow { sendEvent(SettingsEvent.NavigateBack) }
    }
    private fun handleToggleNightMode(isDark: Boolean): Flow<PartialState> {
        viewModelScope.launch {
            val config = if (isDark) DarkThemeConfig.DARK else DarkThemeConfig.LIGHT
            setThemeUseCase(config)
        }
        return emptyFlow()
    }
    private fun handleToggleFollowSystem(enabled: Boolean): Flow<PartialState> {
        viewModelScope.launch {
            val config = if (enabled) DarkThemeConfig.FOLLOW_SYSTEM else DarkThemeConfig.LIGHT
            setThemeUseCase(config)
        }
        return emptyFlow()
    }
    private fun handleSelectFontSize(option: FontSizeOption): Flow<PartialState> {
        viewModelScope.launch { setFontSizeUseCase(option) }
        return emptyFlow()
    }

    override fun reduceState(
        currentState: SettingsUiState,
        partialState: PartialState
    ): SettingsUiState = when (partialState) {
        is PartialState.SetFontSize -> currentState.copy(fontSize = partialState.fontSize)
        is PartialState.SetFollowSystem -> currentState.copy(isFollowSystem = partialState.isFollowSystem)
        PartialState.NoOp -> currentState
    }

    override fun createErrorState(message: String): PartialState = PartialState.NoOp
}
