package com.tamin.taminhamrah.feature.pensionInquiry.ui.prescription.contract

import androidx.compose.runtime.Immutable

@Immutable
data class PrescriptionUiState(
    val isLoading: Boolean = false,
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface PrescriptionIntent {
    data object Init : PrescriptionIntent
}

sealed interface PrescriptionEvent {
    data class ShowToast(val message: String) : PrescriptionEvent
}
