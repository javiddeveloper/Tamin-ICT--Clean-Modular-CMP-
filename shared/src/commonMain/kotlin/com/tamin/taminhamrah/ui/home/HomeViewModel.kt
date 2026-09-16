package com.tamin.taminhamrah.ui.home

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.campaign.CampaignKind
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.featureStatusOf
import com.tamin.taminhamrah.mapper.history.toPresentation
import com.tamin.taminhamrah.model.history.HistorySummaryPR
import com.tamin.taminhamrah.model.history.toHistorySummary
import com.tamin.taminhamrah.ui.home.contract.*
import com.tamin.taminhamrah.useCases.common.GetMainMenuUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import com.tamin.taminhamrah.util.AppConfig
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class HomeViewModel(
    private val getMainMenuUseCase: GetMainMenuUseCase,
    private val getTalfighInfosUseCase: GetTalfighInfosUseCase,
    private val featureManager: FeatureManager
) : BaseViewModel<HomeUiState, HomeUiState.HomePartialState, HomeEvent, HomeIntent>(
    initialState = HomeUiState(isLoading = true)
) {

    init {
        sendIntent(HomeIntent.LoadMenu)
        // Beside the menu, not after it: `BaseViewModel` merges intents rather than queueing them,
        // so the card fills in whenever سوابق answers instead of waiting on the service list.
        sendIntent(HomeIntent.LoadHistorySummary)
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
            is HomeIntent.LoadHistorySummary -> {
                emit(HomeUiState.HomePartialState.HistorySummaryLoaded(loadHistorySummary()))
            }
            is HomeIntent.OnServiceClick -> {
                handleServiceClick(intent.service)
            }
            is HomeIntent.OnCampaignClick -> {
                handleFeatureClick(intent.flag)
            }
            is HomeIntent.OnHistorySummaryClick -> {
                handleFeatureClick(FeatureFlag.WAGE_AND_HISTORY)
            }
        }
    }

    /**
     * The newest year on record, or null when there is none to show.
     *
     * Failure is not raised: the repository already falls back to the last successful load, so what
     * reaches here is a person the service will not answer for — a کارفرما or a مستمری‌بگیر has no
     * premiums of their own — or an outage. Neither is worth putting an error on the home page for,
     * and both mean the same thing for this card: leave it out.
     */
    private suspend fun loadHistorySummary(): HistorySummaryPR? = try {
        getTalfighInfosUseCase().list?.toPresentation()?.toHistorySummary()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
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
        is  HomeUiState.HomePartialState.HistorySummaryLoaded -> currentState.copy(
            isHistorySummaryLoading = false,
            historySummary = partialState.summary
        )
        is  HomeUiState.HomePartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
    }

    override fun createErrorState(message: String):  HomeUiState.HomePartialState =
        HomeUiState.HomePartialState.Error(message)
}
