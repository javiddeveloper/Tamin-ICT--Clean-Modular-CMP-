package com.tamin.taminhamrah.feature.pensionInquiry.ui.deservedTreatment.contract

data class DeservedTreatmentUiState(
    val isLoading: Boolean = false,
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface DeservedTreatmentIntent {
    data object Init : DeservedTreatmentIntent
}

sealed interface DeservedTreatmentEvent {
    data class ShowToast(val message: String) : DeservedTreatmentEvent
}
