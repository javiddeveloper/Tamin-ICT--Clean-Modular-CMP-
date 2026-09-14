package com.tamin.taminhamrah.feature.fractionContract.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractEvent
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractIntent
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractState
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractState.PartialState
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractStep
import com.tamin.taminhamrah.mapper.contracts.toPresentation
import com.tamin.taminhamrah.mapper.fractionContract.toPresentation
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.contracts.GetRegistrationInfoUseCase
import com.tamin.taminhamrah.useCases.fractionContract.CheckFractionAgeAndHistoryUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class FractionContractViewModel(
    private val getRegistrationInfoUseCase: GetRegistrationInfoUseCase,
    private val checkFractionAgeAndHistoryUseCase: CheckFractionAgeAndHistoryUseCase,
) : BaseViewModel<FractionContractState, PartialState, FractionContractEvent, FractionContractIntent>(
    initialState = FractionContractState(),
) {

    override fun handleIntent(intent: FractionContractIntent): Flow<PartialState> =
        handleIntentInternal(intent).catch { e ->
            sendEvent(FractionContractEvent.ShowToast(e.toSingleLineMessage()))
            emit(createErrorState(e.toSingleLineMessage()))
        }

    private fun handleIntentInternal(intent: FractionContractIntent): Flow<PartialState> = flow {
        when (intent) {
            FractionContractIntent.InitData -> loadInitialData()
            FractionContractIntent.OnBackClicked -> handleBack()
            FractionContractIntent.OnGuideClicked -> emit(PartialState.GuideDialogVisibility(true))
            FractionContractIntent.DismissGuideDialog -> emit(PartialState.GuideDialogVisibility(false))
            FractionContractIntent.OnNextStepClicked -> goNext()
            FractionContractIntent.OnPreviousStepClicked -> goPrevious()
            is FractionContractIntent.SetRulesConfirmed ->
                emit(PartialState.RulesConfirmedChanged(intent.confirmed))
        }
    }

    private suspend fun FlowCollector<PartialState>.loadInitialData() {
        emit(PartialState.Loading(true))
        val registrationInfo = getRegistrationInfoUseCase().first().toPresentation()
        val eligibility = checkFractionAgeAndHistoryUseCase().first()?.toPresentation()
        emit(
            PartialState.DataLoaded(
                registrationInfo = registrationInfo,
                eligibility = eligibility,
            ),
        )
    }

    private suspend fun FlowCollector<PartialState>.handleBack() {
        val step = uiState.value.currentStep
        if (step == FractionContractStep.Eligibility) {
            sendEvent(FractionContractEvent.NavigateBack)
        } else {
            goPrevious()
        }
    }

    private suspend fun FlowCollector<PartialState>.goNext() {
        val state = uiState.value
        when (state.currentStep) {
            FractionContractStep.Eligibility -> if (!state.isEligible) return
            FractionContractStep.Terms -> if (!state.isRulesConfirmed) return
            FractionContractStep.UserInfo,
            FractionContractStep.Submit,
            -> Unit
        }
        val next = when (state.currentStep) {
            FractionContractStep.Eligibility -> FractionContractStep.Terms
            FractionContractStep.Terms -> FractionContractStep.UserInfo
            FractionContractStep.UserInfo -> FractionContractStep.Submit
            FractionContractStep.Submit -> return
        }
        emit(PartialState.StepChanged(next))
    }

    private suspend fun FlowCollector<PartialState>.goPrevious() {
        val previous = when (uiState.value.currentStep) {
            FractionContractStep.Eligibility -> return
            FractionContractStep.Terms -> FractionContractStep.Eligibility
            FractionContractStep.UserInfo -> FractionContractStep.Terms
            FractionContractStep.Submit -> FractionContractStep.UserInfo
        }
        emit(PartialState.StepChanged(previous))
    }

    override fun reduceState(
        currentState: FractionContractState,
        partialState: PartialState,
    ): FractionContractState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.DataLoaded -> currentState.copy(
            isLoading = false,
            registrationInfo = partialState.registrationInfo,
            eligibility = partialState.eligibility,
            error = null,
        )
        is PartialState.StepChanged -> currentState.copy(currentStep = partialState.step)
        is PartialState.RulesConfirmedChanged -> currentState.copy(isRulesConfirmed = partialState.confirmed)
        is PartialState.GuideDialogVisibility -> currentState.copy(showGuideDialog = partialState.visible)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
