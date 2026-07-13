package com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract

data class PayRollUiState(
    val isLoading: Boolean = false,
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface PayRollIntent {
    data object Init : PayRollIntent
}

sealed interface PayRollEvent {
    data class ShowToast(val message: String) : PayRollEvent
}
