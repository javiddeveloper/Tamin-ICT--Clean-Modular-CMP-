package com.tamin.taminhamrah.ui.home

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.ui.home.contract.*
import com.tamin.taminhamrah.useCases.common.GetMainMenuUseCase
import com.tamin.taminhamrah.util.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class HomeViewModel(
    private val getMainMenuUseCase: GetMainMenuUseCase,
    private val featureManager: FeatureManager
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
                    getMainMenuUseCase(AppConfig.versionName, false).map {
                        HomeUiState.HomePartialState.MenuLoaded(it)
                    }
                )
            }
            is HomeIntent.OnServiceClick -> {
                handleServiceClick(intent.service)
            }
        }
    }

    private suspend fun handleServiceClick(service: MainServiceDN) {
        val flag = FeatureFlag.fromId(service.id) ?: return

        val status = featureManager.getFeatureStatus(flag).first()
        when (status) {
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
        is  HomeUiState.HomePartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is  HomeUiState.HomePartialState.MenuLoaded -> currentState.copy(
            isLoading = false,
            menuItems = partialState.menuItems
        )
        is  HomeUiState.HomePartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
    }

    override fun createErrorState(message: String):  HomeUiState.HomePartialState =
        HomeUiState.HomePartialState.Error(message)
}
