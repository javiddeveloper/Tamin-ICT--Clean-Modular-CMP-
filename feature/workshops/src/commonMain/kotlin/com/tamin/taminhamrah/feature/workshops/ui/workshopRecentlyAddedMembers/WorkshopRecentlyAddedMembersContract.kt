package com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.workshop.NewMemberRequestStatus
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberPR
import org.jetbrains.compose.resources.StringResource

/**
 * State of نام نویسی غیر حضوری بیمه شده.
 *
 * The branch reaches the query as a typed field here. In the old client it travelled under one key
 * and was read under another, so the screen never made a request at all and showed an empty list
 * with no error.
 */
@Immutable
data class WorkshopRecentlyAddedMembersUiState(
    val workshopId: String = "",
    val branchCode: String = "",
    val list: PagedListState<WorkshopNewMemberPR> = PagedListState(),
    val draft: NewMemberSearch = NewMemberSearch(),
    val applied: NewMemberSearch = NewMemberSearch(),
    val isSearchOpen: Boolean = false,
    /** The row being confirmed or deleted; its actions show progress meanwhile. */
    val busyPersonalId: Long? = null,
) {
    sealed interface PartialState {
        data class Opened(val workshopId: String, val branchCode: String) : PartialState
        data object Loading : PartialState
        data object LoadingMore : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val list: PagedListState<WorkshopNewMemberPR>) : PartialState
        data class DraftChanged(val draft: NewMemberSearch) : PartialState
        data class Applied(val search: NewMemberSearch) : PartialState
        data class SearchOpenChanged(val isOpen: Boolean) : PartialState
        data class Busy(val personalId: Long?) : PartialState
    }
}

/** The two fields the search sheet submits. */
@Immutable
data class NewMemberSearch(
    val nationalId: String = "",
    val status: NewMemberRequestStatus? = null,
) {
    val isNotEmpty: Boolean get() = nationalId.isNotBlank() || status != null
}

sealed interface WorkshopRecentlyAddedMembersIntent {
    data class Open(
        val workshopId: String,
        val branchCode: String,
    ) : WorkshopRecentlyAddedMembersIntent

    data object LoadMore : WorkshopRecentlyAddedMembersIntent
    data object Retry : WorkshopRecentlyAddedMembersIntent
    data class SearchOpenChanged(val isOpen: Boolean) : WorkshopRecentlyAddedMembersIntent
    data class DraftChanged(val draft: NewMemberSearch) : WorkshopRecentlyAddedMembersIntent
    data object ApplySearch : WorkshopRecentlyAddedMembersIntent
    data object ClearSearch : WorkshopRecentlyAddedMembersIntent

    /** تایید — only a drafted registration can be confirmed. */
    data class Confirm(val member: WorkshopNewMemberPR) : WorkshopRecentlyAddedMembersIntent

    /** حذف — same guard as confirm. */
    data class Delete(val member: WorkshopNewMemberPR) : WorkshopRecentlyAddedMembersIntent

    data class Edit(val member: WorkshopNewMemberPR) : WorkshopRecentlyAddedMembersIntent
    data class Follow(val member: WorkshopNewMemberPR) : WorkshopRecentlyAddedMembersIntent
}

sealed interface WorkshopRecentlyAddedMembersEvent {
    /** Confirmed, with the tracking code the service returned. A success, shown as one. */
    data class Confirmed(val referenceCode: String) : WorkshopRecentlyAddedMembersEvent

    data class ShowMessage(val message: StringResource) : WorkshopRecentlyAddedMembersEvent

    /** The member form opens on this registration; a new one is [personalRequestId] `0`. */
    data class OpenMemberForm(val personalRequestId: Long) : WorkshopRecentlyAddedMembersEvent

    /** پیگیری — the request cartable, opened on this registration rather than on nothing. */
    data class OpenCartable(val referenceCode: String) : WorkshopRecentlyAddedMembersEvent
}
