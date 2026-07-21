package com.tamin.taminhamrah.feature.workshops.ui.workshopMembers

import com.tamin.taminhamrah.model.workshop.WorkshopMemberPR

class WorkshopMembersUiState(
    val isLoading: Boolean = false,
    val list: List<WorkshopMemberPR> = emptyList(),
    val error: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Loaded(val list: List<WorkshopMemberPR>) : PartialState()
        data class Error(val message: String?) : PartialState()
    }
}

sealed interface WorkshopMembersEvent
sealed interface WorkshopMembersIntent {
    data class Load(val workshopId: String, val branchCode: String) : WorkshopMembersIntent
}

