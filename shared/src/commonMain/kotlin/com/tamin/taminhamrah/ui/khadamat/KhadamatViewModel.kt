package com.tamin.taminhamrah.ui.khadamat

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.ui.khadamat.contract.*
import com.tamin.taminhamrah.ui.components.khadamat.KhadamatTab
import com.tamin.taminhamrah.useCases.common.GetMainMenuUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class KhadamatViewModel(
    private val getMainMenuUseCase: GetMainMenuUseCase,
    private val featureManager: FeatureManager
) : BaseViewModel<KhadamatUiState, KhadamatUiState.KhadamatPartialState, KhadamatEvent, KhadamatIntent>(
    initialState = KhadamatUiState(isLoading = true)
) {

    init {
        sendIntent(KhadamatIntent.LoadMenu)
    }

    override fun handleIntent(intent: KhadamatIntent): Flow<KhadamatUiState.KhadamatPartialState> = flow {
        when (intent) {
            is KhadamatIntent.LoadMenu -> {
                emit(KhadamatUiState.KhadamatPartialState.Loading(true))
                emitAll(
                    getMainMenuUseCase("1.0.0", false).map {
                        KhadamatUiState.KhadamatPartialState.MenuLoaded(it)
                    }
                )
            }
            is KhadamatIntent.OnTabSelected -> {
                emit(KhadamatUiState.KhadamatPartialState.TabSelected(intent.tab))
            }
            is KhadamatIntent.OnSearchQueryChanged -> {
                emit(KhadamatUiState.KhadamatPartialState.SearchQueryChanged(intent.query))
            }
            is KhadamatIntent.OnServiceClick -> {
                handleServiceClick(intent.service)
            }
        }
    }

    private suspend fun handleServiceClick(service: MainServiceDN) {
        val flag = FeatureFlag.fromId(service.id ?: return) ?: return

        val status = featureManager.getFeatureStatus(flag).first()
        when (status) {
            is FeatureStatus.Enabled -> {
                sendEvent(KhadamatEvent.NavigateToService(flag))
            }
            is FeatureStatus.Disabled -> {
                status.message?.let { sendEvent(KhadamatEvent.ShowMessage(it)) }
            }
            is FeatureStatus.TemporaryDisabled -> {
                status.message?.let { sendEvent(KhadamatEvent.ShowMessage(it)) }
            }
            is FeatureStatus.EnabledWithError -> {
                status.message?.let { sendEvent(KhadamatEvent.ShowMessage(it)) }
                sendEvent(KhadamatEvent.NavigateToService(flag))
            }
            is FeatureStatus.WebView -> {
                sendEvent(KhadamatEvent.NavigateToWeb(status.url))
            }
        }
    }

    override fun reduceState(
        currentState: KhadamatUiState,
        partialState: KhadamatUiState.KhadamatPartialState
    ): KhadamatUiState = when (partialState) {
        is KhadamatUiState.KhadamatPartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is KhadamatUiState.KhadamatPartialState.MenuLoaded -> {
            val updatedState = currentState.copy(isLoading = false, menuItems = partialState.menuItems)
            deriveFilteredServices(updatedState)
        }
        is KhadamatUiState.KhadamatPartialState.TabSelected -> {
            val updatedState = currentState.copy(selectedTab = partialState.tab)
            deriveFilteredServices(updatedState)
        }
        is KhadamatUiState.KhadamatPartialState.SearchQueryChanged -> {
            val updatedState = currentState.copy(searchQuery = partialState.query)
            deriveFilteredServices(updatedState)
        }
        is KhadamatUiState.KhadamatPartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
    }

    private fun deriveFilteredServices(state: KhadamatUiState): KhadamatUiState {
        val tabFiltered = state.menuItems.filter { service ->
            service.showRole.contains(state.selectedTab.roleId)
        }
        val queryFiltered = if (state.searchQuery.trim().isEmpty()) {
            tabFiltered
        } else {
            tabFiltered.filter { service ->
                val name = service.name ?: ""
                name.contains(state.searchQuery, ignoreCase = true)
            }
        }

        val noResults = state.searchQuery.trim().isNotEmpty() && queryFiltered.isEmpty()

        return state.copy(
            filteredServices = queryFiltered,
            showNoResultsError = noResults
        )
    }

    override fun createErrorState(message: String): KhadamatUiState.KhadamatPartialState =
        KhadamatUiState.KhadamatPartialState.Error(message)
}
