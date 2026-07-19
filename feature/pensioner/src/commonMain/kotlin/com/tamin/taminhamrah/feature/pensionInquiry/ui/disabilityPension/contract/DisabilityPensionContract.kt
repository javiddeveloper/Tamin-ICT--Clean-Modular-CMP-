package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract

data class DisabilityPensionUiState(
    val isLoading: Boolean = false,
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface DisabilityPensionIntent {
    data object Init : DisabilityPensionIntent
}

sealed interface DisabilityPensionEvent {
    data class ShowToast(val message: String) : DisabilityPensionEvent
}
