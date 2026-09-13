package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFilePR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class ConstructionInsuranceState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val items: ImmutableList<ConstructionFilePR> = persistentListOf(),
    val error: String? = null,
    val isSearchExpanded: Boolean = false,
    val fileNoQuery: String = "",
    val reqNoQuery: String = "",
    val workshopIdQuery: String = "",
    val branchCodeQuery: String = "",
    val isOfflineData: Boolean = false,
    val userName: String = "",
    val nationalCode: String = "",
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
    data class OnActionClick(val item: ConstructionFilePR) : ConstructionInsuranceIntent
}

sealed interface ConstructionInsuranceEvent {
    data class ShowToast(val message: String) : ConstructionInsuranceEvent
    data class NavigateToDetails(val item: ConstructionFilePR) : ConstructionInsuranceEvent
}

sealed interface ConstructionInsurancePartialState {
    data class Loading(val isLoading: Boolean) : ConstructionInsurancePartialState
    data class IdentityLoaded(
        val userName: String,
        val nationalCode: String
    ) : ConstructionInsurancePartialState
    data class DataLoaded(
        val items: ImmutableList<ConstructionFilePR>
    ) : ConstructionInsurancePartialState
    data class SearchQueriesChanged(
        val fileNo: String,
        val reqNo: String,
        val workshopId: String,
        val branchCode: String
    ) : ConstructionInsurancePartialState
    data class SearchExpandedToggled(val expanded: Boolean) : ConstructionInsurancePartialState
    data class Error(val message: String) : ConstructionInsurancePartialState
}
