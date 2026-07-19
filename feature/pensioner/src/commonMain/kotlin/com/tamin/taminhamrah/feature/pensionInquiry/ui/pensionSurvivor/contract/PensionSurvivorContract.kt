package com.tamin.taminhamrah.feature.pensionInquiry.ui.pensionSurvivor.contract

data class PensionSurvivorUiState(
    val isLoading: Boolean = false,
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface PensionSurvivorIntent {
    data object Init : PensionSurvivorIntent
}

sealed interface PensionSurvivorEvent {
    data class ShowToast(val message: String) : PensionSurvivorEvent
}
