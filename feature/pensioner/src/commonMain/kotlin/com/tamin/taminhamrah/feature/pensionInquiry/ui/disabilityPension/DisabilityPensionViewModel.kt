package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DisabilityPensionViewModel : BaseViewModel<DisabilityPensionUiState, DisabilityPensionUiState.PartialState, DisabilityPensionEvent, DisabilityPensionIntent>(
    initialState = DisabilityPensionUiState()
) {
    override fun handleIntent(intent: DisabilityPensionIntent): Flow<DisabilityPensionUiState.PartialState> = flow {
        when (intent) {
            DisabilityPensionIntent.Init -> {
                // No-op for now
            }
        }
    }

    override fun reduceState(
        currentState: DisabilityPensionUiState,
        partialState: DisabilityPensionUiState.PartialState
    ): DisabilityPensionUiState = when (partialState) {
        is DisabilityPensionUiState.PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is DisabilityPensionUiState.PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): DisabilityPensionUiState.PartialState =
        DisabilityPensionUiState.PartialState.Error(message)
}
