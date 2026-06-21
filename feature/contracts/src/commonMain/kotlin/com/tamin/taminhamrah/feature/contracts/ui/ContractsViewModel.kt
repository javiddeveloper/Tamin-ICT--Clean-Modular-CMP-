package com.tamin.taminhamrah.feature.contracts.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.contracts.ui.contract.ContractsEvent
import com.tamin.taminhamrah.feature.contracts.ui.contract.ContractsIntent
import com.tamin.taminhamrah.feature.contracts.ui.contract.ContractsUiState
import com.tamin.taminhamrah.feature.contracts.ui.contract.ContractsUiState.PartialState
import com.tamin.taminhamrah.mapper.contracts.toPresentation
import com.tamin.taminhamrah.useCases.contracts.GetContractsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ContractsViewModel(
    private val getContractsUseCase: GetContractsUseCase,
) : BaseViewModel<ContractsUiState, PartialState, ContractsEvent, ContractsIntent>(
    initialState = ContractsUiState()
) {

    override fun handleIntent(intent: ContractsIntent): Flow<PartialState> {
        return when (intent) {
            ContractsIntent.LoadContracts -> handleLoadContracts()
        }
    }

    private fun handleLoadContracts(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getContractsUseCase().collect { contracts ->
                emit(PartialState.ContractsLoaded(contracts.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
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
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
