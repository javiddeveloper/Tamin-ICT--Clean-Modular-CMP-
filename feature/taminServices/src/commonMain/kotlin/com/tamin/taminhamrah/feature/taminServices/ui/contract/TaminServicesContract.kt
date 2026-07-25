package com.tamin.taminhamrah.feature.taminServices.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.feature.taminServices.model.RolePR


@Immutable
data class TaminServicesUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val tabs: List<RolePR> = emptyList(),
    val selectedTab: RolePR? = null,
    val menuItems: List<MainServiceDN> = emptyList(),
    val filteredServices: List<MainServiceDN> = emptyList(),
    val error: String? = null,
    val showNoResultsError: Boolean = false
) {
    sealed interface TaminServicesPartialState {
        data class Loading(val isLoading: Boolean) : TaminServicesPartialState
        data class MenuLoaded(val menuItems: List<MainServiceDN>) : TaminServicesPartialState
        data class RolesLoaded(val tabs: List<RolePR>) : TaminServicesPartialState
        data class TabSelected(val tab: RolePR) : TaminServicesPartialState
        data class SearchQueryChanged(val query: String) : TaminServicesPartialState
        data class Error(val message: String?) : TaminServicesPartialState
    }
}

sealed interface TaminServicesIntent {
    data object LoadMenu : TaminServicesIntent
    data class OnSearchQueryChanged(val query: String) : TaminServicesIntent
    data class OnTabSelected(val tab: RolePR) : TaminServicesIntent
    data class OnServiceClick(val service: MainServiceDN) : TaminServicesIntent
}

sealed interface TaminSericesEvent {
    data object NavigateBack: TaminSericesEvent
    data class ShowMessage(val message: String) : TaminSericesEvent
    data class NavigateToWeb(val url: String) : TaminSericesEvent
    data class NavigateToService(val flag: FeatureFlag) : TaminSericesEvent
}
