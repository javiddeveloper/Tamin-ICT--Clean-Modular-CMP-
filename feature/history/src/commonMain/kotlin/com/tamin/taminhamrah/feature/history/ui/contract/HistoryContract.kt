package com.tamin.taminhamrah.feature.history.ui.contract

import com.tamin.taminhamrah.model.history.TalfighInfoItemPR
import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR

data class HistoryUiState(
    val isLoading: Boolean = false,
    val talfighInfos: List<TalfighInfoItemPR> = emptyList(),
    val dastmozdInfos: List<DastmozdInfoItemPR> = emptyList(),
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class TalfighiDataLoaded(val list: List<TalfighInfoItemPR>) : PartialState
        data class DastmozdDataLoaded(val list: List<DastmozdInfoItemPR>) : PartialState
    }
}

sealed interface HistoryIntent {
    object LoadTalfighiData : HistoryIntent
    object LoadDastmozdData : HistoryIntent
}

sealed interface HistoryEvent {
    data class ShowToast(val message: String) : HistoryEvent
}
