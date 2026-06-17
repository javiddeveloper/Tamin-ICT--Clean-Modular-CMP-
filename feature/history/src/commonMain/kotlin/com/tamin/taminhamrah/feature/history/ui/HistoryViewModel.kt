package com.tamin.taminhamrah.feature.history.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryUiState
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryUiState.PartialState
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryIntent
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryEvent
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import com.tamin.taminhamrah.mapper.history.toPresentation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HistoryViewModel(
    private val getTalfighInfosUseCase: GetTalfighInfosUseCase,
    private val getDastmozdInfosUseCase: GetDastmozdInfosUseCase
) : BaseViewModel<HistoryUiState, PartialState, HistoryEvent, HistoryIntent>(
    initialState = HistoryUiState()
) {

    override fun handleIntent(intent: HistoryIntent): Flow<PartialState> {
        return when (intent) {
            is HistoryIntent.LoadTalfighiData -> handleLoadTalfighiData()
            is HistoryIntent.LoadDastmozdData -> handleLoadDastmozdData()
        }
    }

    private fun handleLoadTalfighiData(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val response = getTalfighInfosUseCase()
            emit(PartialState.TalfighiDataLoaded(response.list.toPresentation()))
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadDastmozdData(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val response = getDastmozdInfosUseCase()
            emit(PartialState.DastmozdDataLoaded(response.list.toPresentation()))
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    override fun reduceState(
        currentState: HistoryUiState,
        partialState: PartialState
    ): HistoryUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.TalfighiDataLoaded -> currentState.copy(
            isLoading = false,
            talfighInfos = partialState.list
        )
        is PartialState.DastmozdDataLoaded -> currentState.copy(
            isLoading = false,
            dastmozdInfos = partialState.list
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
