package com.tamin.taminhamrah.ui.home

import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.mapper.home.hasActiveRelation
import com.tamin.taminhamrah.mapper.home.toDarmanCoveredOrNull
import com.tamin.taminhamrah.mapper.identity.toPresentation
import com.tamin.taminhamrah.mapper.userRequest.toPresentation
import com.tamin.taminhamrah.model.campaign.CampaignKind
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.featureStatusOf
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.ui.home.contract.*
import com.tamin.taminhamrah.useCases.common.GetMainMenuUseCase
import com.tamin.taminhamrah.repository.home.HomeRepository
import kotlinx.coroutines.launch
import com.tamin.taminhamrah.util.AppConfig
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class HomeViewModel(
    private val getMainMenuUseCase: GetMainMenuUseCase,
    private val featureManager: FeatureManager,
    private val homeRepository: HomeRepository,
    private val tokenStoreManager: TokenStoreManager,
) : BaseViewModel<HomeUiState, HomeUiState.HomePartialState, HomeEvent, HomeIntent>(
    initialState = HomeUiState(isLoading = true)
) {

    init {
        sendIntent(HomeIntent.LoadMenu)
        sendIntent(HomeIntent.LoadHeader)
        // trigger background fetch for offline first and react to login state
        viewModelScope.launch {
            tokenStoreManager.tokenValidFlow()
                .distinctUntilChanged()
                .collectLatest {
                    try {
                        homeRepository.syncHomeContent()
                    } catch (e: Exception) {
                        // Ignore sync errors and fallback to cached data
                    }
                }
        }
    }

    override fun handleIntent(intent: HomeIntent): Flow<HomeUiState.HomePartialState> = flow {
        when (intent) {
            is HomeIntent.LoadMenu -> {
                emit(HomeUiState.HomePartialState.Loading(true))
                emitAll(
                    getMainMenuUseCase(AppConfig.versionName, false).map { menu ->
                        HomeUiState.HomePartialState.MenuLoaded(menu, menu.visibleCampaigns())
                    }
                )
            }
            is HomeIntent.LoadHeader -> {
                emitAll(
                    merge(homeContentFlow(), agentAvailabilityFlow())
                )
            }
            is HomeIntent.LoadLastRequests -> {
                // Deprecated: Requests are now handled by LoadHeader via HomeRepository
            }
            is HomeIntent.OnServiceClick -> {
                handleServiceClick(intent.service)
            }
            is HomeIntent.OnCampaignClick -> {
                handleFeatureClick(intent.flag)
            }
            is HomeIntent.OnSectionSelected -> {
                emit(HomeUiState.HomePartialState.SectionSelected(intent.section))
            }
        }
    }

    /**
     * The header calls are independent of the menu and of each other, and a failure in any one of
     * them must only cost that one chip — never the whole screen — so each flow swallows its own
     * error instead of routing through [createErrorState].
     */
    private fun homeContentFlow(): Flow<HomeUiState.HomePartialState> =
        homeRepository.getHomeContent()
            .map { HomeUiState.HomePartialState.HomeContentLoaded(it) }
            .catch { }

    private fun agentAvailabilityFlow(): Flow<HomeUiState.HomePartialState> =
        featureManager.getFeatureStatus(FeatureFlag.AGENT)
            .map { status ->
                HomeUiState.HomePartialState.AgentAvailability(
                    status is FeatureStatus.Enabled || status is FeatureStatus.EnabledWithError
                )
            }
            .catch { }



    private suspend fun handleServiceClick(service: MainServiceDN) {
        val flag = FeatureFlag.fromId(service.id) ?: return
        handleFeatureClick(flag)
    }

    /**
     * The one gate every tap on this screen goes through — a service card or a campaign card.
     * Routing around it would let a card open a service the server has switched off.
     */
    private suspend fun handleFeatureClick(flag: FeatureFlag) {
        when (val status = featureManager.getFeatureStatus(flag).first()) {
            is FeatureStatus.Enabled -> {
                sendEvent(HomeEvent.NavigateToService(flag))
            }
            is FeatureStatus.Disabled -> {
                status.message?.let { sendEvent(HomeEvent.ShowMessage(it)) }
            }
            is FeatureStatus.TemporaryDisabled -> {
                status.message?.let { sendEvent(HomeEvent.ShowMessage(it)) }
            }
            is FeatureStatus.EnabledWithError -> {
                status.message?.let { sendEvent(HomeEvent.ShowMessage(it)) }
                sendEvent(HomeEvent.NavigateToService(flag))
            }
            is FeatureStatus.WebView -> {
                sendEvent(HomeEvent.NavigateToWeb(status.url))
            }
        }
    }

    /**
     * Read off the menu that has just arrived rather than asked of [featureManager] per campaign:
     * `getFeatureStatus` refetches the menu on every call, so three campaigns would cost three
     * extra round trips for an answer this list already holds.
     */
    private fun List<MainServiceDN>.visibleCampaigns(): ImmutableList<CampaignKind> =
        CampaignKind.entries
            .filter { featureStatusOf(it.flag).opensSomething }
            .toImmutableList()

    override fun reduceState(
        currentState: HomeUiState,
        partialState: HomeUiState.HomePartialState
    ): HomeUiState = when (partialState) {
        is  HomeUiState.HomePartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is  HomeUiState.HomePartialState.MenuLoaded -> currentState.copy(
            isLoading = false,
            menuItems = partialState.menuItems,
            campaigns = partialState.campaigns
        )
        is  HomeUiState.HomePartialState.SectionSelected -> currentState.copy(
            selectedSection = partialState.section
        )
        is  HomeUiState.HomePartialState.HomeContentLoaded -> currentState.copy(
            homeContent = partialState.content
        )
        is  HomeUiState.HomePartialState.AgentAvailability -> currentState.copy(
            isAgentEnabled = partialState.enabled
        )
        is  HomeUiState.HomePartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
    }

    override fun createErrorState(message: String):  HomeUiState.HomePartialState =
        HomeUiState.HomePartialState.Error(message)
}
