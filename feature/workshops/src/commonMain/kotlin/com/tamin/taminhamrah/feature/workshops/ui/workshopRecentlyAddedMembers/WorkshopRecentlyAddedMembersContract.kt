package com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers

import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberPR

class WorkshopRecentlyAddedMembersUiState(
    val isLoading: Boolean = false,
    val list: List<WorkshopNewMemberPR> = emptyList(),
    val error: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Loaded(val list: List<WorkshopNewMemberPR>) : PartialState()
        data class Error(val message: String?) : PartialState()
    }
}

sealed interface WorkshopRecentlyAddedMembersEvent {
    data class ShowToast(val message: String) : WorkshopRecentlyAddedMembersEvent
}

sealed interface WorkshopRecentlyAddedMembersIntent {
    data class Load(val workshopId: String, val branchCode: String) : WorkshopRecentlyAddedMembersIntent
}

