package com.tamin.taminhamrah.ui.home

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
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.treatment.GetDeservedTreatmentUseCase
import com.tamin.taminhamrah.useCases.user.GetRelationTaminAllUseCase
import com.tamin.taminhamrah.useCases.userRequest.GetUserRequestsUseCase
import com.tamin.taminhamrah.util.AppConfig
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class HomeViewModel(
    private val getMainMenuUseCase: GetMainMenuUseCase,
    private val featureManager: FeatureManager,
    private val identityInfoUseCase: IdentityInfoUseCase,
    private val getDeservedTreatmentUseCase: GetDeservedTreatmentUseCase,
    private val getRelationTaminAllUseCase: GetRelationTaminAllUseCase,
    private val getUserRequestsUseCase: GetUserRequestsUseCase,
) : BaseViewModel<HomeUiState, HomeUiState.HomePartialState, HomeEvent, HomeIntent>(
    initialState = HomeUiState(isLoading = true)
) {

    init {
        sendIntent(HomeIntent.LoadMenu)
        sendIntent(HomeIntent.LoadHeader)
        sendIntent(HomeIntent.LoadLastRequests)
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
                    merge(identityFlow(), darmanFlow(), activeRelationFlow(), agentAvailabilityFlow())
                )
            }
            is HomeIntent.LoadLastRequests -> {
                emitAll(lastRequestsFlow())
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
    private fun identityFlow(): Flow<HomeUiState.HomePartialState> =
        identityInfoUseCase()
            .map { HomeUiState.HomePartialState.IdentityLoaded(it.toPresentation().fullName) }
            .catch { }

    private fun darmanFlow(): Flow<HomeUiState.HomePartialState> = flow {
        val nationalCode = identityInfoUseCase().firstOrNull()?.nationalId
        if (nationalCode.isNullOrBlank()) {
            emit(HomeUiState.HomePartialState.DarmanCoverageLoaded(null))
            return@flow
        }
        emitAll(
            getDeservedTreatmentUseCase(nationalCode)
                .map { HomeUiState.HomePartialState.DarmanCoverageLoaded(it.toDarmanCoveredOrNull()) }
        )
    }.catch { }

    private fun activeRelationFlow(): Flow<HomeUiState.HomePartialState> = flow {
        emitAll(
            getRelationTaminAllUseCase.invoke()
                .map { HomeUiState.HomePartialState.ActiveRelationLoaded(it.hasActiveRelation()) }
        )
    }.catch { }

    private fun agentAvailabilityFlow(): Flow<HomeUiState.HomePartialState> =
        featureManager.getFeatureStatus(FeatureFlag.AGENT)
            .map { status ->
                HomeUiState.HomePartialState.AgentAvailability(
                    status is FeatureStatus.Enabled || status is FeatureStatus.EnabledWithError
                )
            }
            .catch { }

    private fun lastRequestsFlow(): Flow<HomeUiState.HomePartialState> =
        getUserRequestsUseCase()
            .map { requests ->
                HomeUiState.HomePartialState.LastRequestsLoaded(
                    requests.take(3).toPresentation()
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
        is  HomeUiState.HomePartialState.IdentityLoaded -> currentState.copy(
            identityFullName = partialState.fullName
        )
        is  HomeUiState.HomePartialState.DarmanCoverageLoaded -> currentState.copy(
            hasDarmanCoverage = partialState.covered
        )
        is  HomeUiState.HomePartialState.ActiveRelationLoaded -> currentState.copy(
            hasActiveRelation = partialState.hasActive
        )
        is  HomeUiState.HomePartialState.AgentAvailability -> currentState.copy(
            isAgentEnabled = partialState.enabled
        )
        is  HomeUiState.HomePartialState.LastRequestsLoaded -> currentState.copy(
            lastRequests = partialState.requests
        )
        is  HomeUiState.HomePartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
    }

    override fun createErrorState(message: String):  HomeUiState.HomePartialState =
        HomeUiState.HomePartialState.Error(message)
}
