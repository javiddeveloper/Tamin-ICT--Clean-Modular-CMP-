package com.tamin.taminhamrah.feature.pensionInquiry.ui.deferredInstallment.contract

data class DeferredInstallmentUiState(
    val isLoading: Boolean = false,
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface DeferredInstallmentIntent {
    data object Init : DeferredInstallmentIntent
}

sealed interface DeferredInstallmentEvent {
    data class ShowToast(val message: String) : DeferredInstallmentEvent
}
