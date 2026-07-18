package com.tamin.taminhamrah.ui.khadamat.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.ui.components.khadamat.KhadamatTab

sealed interface TaminScreens {
    data object History : TaminScreens
    data object Workshops : TaminScreens
    data object Contracts : TaminScreens
    data object StudentInsurance : TaminScreens
    data object FreelanceInsurance : TaminScreens
    data object OptionalInsurance : TaminScreens
    data object HousewifeInsurance : TaminScreens
    data object PensionInquiry : TaminScreens
    data class WebView(val url: String) : TaminScreens
    data class ShowMessage(val message: String) : TaminScreens
}

@Immutable
data class KhadamatUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedTab: KhadamatTab = KhadamatTab.INSURED,
    val menuItems: List<MainServiceDN> = emptyList(),
    val filteredServices: List<MainServiceDN> = emptyList(),
    val error: String? = null,
    val showNoResultsError: Boolean = false
) {
    sealed interface KhadamatPartialState {
        data class Loading(val isLoading: Boolean) : KhadamatPartialState
        data class MenuLoaded(val menuItems: List<MainServiceDN>) : KhadamatPartialState
        data class TabSelected(val tab: KhadamatTab) : KhadamatPartialState
        data class SearchQueryChanged(val query: String) : KhadamatPartialState
        data class Error(val message: String?) : KhadamatPartialState
    }
}

sealed interface KhadamatIntent {
    data object LoadMenu : KhadamatIntent
    data class OnSearchQueryChanged(val query: String) : KhadamatIntent
    data class OnTabSelected(val tab: KhadamatTab) : KhadamatIntent
    data class OnServiceClick(val service: MainServiceDN) : KhadamatIntent
}

sealed interface KhadamatEvent {
    data class ShowMessage(val message: String) : KhadamatEvent
    data class NavigateToWeb(val url: String) : KhadamatEvent
    data class NavigateToService(val flag: FeatureFlag) : KhadamatEvent
}
