package com.tamin.taminhamrah.feature.workshops.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource

/**
 * State of the کارگاه‌های کارفرما list.
 *
 * Search text and the status filter live here together and are sent together, so choosing a status
 * no longer throws away a code the user typed. [appliedSearch] is what the current page was
 * actually fetched with — kept apart from the editable fields so the screen can show which filter
 * is in force even after the panel is closed.
 */
@Immutable
data class WorkshopsUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val workshops: ImmutableList<WorkshopPR> = persistentListOf(),
    val hasMore: Boolean = false,
    val workshopIdInput: String = "",
    val branchCodeInput: String = "",
    val appliedSearch: WorkshopSearch = WorkshopSearch(),
    val statusFilter: WorkshopActivityStatus? = null,
    /** The workshop whose action menu is open, or null while the sheet is closed. */
    val actionsFor: WorkshopPR? = null,
) {
    /** The first page has not arrived yet — the skeleton stands in for the list. */
    val isFirstLoad: Boolean get() = isLoading && workshops.isEmpty()

    /** Nothing matched, and it is not because the request is still in flight. */
    val isEmpty: Boolean get() = !isLoading && error == null && workshops.isEmpty()

    /** Whether anything narrows the list right now — what an "applied filter" chip reflects. */
    val hasActiveFilter: Boolean
        get() = statusFilter != null || appliedSearch.isNotEmpty

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data object LoadingMore : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(
            val workshops: ImmutableList<WorkshopPR>,
            val hasMore: Boolean,
        ) : PartialState

        data class SearchInputChanged(
            val workshopId: String? = null,
            val branchCode: String? = null,
        ) : PartialState

        data class QueryApplied(
            val search: WorkshopSearch,
            val status: WorkshopActivityStatus?,
        ) : PartialState

        data class ActionsForChanged(val workshop: WorkshopPR?) : PartialState
    }
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

    /** Submit whatever the two code fields hold, keeping the status filter. */
    data object ApplySearch : WorkshopsIntent

    /** همه موارد — clears the code fields *and* the query, so the two cannot disagree. */
    data object ClearSearch : WorkshopsIntent

    /** Null clears the status filter while leaving any code search in place. */
    data class StatusFilterChanged(val status: WorkshopActivityStatus?) : WorkshopsIntent

    data class ActionsRequested(val workshop: WorkshopPR) : WorkshopsIntent
    data object ActionsDismissed : WorkshopsIntent
    data class ActionSelected(val action: WorkshopAction, val workshop: WorkshopPR) : WorkshopsIntent
}

sealed interface WorkshopsEvent {
    /** The picked action can be opened directly; the screen navigates. */
    data class Navigate(
        val action: WorkshopAction,
        val workshopId: String,
        val branchCode: String,
        val workshopName: String,
    ) : WorkshopsEvent

    /** Something the user must read before anything else happens. */
    data class ShowMessage(val message: StringResource) : WorkshopsEvent
}
