/*
 * author Javid Sattar *(javiddeveloper@gmail.com)
 */
package com.tamin.taminhamrah.ui

import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.repository.AuthRepository
import com.tamin.taminhamrah.repository.UserPreferencesRepository
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.repository.BiometricSessionState
import com.tamin.taminhamrah.ui.contract.MainUiState
import com.tamin.taminhamrah.ui.contract.MainUiState.PartialState
import com.tamin.taminhamrah.ui.contract.MainIntent
import com.tamin.taminhamrah.ui.contract.MainEvent
import com.tamin.taminhamrah.useCases.auth.AuthAuthorizeUrlUseCase
import com.tamin.taminhamrah.useCases.common.CompleteBiometricEnrollmentPromptUseCase
import com.tamin.taminhamrah.useCases.common.SetBiometricEnabledUseCase
import com.tamin.taminhamrah.useCases.common.SetThemeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val tokenStoreManager: TokenStoreManager,
    private val authRepository: AuthRepository,
    private val biometricSessionState: BiometricSessionState,
    private val authAuthorizeUrlUseCase: AuthAuthorizeUrlUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val completeBiometricEnrollmentPromptUseCase: CompleteBiometricEnrollmentPromptUseCase,
    private val setBiometricEnabledUseCase: SetBiometricEnabledUseCase
) : BaseViewModel<MainUiState, PartialState, MainEvent, MainIntent>(
    initialState = MainUiState(isLoading = true)
) {

    private var hasObservedInitialLoginState = false

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            userPreferencesRepository.userData.collect { userData ->
                sendIntent(MainIntent.UpdateDarkThemeConfig(userData.darkThemeConfig))
                sendIntent(MainIntent.UpdateBiometricEnabled(userData.isBiometricEnabled))
                sendIntent(MainIntent.UpdateHasAskedToEnableBiometric(userData.hasAskedToEnableBiometric))
            }
        }
        viewModelScope.launch {
            biometricSessionState.isUnlocked.collect { unlocked ->
                if (unlocked) {
                    sendIntent(MainIntent.BiometricUnlockSucceeded)
                }
            }
        }

        viewModelScope.launch {
            authRepository.isLoggedIn.collect { isLoggedIn ->
                val wasLoggedIn = uiState.value.isLoggedIn
                val isFreshLogin = hasObservedInitialLoginState && isLoggedIn && !wasLoggedIn
                hasObservedInitialLoginState = true

                sendIntent(MainIntent.SetAuthStatus(isLoggedIn))

                if (isFreshLogin &&
                    !uiState.value.isBiometricEnabled &&
                    !uiState.value.hasAskedToEnableBiometric
                ) {
                    sendEvent(MainEvent.PromptEnableBiometric)
                }
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
            is MainIntent.UpdateBiometricEnabled -> {
                emit(PartialState.SetBiometricEnabled(intent.enabled))
            }
            is MainIntent.UpdateHasAskedToEnableBiometric -> {
                emit(PartialState.SetHasAskedToEnableBiometric(intent.hasAsked))
            }
            MainIntent.BiometricUnlockSucceeded -> {
                emit(PartialState.SetBiometricUnlocked)
            }
        }
    }

    override fun reduceState(currentState: MainUiState, partialState: PartialState): MainUiState {
        return when (partialState) {
            is PartialState.SetDarkThemeConfig -> currentState.copy(
                darkThemeConfig = partialState.config
            )
            is PartialState.SetLoginStatus -> currentState.copy(
                isLoggedIn = partialState.isLoggedIn,
                isLoading = false )
            is PartialState.SetAuthProcessing -> currentState.copy(
                isLoading = partialState.isProcessing
            )
            is PartialState.SetBiometricEnabled -> currentState.copy(
                isBiometricEnabled = partialState.enabled
            )
            is PartialState.SetHasAskedToEnableBiometric -> currentState.copy(
                hasAskedToEnableBiometric = partialState.hasAsked
            )
            PartialState.SetBiometricUnlocked -> currentState.copy(
                isBiometricUnlocked = true
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

    fun resolveBiometricEnrollmentPrompt(enable: Boolean) {
        if (enable) {
            biometricSessionState.markUnlocked()
        }
        viewModelScope.launch {
            completeBiometricEnrollmentPromptUseCase(enable)
        }
    }

    fun onBiometricUnlockSucceeded() {
        biometricSessionState.markUnlocked()
    }

    fun disableBiometricAndContinue() {
        biometricSessionState.markUnlocked()
        viewModelScope.launch {
            setBiometricEnabledUseCase(false)
        }
    }
}
