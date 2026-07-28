package com.tamin.taminhamrah.feature.contracts.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.feature.contracts.ui.contract.ContractsEvent
import com.tamin.taminhamrah.feature.contracts.ui.contract.ContractsIntent
import com.tamin.taminhamrah.feature.contracts.ui.contract.ContractsUiState
import com.tamin.taminhamrah.feature.contracts.ui.contract.ContractsUiState.PartialState
import com.tamin.taminhamrah.mapper.contracts.toPresentation
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.useCases.contracts.GetContractsUseCase
import com.tamin.taminhamrah.useCases.common.GetMainMenuUseCase
import com.tamin.taminhamrah.util.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ContractsViewModel(
    private val getContractsUseCase: GetContractsUseCase,
    private val getMainMenuUseCase: GetMainMenuUseCase,
    private val featureManager: FeatureManager
) : BaseViewModel<ContractsUiState, PartialState, ContractsEvent, ContractsIntent>(
    initialState = ContractsUiState()
) {

    override fun handleIntent(intent: ContractsIntent): Flow<PartialState> {
        return when (intent) {
            ContractsIntent.LoadContracts -> handleLoadContracts()
            is ContractsIntent.OnServiceClick -> handleServiceClickFlow(intent.service)
        }
    }

    private fun handleLoadContracts(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))

        val contractIds = listOf(33, 34, 36, 37, 39)

        try {
            val menuItems = getMainMenuUseCase(AppConfig.versionName, false).first()
            val contractOptions = menuItems.filter { it.id in contractIds }
            emit(PartialState.OptionsLoaded(contractOptions))

            getContractsUseCase().collect { contracts ->
                emit(PartialState.ContractsLoaded(contracts.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleServiceClickFlow(service: MainServiceDN): Flow<PartialState> = flow {
        val flag = FeatureFlag.fromId(service.id) ?: return@flow

        val status = featureManager.getFeatureStatus(flag).first()
        when (status) {
            is FeatureStatus.Enabled -> {
                sendEvent(ContractsEvent.NavigateToService(flag))
            }
            is FeatureStatus.Disabled -> {
                status.message?.let { sendEvent(ContractsEvent.ShowToast(it)) }
            }
            is FeatureStatus.TemporaryDisabled -> {
                status.message?.let { sendEvent(ContractsEvent.ShowToast(it)) }
            }
            is FeatureStatus.EnabledWithError -> {
                status.message?.let { sendEvent(ContractsEvent.ShowToast(it)) }
                sendEvent(ContractsEvent.NavigateToService(flag))
            }
            is FeatureStatus.WebView -> {
                sendEvent(ContractsEvent.NavigateToWeb(status.url))
            }
        }
    }

    override fun reduceState(
        currentState: ContractsUiState,
        partialState: PartialState,
    ): ContractsUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message,
        )
        is PartialState.ContractsLoaded -> currentState.copy(
            isLoading = false,
            contracts = partialState.contracts,
        )
        is PartialState.OptionsLoaded -> currentState.copy(
            newContractOptions = partialState.options
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
