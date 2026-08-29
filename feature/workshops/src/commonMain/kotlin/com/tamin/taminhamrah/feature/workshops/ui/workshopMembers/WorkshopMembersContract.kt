package com.tamin.taminhamrah.feature.workshops.ui.workshopMembers

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.workshop.WorkshopMemberPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

// `data` on purpose: without structural equality two states holding the same values never
// compare equal, so the screen recomposes on every emission whatever the annotation promises.
@Immutable
data class WorkshopMembersUiState(
    val isLoading: Boolean = false,
    val list: ImmutableList<WorkshopMemberPR> = persistentListOf(),
    val error: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Loaded(val list: ImmutableList<WorkshopMemberPR>) : PartialState()
        data class Error(val message: String?) : PartialState()
    }
}

sealed interface WorkshopMembersEvent
sealed interface WorkshopMembersIntent {
    data class Load(val workshopId: String, val branchCode: String) : WorkshopMembersIntent
}

