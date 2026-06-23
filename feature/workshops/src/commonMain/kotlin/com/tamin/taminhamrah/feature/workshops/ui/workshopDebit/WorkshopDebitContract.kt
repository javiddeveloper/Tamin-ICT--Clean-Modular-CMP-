package com.tamin.taminhamrah.feature.workshops.ui.workshopDebit

import com.tamin.taminhamrah.model.workshop.WorkshopDebitPR

data class WorkshopDebitUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val workshopDebits: List<WorkshopDebitPR> = emptyList(),
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class WorkshopDebitsLoaded(val list: List<WorkshopDebitPR>) : PartialState
    }
}

sealed interface WorkshopDebitIntent {
    data class LoadWorkshopDebit(
        val workshopId: String?,
        val branchCode: String?
    ) : WorkshopDebitIntent
}


sealed interface WorkshopDebitEvent {
    data class ShowToast(val message: String) : WorkshopDebitEvent
}
