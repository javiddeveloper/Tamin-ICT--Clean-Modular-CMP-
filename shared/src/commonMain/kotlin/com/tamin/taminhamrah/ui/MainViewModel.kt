/*
 * author Javid Sattar *(javiddeveloper@gmail.com)
 */
package com.tamin.taminhamrah.ui

import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.repository.AuthRepository
import com.tamin.taminhamrah.repository.UserPreferencesRepository
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.ui.contract.MainUiState
import com.tamin.taminhamrah.ui.contract.MainUiState.PartialState
import com.tamin.taminhamrah.ui.contract.MainIntent
import com.tamin.taminhamrah.ui.contract.MainEvent
import com.tamin.taminhamrah.useCases.auth.AuthAuthorizeUrlUseCase
import com.tamin.taminhamrah.useCases.common.SetThemeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val tokenStoreManager: TokenStoreManager,
    private val authRepository: AuthRepository,
    private val authAuthorizeUrlUseCase: AuthAuthorizeUrlUseCase,
    private val setThemeUseCase: SetThemeUseCase
) : BaseViewModel<MainUiState, PartialState, MainEvent, MainIntent>(
    initialState = MainUiState(isLoading = true)
) {

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            userPreferencesRepository.userData.collect { userData ->
                sendIntent(MainIntent.UpdateDarkThemeConfig(userData.darkThemeConfig))
                sendIntent(MainIntent.UpdateFontSizeOption(userData.fontSize))
            }
        }
        viewModelScope.launch {
            authRepository.isLoggedIn.collect { isLoggedIn ->
                sendIntent(MainIntent.SetAuthStatus(isLoggedIn))
            }
        }

        viewModelScope.launch {
            tokenStoreManager.isAuthProcessingFlow().collect { isProcessing ->
                sendIntent(MainIntent.SetAuthProcessing(isProcessing))
            }
        }
    }

    override fun handleIntent(intent: MainIntent): Flow<PartialState> = flow {
        when (intent) {
            is MainIntent.UpdateDarkThemeConfig -> {
                emit(PartialState.SetDarkThemeConfig(intent.config))
            }
            is MainIntent.UpdateFontSizeOption -> {
                emit(PartialState.SetFontSizeOption(intent.fontSizeOption))
            }
            MainIntent.Login -> {
                val url = authAuthorizeUrlUseCase()
                sendEvent(MainEvent.OpenUrl(url))
            }
            is MainIntent.SetAuthStatus -> {
                emit(PartialState.SetLoginStatus(intent.isLoggedIn))
            }
            is MainIntent.SetAuthProcessing -> {
                emit(PartialState.SetAuthProcessing(intent.isProcessing))
            }
        }
    }

    override fun reduceState(currentState: MainUiState, partialState: PartialState): MainUiState {
        return when (partialState) {
            is PartialState.SetDarkThemeConfig -> currentState.copy(
                darkThemeConfig = partialState.config
            )
            is PartialState.SetFontSizeOption -> currentState.copy(
                fontSizeOption = partialState.fontSizeOption
            )
            is PartialState.SetLoginStatus -> currentState.copy(
                isLoggedIn = partialState.isLoggedIn,
                isLoading = false )
            is PartialState.SetAuthProcessing -> currentState.copy(
                isLoading = partialState.isProcessing
            )
            PartialState.Loading -> currentState.copy(isLoading = true)
        }
    }

    override fun createErrorState(message: String): PartialState {
        return PartialState.Loading
    }

    fun updateDarkThemeConfig(config: com.tamin.taminhamrah.model.DarkThemeConfig) {
        viewModelScope.launch {
            setThemeUseCase(config)
        }
    }

    fun login() {
        sendIntent(MainIntent.Login)
    }
}
