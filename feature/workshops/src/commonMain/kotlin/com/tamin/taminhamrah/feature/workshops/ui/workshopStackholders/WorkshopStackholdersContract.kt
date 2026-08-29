package com.tamin.taminhamrah.feature.workshops.ui.workshopStackholders

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

// `data` on purpose: without structural equality two states holding the same values never
// compare equal, so the screen recomposes on every emission whatever the annotation promises.
@Immutable
data class WorkshopStackholdersUiState(
    val isLoading: Boolean = false,
    val list: ImmutableList<WorkshopStackHolderPR> = persistentListOf(),
    val error: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Loaded(val list: ImmutableList<WorkshopStackHolderPR>) : PartialState()
        data class Error(val message: String?) : PartialState()
    }
}

sealed interface WorkshopStackholdersEvent
sealed interface WorkshopStackholdersIntent {
    data class Load(val workshopId: String, val branchCode: String) : WorkshopStackholdersIntent
}

