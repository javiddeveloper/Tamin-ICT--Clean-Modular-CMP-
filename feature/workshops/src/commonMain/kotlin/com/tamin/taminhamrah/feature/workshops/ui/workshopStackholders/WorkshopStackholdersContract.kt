package com.tamin.taminhamrah.feature.workshops.ui.workshopStackholders

import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderPR

class WorkshopStackholdersUiState(
    val isLoading: Boolean = false,
    val list: List<WorkshopStackHolderPR> = emptyList(),
    val error: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Loaded(val list: List<WorkshopStackHolderPR>) : PartialState()
        data class Error(val message: String?) : PartialState()
    }
}

sealed interface WorkshopStackholdersEvent
sealed interface WorkshopStackholdersIntent {
    data class Load(val workshopId: String, val branchCode: String) : WorkshopStackholdersIntent
}

