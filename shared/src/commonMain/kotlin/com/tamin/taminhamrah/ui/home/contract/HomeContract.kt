package com.tamin.taminhamrah.ui.home.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.campaign.CampaignKind
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.home.HomeServiceSection
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class HomeUiState(
    val isLoading: Boolean = false,
    val menuItems: List<MainServiceDN> = emptyList(),
    /**
     * The campaigns worth showing, in the design's order. Only the ones whose service the server
     * says will actually open survive; the rest are dropped rather than shown and blocked, because
     * a service the menu omits entirely has no message to explain itself with.
     *
     * Holds the catalogue key rather than the rendered card: there is no campaigns web service yet,
     * so the copy is bundled and resolved in the UI. When the endpoint arrives this becomes an
     * `ImmutableList<CampaignPR>` filled in from the wire and nothing below it moves.
     */
    val campaigns: ImmutableList<CampaignKind> = persistentListOf(),
    /**
     * Which «دسترسی سریع» chip is selected. The rendered chip list and the grid under it are
     * derived in the screen from [menuItems] via `toQuickAccessSections()`; this is only the
     * selection. If the selected section resolves empty (so it isn't in the chip row), the screen
     * falls back to the first non-empty section.
     */
    val selectedSection: HomeServiceSection = HomeServiceSection.FREQUENT,
    /** Header: the user's display name, `null` until [com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase] resolves. */
    val identityFullName: String? = null,
    /**
     * Header chip «بیمهٔ درمانی»: `true`/`false` once the entitlement call resolves, `null` while it
     * hasn't (or came back empty) — the chip stays hidden rather than showing a guess.
     */
    val hasDarmanCoverage: Boolean? = null,
    /** Header chip «ارتباط فعال»: `null` until the active-relation list resolves. */
    val hasActiveRelation: Boolean? = null,
    /** Whether `FeatureFlag.AGENT` is on — gates the ask-bar and suggestion chips in the header. */
    val isAgentEnabled: Boolean = false,
    val error: String? = null,
){
    sealed interface HomePartialState {
        data class Loading(val isLoading: Boolean) : HomePartialState
        data class MenuLoaded(
            val menuItems: List<MainServiceDN>,
            val campaigns: ImmutableList<CampaignKind>,
        ) : HomePartialState
        data class SectionSelected(val section: HomeServiceSection) : HomePartialState
        data class IdentityLoaded(val fullName: String) : HomePartialState
        data class DarmanCoverageLoaded(val covered: Boolean?) : HomePartialState
        data class ActiveRelationLoaded(val hasActive: Boolean) : HomePartialState
        data class AgentAvailability(val enabled: Boolean) : HomePartialState
        data class Error(val message: String?) : HomePartialState
    }
}



sealed interface HomeIntent {
    object LoadMenu : HomeIntent
    object LoadHeader : HomeIntent
    data class OnServiceClick(val service: MainServiceDN) : HomeIntent
    data class OnCampaignClick(val flag: FeatureFlag) : HomeIntent
    data class OnSectionSelected(val section: HomeServiceSection) : HomeIntent
}

sealed interface HomeEvent {
    data class ShowMessage(val message: String) : HomeEvent
    data class NavigateToWeb(val url: String) : HomeEvent
    data class NavigateToService(val flag: FeatureFlag) : HomeEvent
}
