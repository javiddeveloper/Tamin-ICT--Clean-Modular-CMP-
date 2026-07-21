package com.tamin.taminhamrah.feature.pensionInquiry.ui.deservedTreatment

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.deservedTreatment.contract.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeservedTreatmentViewModel : BaseViewModel<DeservedTreatmentUiState, DeservedTreatmentUiState.PartialState, DeservedTreatmentEvent, DeservedTreatmentIntent>(
    initialState = DeservedTreatmentUiState()
) {
    override fun handleIntent(intent: DeservedTreatmentIntent): Flow<DeservedTreatmentUiState.PartialState> = flow {
        when (intent) {
            DeservedTreatmentIntent.Init -> {
                // No-op for now
            }
        }
    }

    override fun reduceState(
        currentState: DeservedTreatmentUiState,
        partialState: DeservedTreatmentUiState.PartialState
    ): DeservedTreatmentUiState = when (partialState) {
        is DeservedTreatmentUiState.PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is DeservedTreatmentUiState.PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): DeservedTreatmentUiState.PartialState =
        DeservedTreatmentUiState.PartialState.Error(message)
}
