package com.tamin.taminhamrah.feature.workshops.ui.workshopStackholders

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.feature.workshops.ui.model.PersonSearch
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderPR

/**
 * State of ذینفعان.
 *
 * The search here is reachable — the old screen wired one up and then never showed its button.
 */
@Immutable
data class WorkshopStackholdersUiState(
    val workshopId: String = "",
    val branchCode: String = "",
    val list: PagedListState<WorkshopStackHolderPR> = PagedListState(),
    val draft: PersonSearch = PersonSearch(),
    val applied: PersonSearch = PersonSearch(),
    val isSearchOpen: Boolean = false,
) {
    sealed interface PartialState {
        data class Opened(val workshopId: String, val branchCode: String) : PartialState
        data object Loading : PartialState
        data object LoadingMore : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val list: PagedListState<WorkshopStackHolderPR>) : PartialState
        data class DraftChanged(val draft: PersonSearch) : PartialState
        data class Applied(val search: PersonSearch) : PartialState
        data class SearchOpenChanged(val isOpen: Boolean) : PartialState
    }
}

sealed interface WorkshopStackholdersIntent {
    data class Open(val workshopId: String, val branchCode: String) : WorkshopStackholdersIntent
    data object LoadMore : WorkshopStackholdersIntent
    data object Retry : WorkshopStackholdersIntent
    data class SearchOpenChanged(val isOpen: Boolean) : WorkshopStackholdersIntent
    data class DraftChanged(val draft: PersonSearch) : WorkshopStackholdersIntent
    data object ApplySearch : WorkshopStackholdersIntent
    data object ClearSearch : WorkshopStackholdersIntent

    /** Applies [search] as it stands — what removing one of the applied-search chips does. */
    data class ReplaceSearch(val search: PersonSearch) : WorkshopStackholdersIntent
}

sealed interface WorkshopStackholdersEvent
