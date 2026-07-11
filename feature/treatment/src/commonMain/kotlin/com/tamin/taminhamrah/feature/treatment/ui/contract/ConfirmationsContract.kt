package com.tamin.taminhamrah.feature.treatment.ui.contract

import com.tamin.taminhamrah.model.treatment.MedicalAuthoritiesPR

data class ConfirmationsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val medicalAuthorities: List<MedicalAuthoritiesPR> = emptyList()
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data object Reset : PartialState()
        data class MedicalAuthoritiesLoaded(val list: List<MedicalAuthoritiesPR>) : PartialState()
    }
}

sealed class ConfirmationsIntent {
    data object LoadList : ConfirmationsIntent()
}

sealed class ConfirmationsEvent {
    data class ShowToast(val message: String) : ConfirmationsEvent()
}
