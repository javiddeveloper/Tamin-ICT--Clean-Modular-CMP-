package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFilePR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class ConstructionInsuranceState(
    /** True only while the first page is in flight — drives the full-screen skeleton. */
    val isLoading: Boolean = false,
    val items: ImmutableList<ConstructionFilePR> = persistentListOf(),
    val error: String? = null,
    /** True while a further page is in flight — drives the list-footer spinner, not the skeleton. */
    val isLoadingNextPage: Boolean = false,
    /** No more pages left to ask for, or none exist yet — hides the load-more trigger. */
    val endReached: Boolean = false,
    /** A page request past the first one failed — shown in the list footer with a retry action. */
    val paginationError: String? = null,
    val isSearchExpanded: Boolean = false,
    val fileNoQuery: String = "",
    val reqNoQuery: String = "",
    val workshopIdQuery: String = "",
    val branchCodeQuery: String = "",
    // The query actually behind [items] — a snapshot taken when a search last ran (LoadData,
    // ExecuteSearch, Refresh, ResetSearch), not the live text field values above. Editing a field
    // must not by itself change what the filter chip row or [items] claim to represent; only
    // pressing جستجو (or clearing) does.
    val appliedFileNoQuery: String = "",
    val appliedReqNoQuery: String = "",
    val appliedWorkshopIdQuery: String = "",
    val appliedBranchCodeQuery: String = "",
    val userName: String = "",
    val nationalCode: String = "",

    val isNoticeVisible: Boolean = false,
)

sealed interface ConstructionInsuranceIntent {
    data object LoadData : ConstructionInsuranceIntent
    data object Refresh : ConstructionInsuranceIntent
    data class ToggleSearchExpanded(val expanded: Boolean) : ConstructionInsuranceIntent
    data class OnFileNoQueryChanged(val query: String) : ConstructionInsuranceIntent
    data class OnReqNoQueryChanged(val query: String) : ConstructionInsuranceIntent
    data class OnWorkshopIdQueryChanged(val query: String) : ConstructionInsuranceIntent
    data class OnBranchCodeQueryChanged(val query: String) : ConstructionInsuranceIntent
    data object ExecuteSearch : ConstructionInsuranceIntent
    data object ResetSearch : ConstructionInsuranceIntent
    data class OnDetailClick(val item: ConstructionFilePR) : ConstructionInsuranceIntent

    data object ToggleNoticeVisibility : ConstructionInsuranceIntent
    data object LoadNextPage : ConstructionInsuranceIntent
    data object RetryNextPage : ConstructionInsuranceIntent
}

sealed interface ConstructionInsuranceEvent {
    data class ShowToast(val message: String) : ConstructionInsuranceEvent
    data class NavigateToDetails(val item: ConstructionFilePR) : ConstructionInsuranceEvent
}

sealed interface ConstructionInsurancePartialState {
    data class IdentityLoaded(
        val userName: String,
        val nationalCode: String
    ) : ConstructionInsurancePartialState
    data class PagingChanged(
        val items: ImmutableList<ConstructionFilePR>,
        val isLoadingFirstPage: Boolean,
        val isLoadingNextPage: Boolean,
        val endReached: Boolean,
        val error: String?,
    ) : ConstructionInsurancePartialState
    data class AppliedQueryChanged(
        val appliedFileNo: String = "",
        val appliedReqNo: String = "",
        val appliedWorkshopId: String = "",
        val appliedBranchCode: String = "",
    ) : ConstructionInsurancePartialState
    data class SearchQueriesChanged(
        val fileNo: String,
        val reqNo: String,
        val workshopId: String,
        val branchCode: String
    ) : ConstructionInsurancePartialState
    data class SearchExpandedToggled(val expanded: Boolean) : ConstructionInsurancePartialState
    data class Error(val message: String) : ConstructionInsurancePartialState

    data class NoticeVisibilityToggled(val isVisible: Boolean) : ConstructionInsurancePartialState
}
