package com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.contract

data class IssuanceCertificateUiState(
    val isLoading: Boolean = false,
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface IssuanceCertificateIntent {
    data object Init : IssuanceCertificateIntent
}

sealed interface IssuanceCertificateEvent {
    data class ShowToast(val message: String) : IssuanceCertificateEvent
}
