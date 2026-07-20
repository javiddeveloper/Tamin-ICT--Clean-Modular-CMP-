package com.tamin.taminhamrah.feature.pensionInquiry.ui.prescription

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.prescription.contract.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PrescriptionViewModel : BaseViewModel<PrescriptionUiState, PrescriptionUiState.PartialState, PrescriptionEvent, PrescriptionIntent>(
    initialState = PrescriptionUiState()
) {
    override fun handleIntent(intent: PrescriptionIntent): Flow<PrescriptionUiState.PartialState> = flow {
        when (intent) {
            PrescriptionIntent.Init -> {
                // No-op for now
            }
        }
    }

    override fun reduceState(
        currentState: PrescriptionUiState,
        partialState: PrescriptionUiState.PartialState
    ): PrescriptionUiState = when (partialState) {
        is PrescriptionUiState.PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PrescriptionUiState.PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): PrescriptionUiState.PartialState =
        PrescriptionUiState.PartialState.Error(message)
}
