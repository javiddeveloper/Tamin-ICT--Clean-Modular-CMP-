package com.tamin.taminhamrah.feature.historyobjection.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionEvent
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionIntent
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionUiState
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionUiState.PartialState
import com.tamin.taminhamrah.mapper.historyObjection.toPresentation
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.historyObjection.CheckHistoryObjectionStatusNotExistUseCase
import com.tamin.taminhamrah.useCases.historyObjection.GetHistoryObjectionNotExistRequestsUseCase
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

class HistoryObjectionViewModel(
    private val checkHistoryObjectionStatusNotExistUseCase: CheckHistoryObjectionStatusNotExistUseCase,
    private val getHistoryObjectionNotExistRequestsUseCase: GetHistoryObjectionNotExistRequestsUseCase,
) : BaseViewModel<HistoryObjectionUiState, PartialState, HistoryObjectionEvent, HistoryObjectionIntent>(
    initialState = HistoryObjectionUiState()
) {

    init {
        sendIntent(HistoryObjectionIntent.Load)
    }

    override fun handleIntent(intent: HistoryObjectionIntent): Flow<PartialState> = when (intent) {
        HistoryObjectionIntent.Load -> loadHistoryObjectionData()

        HistoryObjectionIntent.OnAddNewObjectionClicked -> {
            if (!uiState.value.hasActiveRequest) {
                sendEvent(HistoryObjectionEvent.NavigateToAddNewObjection)
            }
            emptyFlow()
        }

        HistoryObjectionIntent.OnActiveRequestDialogDismissed -> flow {
            emit(PartialState.ActiveRequestDialogDismissed)
        }

        is HistoryObjectionIntent.OnEditNotExistRequestClicked -> {
            sendEvent(HistoryObjectionEvent.NavigateToEditNotExistRequest(intent.requestNumber))
            emptyFlow()
        }

        is HistoryObjectionIntent.OnDeleteNotExistRequestClicked -> {
            sendEvent(HistoryObjectionEvent.ConfirmDeleteNotExistRequest(intent.requestNumber))
            emptyFlow()
        }

        is HistoryObjectionIntent.OnDescriptionChanged -> flow {
            emit(PartialState.DescriptionChanged(intent.description))
        }

        HistoryObjectionIntent.OnSubmitClicked -> {
            sendEvent(HistoryObjectionEvent.SubmitRequested)
            emptyFlow()
        }
    }

    private fun loadHistoryObjectionData(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        emitAll(merge(checkStatusNotExist(), loadNotExistRequests()))
        emit(PartialState.Loading(false))
    }

    private fun checkStatusNotExist(): Flow<PartialState> = flow {
        try {
            val hasActiveRequest = checkHistoryObjectionStatusNotExistUseCase().first()
            emit(PartialState.StatusChecked(hasActiveRequest))
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        }
    }

    private fun loadNotExistRequests(): Flow<PartialState> = flow {
        try {
            val requests = getHistoryObjectionNotExistRequestsUseCase().first().toPresentation()
            emit(PartialState.RequestsLoaded(requests.toPersistentList()))
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        }
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
        is PartialState.RequestsLoaded -> currentState.copy(
            notExistRequests = partialState.requests,
            error = null,
        )
        PartialState.ActiveRequestDialogDismissed -> currentState.copy(showActiveRequestDialog = false)
        is PartialState.DescriptionChanged -> currentState.copy(description = partialState.description)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
