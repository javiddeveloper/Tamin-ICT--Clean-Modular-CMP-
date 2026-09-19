package com.tamin.taminhamrah.ui.home

import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.mapper.history.toPresentation
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.history.HistorySummaryPR
import com.tamin.taminhamrah.model.history.toHistorySummary
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.ui.home.contract.HomeEvent
import com.tamin.taminhamrah.ui.home.contract.HomeIntent
import com.tamin.taminhamrah.ui.home.contract.HomeUiState
import com.tamin.taminhamrah.useCases.agent.CheckChatAllowedUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import com.tamin.taminhamrah.useCases.home.GetHomeContentUseCase
import com.tamin.taminhamrah.useCases.home.SyncHomeContentUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

class HomeViewModel(
    private val checkChatAllowedUseCase: CheckChatAllowedUseCase,
    private val featureManager: FeatureManager,
    private val getHomeContentUseCase: GetHomeContentUseCase,
    private val syncHomeContentUseCase: SyncHomeContentUseCase,
    private val tokenStoreManager: TokenStoreManager,
    private val getTalfighInfosUseCase: GetTalfighInfosUseCase,
) : BaseViewModel<HomeUiState, HomeUiState.HomePartialState, HomeEvent, HomeIntent>(
    initialState = HomeUiState(isLoading = true)
) {

    init {
        sendIntent(HomeIntent.LoadHeader)
        // Beside the header, not after it: `BaseViewModel` merges intents rather than queueing them,
        // so the card fills in whenever سوابق answers instead of waiting on the header.
        sendIntent(HomeIntent.LoadHistorySummary)
        // trigger background fetch for offline first and react to login state — this also covers
        // the initial sync, since tokenValidFlow() emits once immediately on subscribe
        viewModelScope.launch {
            tokenStoreManager.tokenValidFlow()
                .distinctUntilChanged()
                .collectLatest {
                    try {
                        syncHomeContentUseCase()
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        // Ignore sync errors and fallback to cached data
                    }
                }
        }
    }

    /** Fire-and-forget: failures are swallowed the same way the token-driven sync above is —
     *  [homeContentFlow] renders whatever is already cached regardless of how this call ends. */
    private fun triggerSync() {
        viewModelScope.launch {
            try {
                syncHomeContentUseCase()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Ignore; the screen falls back to cached data via homeContentFlow().
            }
        }
    }

    override fun handleIntent(intent: HomeIntent): Flow<HomeUiState.HomePartialState> = flow {
        when (intent) {
            is HomeIntent.LoadHeader -> {
                emitAll(
                    merge(homeContentFlow(), agentAvailabilityFlow())
                )
            }
            is HomeIntent.LoadHistorySummary -> {
                emit(HomeUiState.HomePartialState.HistorySummaryLoaded(loadHistorySummary()))
            }
            is HomeIntent.LoadLastRequests -> {
                // Deprecated: Requests are now handled by LoadHeader via GetHomeContentUseCase
            }
            is HomeIntent.Retry -> {
                triggerSync()
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
            is HomeIntent.RefreshAgentAccess -> {
                // Like the native dashboard: the answer is cached by the use case and drives the
                // assistant's entry point. A failure keeps the last known answer, so it is ignored.
                checkChatAllowedUseCase()
            }
            is HomeIntent.OnSectionSelected -> {
                emit(HomeUiState.HomePartialState.SectionSelected(intent.section))
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

    /**
     * The header calls are independent of the menu and of each other, and a failure in any one of
     * them must only cost that one chip — never the whole screen — so each flow swallows its own
     * error instead of routing through [createErrorState].
     */
    private fun homeContentFlow(): Flow<HomeUiState.HomePartialState> =
        getHomeContentUseCase()
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

    override fun reduceState(
        currentState: HomeUiState,
        partialState: HomeUiState.HomePartialState
    ): HomeUiState = when (partialState) {
        is HomeUiState.HomePartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is HomeUiState.HomePartialState.SectionSelected -> currentState.copy(
            selectedSection = partialState.section
        )
        is HomeUiState.HomePartialState.HomeContentLoaded -> currentState.copy(
            isLoading = false,
            homeContent = partialState.content
        )
        is HomeUiState.HomePartialState.AgentAvailability -> currentState.copy(
            isAgentEnabled = partialState.enabled
        )
        is HomeUiState.HomePartialState.HistorySummaryLoaded -> currentState.copy(
            isHistorySummaryLoading = false,
            historySummary = partialState.summary
        )
        is HomeUiState.HomePartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
    }

    override fun createErrorState(message: String): HomeUiState.HomePartialState =
        HomeUiState.HomePartialState.Error(message)
}
