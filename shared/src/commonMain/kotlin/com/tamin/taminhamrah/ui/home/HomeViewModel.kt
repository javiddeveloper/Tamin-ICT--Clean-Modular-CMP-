package com.tamin.taminhamrah.ui.home

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.campaign.CampaignKind
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.featureStatusOf
import com.tamin.taminhamrah.ui.home.contract.*
import com.tamin.taminhamrah.useCases.agent.CheckChatAllowedUseCase
import com.tamin.taminhamrah.useCases.common.GetMainMenuUseCase
import com.tamin.taminhamrah.util.AppConfig
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class HomeViewModel(
    private val getMainMenuUseCase: GetMainMenuUseCase,
    private val featureManager: FeatureManager,
    private val checkChatAllowedUseCase: CheckChatAllowedUseCase,
) : BaseViewModel<HomeUiState, HomeUiState.HomePartialState, HomeEvent, HomeIntent>(
    initialState = HomeUiState(isLoading = true)
) {

    init {
        sendIntent(HomeIntent.LoadMenu)
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
            is HomeIntent.OnServiceClick -> {
                handleServiceClick(intent.service)
            }
            is HomeIntent.OnCampaignClick -> {
                handleFeatureClick(intent.flag)
            }
            is HomeIntent.RefreshAgentAccess -> {
                // Like the native dashboard: the answer is cached by the use case and drives the
                // assistant's entry point. A failure keeps the last known answer, so it is ignored.
                checkChatAllowedUseCase()
            }
        }
    }

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
        is  HomeUiState.HomePartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
    }

    override fun createErrorState(message: String):  HomeUiState.HomePartialState =
        HomeUiState.HomePartialState.Error(message)
}
