package com.tamin.taminhamrah.feature.workshops.ui.workshopMembers

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.workshop.WorkshopMemberPR

/** State of کارکنان — the insured people registered against one workshop. */
@Immutable
data class WorkshopMembersUiState(
    val workshopId: String = "",
    val branchCode: String = "",
    val list: PagedListState<WorkshopMemberPR> = PagedListState(),
    val draft: PersonSearch = PersonSearch(),
    val applied: PersonSearch = PersonSearch(),
    val isSearchOpen: Boolean = false,
) {
    sealed interface PartialState {
        data class Opened(val workshopId: String, val branchCode: String) : PartialState
        data object Loading : PartialState
        data object LoadingMore : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val list: PagedListState<WorkshopMemberPR>) : PartialState
        data class DraftChanged(val draft: PersonSearch) : PartialState
        data class Applied(val search: PersonSearch) : PartialState
        data class SearchOpenChanged(val isOpen: Boolean) : PartialState
    }
}

/**
 * The two fields the کارکنان and ذینفعان searches share.
 *
 * Each value goes to the property that names it — the old stakeholder screen crossed them over and
 * filtered on the wrong column.
 */
@Immutable
data class PersonSearch(
    val nationalId: String = "",
    val insuranceNumber: String = "",
) {
    val isNotEmpty: Boolean get() = nationalId.isNotBlank() || insuranceNumber.isNotBlank()
}

sealed interface WorkshopMembersIntent {
    data class Open(val workshopId: String, val branchCode: String) : WorkshopMembersIntent
    data object LoadMore : WorkshopMembersIntent
    data object Retry : WorkshopMembersIntent
    data class SearchOpenChanged(val isOpen: Boolean) : WorkshopMembersIntent
    data class DraftChanged(val draft: PersonSearch) : WorkshopMembersIntent
    data object ApplySearch : WorkshopMembersIntent
    data object ClearSearch : WorkshopMembersIntent
}

sealed interface WorkshopMembersEvent
