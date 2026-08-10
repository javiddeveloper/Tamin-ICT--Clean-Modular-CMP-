package com.tamin.taminhamrah.feature.history.ui.jobinfo

import com.tamin.taminhamrah.model.history.HistoryJobInfoItemPR

data class HistoryJobInfoUiState(
    val isLoading: Boolean = false,
    val jobInfos: List<HistoryJobInfoItemPR> = emptyList(),
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class JobInfosLoaded(val list: List<HistoryJobInfoItemPR>) : PartialState
    }
}

sealed interface HistoryJobInfoIntent {
    data object Load : HistoryJobInfoIntent
    data object Retry : HistoryJobInfoIntent
}

sealed interface HistoryJobInfoEvent
