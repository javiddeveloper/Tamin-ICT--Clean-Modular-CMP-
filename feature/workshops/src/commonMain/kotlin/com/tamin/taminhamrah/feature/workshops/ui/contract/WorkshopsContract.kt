package com.tamin.taminhamrah.feature.workshops.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import org.jetbrains.compose.resources.StringResource
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.model.contractFlow.BranchSelectionFormPR
import com.tamin.taminhamrah.model.workshop.EmployerAgreementPR

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

    // Search & Status Filters
    val workshopIdInput: String = "",
    val branchCodeInput: String = "",
    val appliedSearch: WorkshopSearch = WorkshopSearch(),
    val statusFilter: WorkshopActivityStatus? = null,
    val isSearchOpen: Boolean = false,
    val isFilterSheetOpen: Boolean = false,

    // Cascading Branch Selection (استان → شهر → شعبه)
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

        // Cascading Branch Selection
    }
}

const val WORKSHOP_STATS_PAGE_SIZE = 1

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
}

sealed interface WorkshopsEvent {
    data class Navigate(
        val action: WorkshopAction,
        val workshopId: String,
        val branchCode: String,
        val workshopName: String,
    ) : WorkshopsEvent

    data class ShowMessage(val message: StringResource) : WorkshopsEvent
    data class ShowToast(val message: String) : WorkshopsEvent
}
