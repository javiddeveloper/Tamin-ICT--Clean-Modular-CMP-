package com.tamin.taminhamrah.feature.security.ui

import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.security.ui.contract.SecurityEvent
import com.tamin.taminhamrah.feature.security.ui.contract.SecurityIntent
import com.tamin.taminhamrah.feature.security.ui.contract.SecurityUiState
import com.tamin.taminhamrah.feature.security.ui.contract.SecurityUiState.PartialState
import com.tamin.taminhamrah.repository.UserPreferencesRepository
import com.tamin.taminhamrah.repository.BiometricSessionState
import com.tamin.taminhamrah.useCases.common.SetBiometricEnabledUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SecurityViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val setBiometricEnabledUseCase: SetBiometricEnabledUseCase,
    private val biometricSessionState: BiometricSessionState
) : BaseViewModel<SecurityUiState, PartialState, SecurityEvent, SecurityIntent>(
    initialState = SecurityUiState()
) {

    init {
        viewModelScope.launch {
            userPreferencesRepository.observeBiometricEnabled.collect { enabled ->
                sendIntent(SecurityIntent.UpdateBiometricEnabled(enabled))
            }
        }
    }

    override fun handleIntent(intent: SecurityIntent): Flow<PartialState> {
        return when (intent) {
            is SecurityIntent.OnBackClicked -> {
                sendEvent(SecurityEvent.NavigateBack)
                kotlinx.coroutines.flow.emptyFlow()
            }
            is SecurityIntent.UpdateBiometricEnabled -> flow {
                emit(PartialState.SetBiometricEnabled(intent.enabled))
            }
            is SecurityIntent.UpdateBiometricAvailability -> flow {
                emit(PartialState.SetBiometricAvailable(intent.available))
            }
            is SecurityIntent.SetBiometricEnabled -> flow {
                if (intent.enabled) {
                    biometricSessionState.markUnlocked()
                }
                setBiometricEnabledUseCase(intent.enabled)
                emit(PartialState.SetBiometricEnabled(intent.enabled))
            }
        }
    }

    override fun reduceState(
        currentState: SecurityUiState,
        partialState: PartialState
    ): SecurityUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading
        )
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
        is PartialState.SetBiometricEnabled -> currentState.copy(
            isBiometricEnabled = partialState.enabled
        )
        is PartialState.SetBiometricAvailable -> currentState.copy(
            isBiometricAvailable = partialState.available
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
