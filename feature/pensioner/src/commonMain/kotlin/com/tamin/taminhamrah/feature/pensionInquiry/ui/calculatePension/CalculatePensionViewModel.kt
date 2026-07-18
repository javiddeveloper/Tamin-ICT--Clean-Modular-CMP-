package com.tamin.taminhamrah.feature.pensionInquiry.ui.calculatePension

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.calculatePension.contract.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CalculatePensionViewModel : BaseViewModel<CalculatePensionUiState, CalculatePensionUiState.PartialState, CalculatePensionEvent, CalculatePensionIntent>(
    initialState = CalculatePensionUiState()
) {
    override fun handleIntent(intent: CalculatePensionIntent): Flow<CalculatePensionUiState.PartialState> = flow {
        when (intent) {
            CalculatePensionIntent.Init -> {
                // No-op for now
            }
        }
    }

    override fun reduceState(
        currentState: CalculatePensionUiState,
        partialState: CalculatePensionUiState.PartialState
    ): CalculatePensionUiState = when (partialState) {
        is CalculatePensionUiState.PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is CalculatePensionUiState.PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): CalculatePensionUiState.PartialState =
        CalculatePensionUiState.PartialState.Error(message)
}
