package com.tamin.taminhamrah.ui.home.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.home.HomeContentDN
import com.tamin.taminhamrah.model.home.HomeServiceSection

@Immutable
data class HomeUiState(
    val isLoading: Boolean = false,
    /**
     * Which «دسترسی سریع» chip is selected. The rendered chip list and the grid under it are
     * derived in the screen from [homeContent]'s cached quick-access rows via `toHomeSections()`;
     * this is only the selection. If the selected section resolves empty (so it isn't in the chip
     * row), the screen falls back to the first non-empty section.
     */
    val selectedSection: HomeServiceSection = HomeServiceSection.FREQUENT,
    /** Unified offline-first data model containing UserInfo, Requests, Stories, Campaigns,
     *  QuickAccess and SpecialServices — the sole source for everything below the header. */
    val homeContent: HomeContentDN? = null,
    /** Whether `FeatureFlag.AGENT` is on — gates the ask-bar and suggestion chips in the header. */
    val isAgentEnabled: Boolean = false,
    val error: String? = null,
){
    sealed interface HomePartialState {
        data class Loading(val isLoading: Boolean) : HomePartialState
        data class SectionSelected(val section: HomeServiceSection) : HomePartialState
        data class HomeContentLoaded(val content: HomeContentDN?) : HomePartialState
        data class AgentAvailability(val enabled: Boolean) : HomePartialState
        data class Error(val message: String?) : HomePartialState
    }
}



sealed interface HomeIntent {
    /** Re-asks whether this user may chat with the assistant; runs whenever home is shown. */
    object RefreshAgentAccess : HomeIntent
    object LoadHeader : HomeIntent
    object LoadLastRequests : HomeIntent
    /** Retries the offline-first sync after it failed and left [HomeUiState.homeContent] empty. */
    object Retry : HomeIntent
    data class OnServiceClick(val service: MainServiceDN) : HomeIntent
    data class OnCampaignClick(val flag: FeatureFlag) : HomeIntent
    data class OnSectionSelected(val section: HomeServiceSection) : HomeIntent
}

sealed interface HomeEvent {
    data class ShowMessage(val message: String) : HomeEvent
    data class NavigateToWeb(val url: String) : HomeEvent
    data class NavigateToService(val flag: FeatureFlag) : HomeEvent
    data class NavigateToUserRequestDetail(
        val requestId: Long,
        val refCode: String,
        val requestTypeId: Long,
        val title: String,
        val referenceId: String = "",
    ) : HomeEvent
}
