package com.tamin.taminhamrah.feature.historyobjection.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionEvent
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionIntent
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionUiState
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionUiState.PartialState
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.historyObjection.CheckHistoryObjectionStatusNotExistUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class HistoryObjectionViewModel(
    private val checkHistoryObjectionStatusNotExistUseCase: CheckHistoryObjectionStatusNotExistUseCase,
) : BaseViewModel<HistoryObjectionUiState, PartialState, HistoryObjectionEvent, HistoryObjectionIntent>(
    initialState = HistoryObjectionUiState()
) {

    init {
        sendIntent(HistoryObjectionIntent.Load)
    }

    override fun handleIntent(intent: HistoryObjectionIntent): Flow<PartialState> = when (intent) {
        HistoryObjectionIntent.Load -> checkStatusNotExist()

        HistoryObjectionIntent.OnAddNewObjectionClicked -> {
            if (!uiState.value.hasActiveRequest) {
                sendEvent(HistoryObjectionEvent.NavigateToAddNewObjection)
            }
            emptyFlow()
        }

        HistoryObjectionIntent.OnActiveRequestDialogDismissed -> flow {
            emit(PartialState.ActiveRequestDialogDismissed)
        }
    }

    private fun checkStatusNotExist(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val hasActiveRequest = checkHistoryObjectionStatusNotExistUseCase().first()
            emit(PartialState.StatusChecked(hasActiveRequest))
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        }
        emit(PartialState.Loading(false))
    }

    override fun reduceState(
        currentState: HistoryObjectionUiState,
        partialState: PartialState,
    ): HistoryObjectionUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.StatusChecked -> currentState.copy(
            hasActiveRequest = partialState.hasActiveRequest,
            showActiveRequestDialog = partialState.hasActiveRequest,
            error = null,
        )
        PartialState.ActiveRequestDialogDismissed -> currentState.copy(showActiveRequestDialog = false)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}



