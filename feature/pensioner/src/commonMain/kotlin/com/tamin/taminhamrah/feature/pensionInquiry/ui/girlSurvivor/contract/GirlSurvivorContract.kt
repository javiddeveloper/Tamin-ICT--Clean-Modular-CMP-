package com.tamin.taminhamrah.feature.pensionInquiry.ui.girlSurvivor.contract

data class GirlSurvivorUiState(
    val isLoading: Boolean = false,
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface GirlSurvivorIntent {
    data object Init : GirlSurvivorIntent
}

sealed interface GirlSurvivorEvent {
    data class ShowToast(val message: String) : GirlSurvivorEvent
}
