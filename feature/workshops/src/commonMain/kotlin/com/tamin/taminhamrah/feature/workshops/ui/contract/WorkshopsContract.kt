package com.tamin.taminhamrah.feature.workshops.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.StringResource


/**
 * State of the کارگاه‌های کارفرما list.
 */
@Immutable
data class WorkshopsUiState(
    // Workshop List & Pagination
    val list: PagedListState<WorkshopPR> = PagedListState(),
    val stats: WorkshopStats? = null,
    /** The workshop جزئیات کارگاه is showing, or null while the list is up. */
    val detailFor: WorkshopPR? = null,
    /**
     * Which services جزئیات کارگاه offers, after the server's feature flags have had their say.
     *
     * Starts as every action and narrows once the flags are read, rather than starting empty — a
     * menu that flashes blank on open reads as a failure, and an unreachable row is the rarer case.
     */
    val availableActions: ImmutableList<WorkshopAction> = WorkshopAction.entries.toImmutableList(),

    // Search & Status Filters
    val workshopIdInput: String = "",
    val branchCodeInput: String = "",
    val appliedSearch: WorkshopSearch = WorkshopSearch(),
    val statusFilter: WorkshopActivityStatus? = null,
    val isSearchOpen: Boolean = false,
    val isFilterSheetOpen: Boolean = false,

    /** رسیدگی به بدهی ماده ۱۶ is asking whether the workshop has any debt to open on. */
    val isCheckingDebts: Boolean = false,
    /** «لیست بدهی برای این کارگاه یافت نشد» — the answer when it has none. */
    val isNoDebtDialogOpen: Boolean = false,
) {
    val hasActiveFilter: Boolean
        get() = statusFilter != null || appliedSearch.isNotEmpty

    val workshops get() = list.items

    sealed interface PartialState {
        // Workshops Paging & Status
        data object Loading : PartialState
        data object LoadingMore : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val list: PagedListState<WorkshopPR>) : PartialState
        data class StatsLoaded(val stats: WorkshopStats) : PartialState
        data class DetailForChanged(val workshop: WorkshopPR?) : PartialState
        data class ActionsResolved(val actions: ImmutableList<WorkshopAction>) : PartialState

        // Search & Filters
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

        // رسیدگی به بدهی ماده ۱۶
        data class CheckingDebtsChanged(val isChecking: Boolean) : PartialState
        data class NoDebtDialogChanged(val isOpen: Boolean) : PartialState
    }
}

/** The strip's figures, counted over distinct workshops — the rows the list itself shows. */
@Immutable
data class WorkshopStats(
    val total: Int = 0,
    val active: Int = 0,
) {
    val inactive: Int get() = (total - active).coerceAtLeast(0)
}

@Immutable
data class WorkshopSearch(
    val workshopId: String = "",
    val branchCode: String = "",
) {
    val isNotEmpty: Boolean get() = workshopId.isNotBlank() || branchCode.isNotBlank()
}

sealed interface WorkshopsIntent {
    // Workshops Loading & Pagination
    data object Load : WorkshopsIntent
    data object LoadMore : WorkshopsIntent

    // Search & Filter Panel
    data class WorkshopIdChanged(val value: String) : WorkshopsIntent
    data class BranchCodeChanged(val value: String) : WorkshopsIntent
    data class SearchOpenChanged(val isOpen: Boolean) : WorkshopsIntent
    data object ApplySearch : WorkshopsIntent
    data object ClearSearch : WorkshopsIntent
    data class FilterSheetOpenChanged(val isOpen: Boolean) : WorkshopsIntent
    data class StatusFilterChanged(val status: WorkshopActivityStatus?) : WorkshopsIntent

    // Cascading Dropdown Selectors

    // جزئیات کارگاه
    data class DetailRequested(val workshop: WorkshopPR) : WorkshopsIntent
    data object DetailDismissed : WorkshopsIntent
    data class ActionSelected(val action: WorkshopAction, val workshop: WorkshopPR) : WorkshopsIntent
    data class AvailableActionsResolved(val actions: ImmutableList<WorkshopAction>) : WorkshopsIntent
    data object NoDebtDialogDismissed : WorkshopsIntent
}

sealed interface WorkshopsEvent {
    /**
     * Carries the whole workshop identity rather than a widening list of positional strings: the
     * payment call needs two more fields than the destination's own route does, and six adjacent
     * `String` parameters are one transposition away from paying the wrong workshop.
     */
    data class Navigate(
        val action: WorkshopAction,
        val workshopId: String,
        val branchCode: String,
        val workshopName: String,
        /** `01` حقیقی / `02` حقوقی — reaches `pay-normal-debit` as `nationalType`. */
        val characterCode: String,
        /** The حقوقی workshop's national id; blank for a حقیقی one. */
        val legalNationalId: String,
    ) : WorkshopsEvent

    data class ShowMessage(val message: StringResource) : WorkshopsEvent
    data class ShowToast(val message: String) : WorkshopsEvent
}
