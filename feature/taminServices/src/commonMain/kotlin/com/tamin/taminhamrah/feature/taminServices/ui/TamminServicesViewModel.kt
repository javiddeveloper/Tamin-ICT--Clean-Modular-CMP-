package com.tamin.taminhamrah.feature.taminServices.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.feature.taminServices.model.toPR
import com.tamin.taminhamrah.feature.taminServices.ui.contract.*
import com.tamin.taminhamrah.useCases.common.GetMainMenuUseCase
import com.tamin.taminhamrah.useCases.common.GetRolesUseCase
import com.tamin.taminhamrah.util.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class TamminServicesViewModel(
    private val getRolesUseCase: GetRolesUseCase,
    private val getMainMenuUseCase: GetMainMenuUseCase,
    private val featureManager: FeatureManager
) : BaseViewModel<TaminServicesUiState, TaminServicesUiState.TaminServicesPartialState, TaminSericesEvent, TaminServicesIntent>(
    initialState = TaminServicesUiState(isLoading = true)
) {

    init {
        sendIntent(TaminServicesIntent.LoadMenu)
    }

    override fun handleIntent(intent: TaminServicesIntent): Flow<TaminServicesUiState.TaminServicesPartialState> = flow {
        when (intent) {
            is TaminServicesIntent.LoadMenu -> {
                emit(TaminServicesUiState.TaminServicesPartialState.Loading(true))
                emitAll(
                    kotlinx.coroutines.flow.merge(
                        getRolesUseCase().map { roles ->
                            val tabs = roles.map { it.toPR() }
                            TaminServicesUiState.TaminServicesPartialState.RolesLoaded(tabs)
                        },
                        getMainMenuUseCase(AppConfig.versionName, false).map {
                            TaminServicesUiState.TaminServicesPartialState.MenuLoaded(it)
                        }
                    )
                )
            }
            is TaminServicesIntent.OnTabSelected -> {
                emit(TaminServicesUiState.TaminServicesPartialState.TabSelected(intent.tab))
            }
            is TaminServicesIntent.OnSearchQueryChanged -> {
                emit(TaminServicesUiState.TaminServicesPartialState.SearchQueryChanged(intent.query))
            }
            is TaminServicesIntent.OnServiceClick -> {
                handleServiceClick(intent.service)
            }
        }
    }

    private suspend fun handleServiceClick(service: MainServiceDN) {
        val flag = FeatureFlag.fromId(service.id ?: return) ?: return

        val status = featureManager.getFeatureStatus(flag).first()
        when (status) {
            is FeatureStatus.Enabled -> {
                if (flag == FeatureFlag.LAWS) {
                    service.url?.let { sendEvent(TaminSericesEvent.NavigateToWeb(it)) }
                } else {
                    sendEvent(TaminSericesEvent.NavigateToService(flag))
                }
            }
            is FeatureStatus.Disabled -> {
                status.message?.let { sendEvent(TaminSericesEvent.ShowMessage(it)) }
            }
            is FeatureStatus.TemporaryDisabled -> {
                status.message?.let { sendEvent(TaminSericesEvent.ShowMessage(it)) }
            }
            is FeatureStatus.EnabledWithError -> {
                status.message?.let { sendEvent(TaminSericesEvent.ShowMessage(it)) }
                sendEvent(TaminSericesEvent.NavigateToService(flag))
            }
            is FeatureStatus.WebView -> {
                sendEvent(TaminSericesEvent.NavigateToWeb(status.url))
            }
        }
    }

    override fun reduceState(
        currentState: TaminServicesUiState,
        partialState: TaminServicesUiState.TaminServicesPartialState
    ): TaminServicesUiState = when (partialState) {
        is TaminServicesUiState.TaminServicesPartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is TaminServicesUiState.TaminServicesPartialState.RolesLoaded -> {
            val updatedState = currentState.copy(
                tabs = partialState.tabs,
                selectedTab = currentState.selectedTab ?: partialState.tabs.firstOrNull()
            )
            deriveFilteredServices(updatedState)
        }
        is TaminServicesUiState.TaminServicesPartialState.MenuLoaded -> {
            val updatedState = currentState.copy(isLoading = false, menuItems = partialState.menuItems)
            deriveFilteredServices(updatedState)
        }
        is TaminServicesUiState.TaminServicesPartialState.TabSelected -> {
            val updatedState = currentState.copy(selectedTab = partialState.tab)
            deriveFilteredServices(updatedState)
        }
        is TaminServicesUiState.TaminServicesPartialState.SearchQueryChanged -> {
            val updatedState = currentState.copy(searchQuery = partialState.query)
            deriveFilteredServices(updatedState)
        }
        is TaminServicesUiState.TaminServicesPartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
    }

    private fun deriveFilteredServices(state: TaminServicesUiState): TaminServicesUiState {
        val tabFiltered = state.menuItems.filter { service ->
            state.selectedTab?.let { tab ->
                service.showRole.contains(tab.roleId)
            } ?: false
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

    override fun createErrorState(message: String): TaminServicesUiState.TaminServicesPartialState =
        TaminServicesUiState.TaminServicesPartialState.Error(message)
}
