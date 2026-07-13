package com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.contract

data class EdictUiState(
    val isLoading: Boolean = false,
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface EdictIntent {
    data object Init : EdictIntent
}

sealed interface EdictEvent {
    data class ShowToast(val message: String) : EdictEvent
}
