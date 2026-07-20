package com.tamin.taminhamrah.feature.pensionInquiry.ui.deferredInstallment

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.deferredInstallment.contract.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeferredInstallmentViewModel : BaseViewModel<DeferredInstallmentUiState, DeferredInstallmentUiState.PartialState, DeferredInstallmentEvent, DeferredInstallmentIntent>(
    initialState = DeferredInstallmentUiState()
) {
    override fun handleIntent(intent: DeferredInstallmentIntent): Flow<DeferredInstallmentUiState.PartialState> = flow {
        when (intent) {
            DeferredInstallmentIntent.Init -> {
                // No-op for now
            }
        }
    }

    override fun reduceState(
        currentState: DeferredInstallmentUiState,
        partialState: DeferredInstallmentUiState.PartialState
    ): DeferredInstallmentUiState = when (partialState) {
        is DeferredInstallmentUiState.PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is DeferredInstallmentUiState.PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): DeferredInstallmentUiState.PartialState =
        DeferredInstallmentUiState.PartialState.Error(message)
}
