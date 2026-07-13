package com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PayRollViewModel : BaseViewModel<PayRollUiState, PayRollUiState.PartialState, PayRollEvent, PayRollIntent>(
    initialState = PayRollUiState()
) {
    override fun handleIntent(intent: PayRollIntent): Flow<PayRollUiState.PartialState> = flow {
        when (intent) {
            PayRollIntent.Init -> {
                // No-op for now
            }
        }
    }

    override fun reduceState(
        currentState: PayRollUiState,
        partialState: PayRollUiState.PartialState
    ): PayRollUiState = when (partialState) {
        is PayRollUiState.PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PayRollUiState.PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): PayRollUiState.PartialState =
        PayRollUiState.PartialState.Error(message)
}
