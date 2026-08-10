package com.tamin.taminhamrah.feature.history.ui.jobinfo

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.mapper.history.toPresentation
import com.tamin.taminhamrah.useCases.history.GetHistoryJobInfosUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HistoryJobInfoViewModel(
    private val getHistoryJobInfosUseCase: GetHistoryJobInfosUseCase
) : BaseViewModel<HistoryJobInfoUiState, HistoryJobInfoUiState.PartialState, HistoryJobInfoEvent, HistoryJobInfoIntent>(
    initialState = HistoryJobInfoUiState()
) {
    init {
        sendIntent(HistoryJobInfoIntent.Load)
    }

    override fun handleIntent(intent: HistoryJobInfoIntent): Flow<HistoryJobInfoUiState.PartialState> {
        return when (intent) {
            HistoryJobInfoIntent.Load-> loadJobInfos()
        }
    }

    private fun loadJobInfos(): Flow<HistoryJobInfoUiState.PartialState> = flow {
        emit(HistoryJobInfoUiState.PartialState.Loading(true))
        try {
            getHistoryJobInfosUseCase().collect { result ->
                emit(
                    HistoryJobInfoUiState.PartialState.JobInfosLoaded(
                        result.list?.toPresentation() ?: emptyList()
                    )
                )
            }
        } catch (e: Exception) {
            val message = e.message ?: "در دریافت عناوین شغلی خطایی رخ داده است"
            sendEvent(HistoryJobInfoEvent.ShowToast(message))
            emit(HistoryJobInfoUiState.PartialState.Error(message))
        } finally {
            emit(HistoryJobInfoUiState.PartialState.Loading(false))
        }
    }

    override fun reduceState(
        currentState: HistoryJobInfoUiState,
        partialState: HistoryJobInfoUiState.PartialState
    ): HistoryJobInfoUiState = when (partialState) {
        is HistoryJobInfoUiState.PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            error = null
        )
        is HistoryJobInfoUiState.PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
        is HistoryJobInfoUiState.PartialState.JobInfosLoaded -> currentState.copy(
            isLoading = false,
            jobInfos = partialState.list,
            error = null
        )
    }

    override fun createErrorState(message: String): HistoryJobInfoUiState.PartialState =
        HistoryJobInfoUiState.PartialState.Error(message)
}
