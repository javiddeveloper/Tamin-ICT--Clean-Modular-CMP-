package com.tamin.taminhamrah.feature.workshops.ui.workshopDebit

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.workshop.WorkshopDebitPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class WorkshopDebitUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val workshopDebits: ImmutableList<WorkshopDebitPR> = persistentListOf(),
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class WorkshopDebitsLoaded(val list: ImmutableList<WorkshopDebitPR>) : PartialState
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
