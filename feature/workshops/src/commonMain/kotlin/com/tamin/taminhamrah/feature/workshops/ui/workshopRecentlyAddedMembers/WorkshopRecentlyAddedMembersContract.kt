package com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

// `data` on purpose: without structural equality two states holding the same values never compare
// equal, so the screen recomposes on every emission no matter what the annotation promises.
@Immutable
data class WorkshopRecentlyAddedMembersUiState(
    val isLoading: Boolean = false,
    val list: ImmutableList<WorkshopNewMemberPR> = persistentListOf(),
    val error: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Loaded(val list: ImmutableList<WorkshopNewMemberPR>) : PartialState()
        data class Error(val message: String?) : PartialState()
    }
}

sealed interface WorkshopRecentlyAddedMembersEvent {
    data class ShowToast(val message: String) : WorkshopRecentlyAddedMembersEvent
}

sealed interface WorkshopRecentlyAddedMembersIntent {
    data class Load(val workshopId: String, val branchCode: String) : WorkshopRecentlyAddedMembersIntent
}

