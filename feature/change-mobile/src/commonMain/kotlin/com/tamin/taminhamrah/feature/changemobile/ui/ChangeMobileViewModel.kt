package com.tamin.taminhamrah.feature.changemobile.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.changemobile.ui.contract.ChangeMobileUiState
import com.tamin.taminhamrah.feature.changemobile.ui.contract.ChangeMobileUiState.PartialState
import com.tamin.taminhamrah.feature.changemobile.ui.contract.ChangeMobileIntent
import com.tamin.taminhamrah.feature.changemobile.ui.contract.ChangeMobileEvent
import com.tamin.taminhamrah.useCases.user.ChangeMobileUseCase
import com.tamin.taminhamrah.useCases.user.GetUserProfileUseCase
import com.tamin.taminhamrah.useCases.user.VerifyChangeMobileUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class ChangeMobileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val changeMobileUseCase: ChangeMobileUseCase,
    private val verifyChangeMobileUseCase: VerifyChangeMobileUseCase
) : BaseViewModel<ChangeMobileUiState, PartialState, ChangeMobileEvent, ChangeMobileIntent>(
    initialState = ChangeMobileUiState()
) {

    init {
        sendIntent(ChangeMobileIntent.LoadCurrentMobile)
    }

    override fun handleIntent(intent: ChangeMobileIntent): Flow<PartialState> {
        return when (intent) {
            is ChangeMobileIntent.NewMobileChanged -> flow {
                emit(PartialState.NewMobileChanged(intent.value))
                if (intent.value.length >= 11) {
                    emit(PartialState.MobileError(false))
                }
            }
            is ChangeMobileIntent.OtpChanged -> flow { emit(PartialState.OtpChanged(intent.value)) }
            is ChangeMobileIntent.GetOtpCode -> handleGetOtpCode()
            is ChangeMobileIntent.VerifyOtp -> handleVerifyOtp()
            is ChangeMobileIntent.BackToPreviousStep -> handleBackStep()
            is ChangeMobileIntent.LoadCurrentMobile -> handleLoadCurrentMobile()
        }
    }

    private fun handleLoadCurrentMobile(): Flow<PartialState> = flow {
        getUserProfileUseCase()
            .onStart { emit(PartialState.Loading(true)) }
            .catch {
                emit(PartialState.Loading(false))
                emit(PartialState.Error(it.message))
            }
            .collect { profile ->
                emit(PartialState.Loading(false))
                profile.mobile?.let {
                    emit(PartialState.SetCurrentMobile(it))
                }
            }
    }

    private fun handleGetOtpCode(): Flow<PartialState> = flow {
        if (uiState.value.newMobile.length < 11 || !uiState.value.newMobile.startsWith("09")) {
            emit(PartialState.MobileError(true))
            return@flow
        }
        changeMobileUseCase(uiState.value.newMobile)
            .onStart { emit(PartialState.Loading(true)) }
            .catch { e ->
                emit(PartialState.Loading(false))
                emit(PartialState.Error(e.message))
            }
            .collect { response ->
                emit(PartialState.Loading(false))
                emit(PartialState.OtpHashCodeChanged(response.data?.hash ?: ""))
                emit(PartialState.ChangeStep(ChangeMobileStep.VerifyOtp))
            }
    }

    private fun handleVerifyOtp(): Flow<PartialState> = flow {
        verifyChangeMobileUseCase(uiState.value.newMobile, uiState.value.otpCode, uiState.value.otpHashCode)
            .onStart { emit(PartialState.Loading(true)) }
            .catch { e ->
                emit(PartialState.Loading(false))
                emit(PartialState.Error(e.message))
            }
            .collect {
                emit(PartialState.Loading(false))
                emit(PartialState.ChangeStep(ChangeMobileStep.Success))
            }
    }

    private fun handleBackStep(): Flow<PartialState> = flow {
        val currentStep = uiState.value.currentStep
        when (currentStep) {
            ChangeMobileStep.VerifyOtp -> emit(PartialState.ChangeStep(ChangeMobileStep.EnterMobile))
            ChangeMobileStep.Success -> emit(PartialState.ChangeStep(ChangeMobileStep.VerifyOtp))
            ChangeMobileStep.EnterMobile -> sendEvent(ChangeMobileEvent.NavigateBack)
        }
    }

    override fun reduceState(
        currentState: ChangeMobileUiState,
        partialState: PartialState
    ): ChangeMobileUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            error = null
        )
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
        is PartialState.SetCurrentMobile -> currentState.copy(
            currentMobile = partialState.mobile
        )
        is PartialState.NewMobileChanged -> currentState.copy(
            newMobile = partialState.value
        )
        is PartialState.OtpChanged -> currentState.copy(
            otpCode = partialState.value
        )
        is PartialState.OtpHashCodeChanged -> currentState.copy(
            otpHashCode = partialState.value
        )
        is PartialState.MobileError -> currentState.copy(
            isMobileError = partialState.isError
        )
        is PartialState.ChangeStep -> currentState.copy(
            currentStep = partialState.step
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
