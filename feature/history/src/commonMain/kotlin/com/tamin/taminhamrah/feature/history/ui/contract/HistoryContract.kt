package com.tamin.taminhamrah.feature.history.ui.contract

import com.tamin.taminhamrah.model.history.TalfighInfoItemPR

data class HistoryUiState(
    val isLoading: Boolean = false,
    val talfighInfos: List<TalfighInfoItemPR> = emptyList(),
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class DataLoaded(val list: List<TalfighInfoItemPR>) : PartialState
    }
}

sealed interface HistoryIntent {
    object LoadData : HistoryIntent
}

sealed interface HistoryEvent {
    data class ShowToast(val message: String) : HistoryEvent
}
