package com.tamin.taminhamrah.feature.healthProfile.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationUiState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationUiState.PartialState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationEvent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SelfDeclarationViewModel : BaseViewModel<SelfDeclarationUiState, PartialState, SelfDeclarationEvent, SelfDeclarationIntent>(
    initialState = SelfDeclarationUiState()
) {
    override fun handleIntent(intent: SelfDeclarationIntent): Flow<PartialState> {
        return when (intent) {
            is SelfDeclarationIntent.ChangeStep -> handleChangeStep(intent.step)
            is SelfDeclarationIntent.UpdateState -> handleUpdateState(intent.transform)
        }
    }

    private fun handleChangeStep(step: SelfDeclarationStep): Flow<PartialState> = flow {
        emit(PartialState.StepChanged(step))
    }

    private fun handleUpdateState(transform: SelfDeclarationUiState.() -> SelfDeclarationUiState): Flow<PartialState> = flow {
        emit(PartialState.StateUpdated(transform))
    }

    override fun reduceState(
        currentState: SelfDeclarationUiState,
        partialState: PartialState
    ): SelfDeclarationUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            error = null
        )
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
        is PartialState.StepChanged -> currentState.copy(
            currentStep = partialState.step
        )
        is PartialState.StateUpdated -> partialState.transform(currentState)
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
