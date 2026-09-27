package com.tamin.taminhamrah.ui.home.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.history.HistorySummaryPR
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
    /**
     * خلاصهٔ سابقه for the newest year on record, or null when there is none to summarize — a
     * person with no insured year, or one the service refuses to answer for at all (a کارفرما and a
     * مستمری‌بگیر have no premiums of their own).
     */
    val historySummary: HistorySummaryPR? = null,
    /**
     * The summary's own load, which runs beside the menu's rather than inside it.
     *
     * Separate from [isLoading] because they finish at different times and mean different things:
     * the menu decides whether the page has anything on it, while this decides only whether the one
     * card shows its skeleton.
     */
    val isHistorySummaryLoading: Boolean = true,
    /**
     * Whether the summary's load failed on the way out rather than answering.
     *
     * Only a connection failure sets this, and only when nothing was cached to fall back to. The
     * service answering "nothing for this person" is [historySummary] `null` instead: trying that
     * again cannot change it, and a retry row that never goes away would claim otherwise.
     */
    val historySummaryFailed: Boolean = false,
    val error: String? = null,
) {
    sealed interface HomePartialState {
        data class Loading(val isLoading: Boolean) : HomePartialState
        data class SectionSelected(val section: HomeServiceSection) : HomePartialState
        data class HomeContentLoaded(val content: HomeContentDN?) : HomePartialState
        data class AgentAvailability(val enabled: Boolean) : HomePartialState
        /** The summary is being fetched — on first load, and again on every retry. */
        data object HistorySummaryLoading : HomePartialState
        /**
         * Null is an answer: the load finished and there is no year to show.
         *
         * [failed] separates the two ways of having no year — a connection that never delivered
         * one, which a retry can fix, from a service that has none to give, which it cannot.
         */
        data class HistorySummaryLoaded(
            val summary: HistorySummaryPR?,
            val failed: Boolean = false,
        ) : HomePartialState
        data class Error(val message: String?) : HomePartialState
    }
}

sealed interface HomeIntent {
    /** Re-asks whether this user may chat with the assistant; runs whenever home is shown. */
    object RefreshAgentAccess : HomeIntent
    object LoadHeader : HomeIntent
    object LoadHistorySummary : HomeIntent
    object LoadLastRequests : HomeIntent
    /** Retries the offline-first sync after it failed and left [HomeUiState.homeContent] empty. */
    object Retry : HomeIntent
    data class OnServiceClick(val service: MainServiceDN) : HomeIntent
    data class OnCampaignClick(val flag: FeatureFlag) : HomeIntent
    data class OnSectionSelected(val section: HomeServiceSection) : HomeIntent
    /** Anywhere on خلاصهٔ سابقه — the card, its year pill and «جزئیات ماه‌به‌ماه» all open سوابق. */
    object OnHistorySummaryClick : HomeIntent
    /** The header's support icon — dials 1420, same as پروفایل › پشتیبانی. */
    object OnSupportClick : HomeIntent
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
