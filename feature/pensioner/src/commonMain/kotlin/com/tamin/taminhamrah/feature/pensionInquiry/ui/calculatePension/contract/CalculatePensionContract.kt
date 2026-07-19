package com.tamin.taminhamrah.feature.pensionInquiry.ui.calculatePension.contract

data class CalculatePensionUiState(
    val isLoading: Boolean = false,
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface CalculatePensionIntent {
    data object Init : CalculatePensionIntent
}

sealed interface CalculatePensionEvent {
    data class ShowToast(val message: String) : CalculatePensionEvent
}
