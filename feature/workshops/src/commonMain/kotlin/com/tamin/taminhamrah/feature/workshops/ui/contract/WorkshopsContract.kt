package com.tamin.taminhamrah.feature.workshops.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import org.jetbrains.compose.resources.StringResource

/**
 * State of the کارگاه‌های کارفرما list.
 *
 * Search text and the status filter live here together and are sent together, so choosing a status
 * no longer throws away a code the user typed. [appliedSearch] is what the visible page was
 * actually fetched with — kept apart from the editable fields so the screen can show which filter
 * is in force even after the panel is closed.
 */
@Immutable
data class WorkshopsUiState(
    val list: PagedListState<WorkshopPR> = PagedListState(),
    val workshopIdInput: String = "",
    val branchCodeInput: String = "",
    val appliedSearch: WorkshopSearch = WorkshopSearch(),
    val statusFilter: WorkshopActivityStatus? = null,
    val isSearchOpen: Boolean = false,
    val isFilterSheetOpen: Boolean = false,
    /** The three figures the header card shows. Null until they have been counted. */
    val stats: WorkshopStats? = null,
    /** The workshop whose جزئیات و عملیات sheet is open, or null while it is closed. */
    val actionsFor: WorkshopPR? = null,
) {
    /** Whether anything narrows the list right now — what the filter chip reflects. */
    val hasActiveFilter: Boolean get() = statusFilter != null || appliedSearch.isNotEmpty

    /** Convenience for the screen, which shows the loaded rows and nothing else. */
    val workshops get() = list.items

    sealed interface PartialState {
        data object Loading : PartialState
        data object LoadingMore : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val list: PagedListState<WorkshopPR>) : PartialState
        data class SearchInputChanged(
            val workshopId: String? = null,
            val branchCode: String? = null,
        ) : PartialState

        data class QueryApplied(
            val search: WorkshopSearch,
            val status: WorkshopActivityStatus?,
        ) : PartialState

        data class SearchOpenChanged(val isOpen: Boolean) : PartialState
        data class FilterSheetOpenChanged(val isOpen: Boolean) : PartialState
        data class StatsLoaded(val stats: WorkshopStats) : PartialState
        data class ActionsForChanged(val workshop: WorkshopPR?) : PartialState
    }
}

/**
 * Counting rows needs no rows: the status counts are read off the envelope's `total`, so the
 * request asks for the smallest page the service will give.
 */
const val WORKSHOP_STATS_PAGE_SIZE = 1

/**
 * The figures over the list: how many workshops the user has, and how many are active.
 *
 * Counted from the service's own totals rather than from the rows on screen — the list is paged,
 * so counting what happens to be loaded would report a smaller number the further you scroll.
 */
@Immutable
data class WorkshopStats(
    val total: Int = 0,
    val active: Int = 0,
) {
    /** The design shows نیمه فعال and غیر فعال as one figure, so it is derived, never counted twice. */
    val inactive: Int get() = (total - active).coerceAtLeast(0)
}

/** The two code fields the search panel submits. Blank means "not part of the query". */
@Immutable
data class WorkshopSearch(
    val workshopId: String = "",
    val branchCode: String = "",
) {
    val isNotEmpty: Boolean get() = workshopId.isNotBlank() || branchCode.isNotBlank()
}

sealed interface WorkshopsIntent {
    /** First load, and the retry after a failure. */
    data object Load : WorkshopsIntent

    data object LoadMore : WorkshopsIntent

    data class WorkshopIdChanged(val value: String) : WorkshopsIntent
    data class BranchCodeChanged(val value: String) : WorkshopsIntent
    data class SearchOpenChanged(val isOpen: Boolean) : WorkshopsIntent

    /** Submit whatever the two code fields hold, keeping the status filter. */
    data object ApplySearch : WorkshopsIntent

    /** همه موارد — clears the code fields *and* the query, so the two cannot disagree. */
    data object ClearSearch : WorkshopsIntent

    data class FilterSheetOpenChanged(val isOpen: Boolean) : WorkshopsIntent

    /** Null clears the status filter while leaving any code search in place. */
    data class StatusFilterChanged(val status: WorkshopActivityStatus?) : WorkshopsIntent

    data class ActionsRequested(val workshop: WorkshopPR) : WorkshopsIntent
    data object ActionsDismissed : WorkshopsIntent
    data class ActionSelected(val action: WorkshopAction, val workshop: WorkshopPR) : WorkshopsIntent
}

sealed interface WorkshopsEvent {
    /** The picked action can be opened; the route navigates. */
    data class Navigate(
        val action: WorkshopAction,
        val workshopId: String,
        val branchCode: String,
        val workshopName: String,
    ) : WorkshopsEvent

    /** Something the user must read before anything else happens. */
    data class ShowMessage(val message: StringResource) : WorkshopsEvent
}
