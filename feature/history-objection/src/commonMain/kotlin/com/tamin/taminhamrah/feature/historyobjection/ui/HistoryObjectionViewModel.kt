package com.tamin.taminhamrah.feature.historyobjection.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionEvent
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionIntent
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionUiState
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionUiState.PartialState
import com.tamin.taminhamrah.mapper.historyObjection.toPresentation
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.historyObjection.CheckHistoryObjectionStatusNotExistUseCase
import com.tamin.taminhamrah.useCases.historyObjection.DeleteHistoryObjectionNotExistRequestUseCase
import com.tamin.taminhamrah.useCases.historyObjection.GetHistoryObjectionNotExistRequestsUseCase
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.history_objection_delete_missing_data_error

class HistoryObjectionViewModel(
    private val checkHistoryObjectionStatusNotExistUseCase: CheckHistoryObjectionStatusNotExistUseCase,
    private val getHistoryObjectionNotExistRequestsUseCase: GetHistoryObjectionNotExistRequestsUseCase,
    private val deleteHistoryObjectionNotExistRequestUseCase: DeleteHistoryObjectionNotExistRequestUseCase,
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

        is HistoryObjectionIntent.OnDeleteNotExistRequestClicked -> flow {
            emit(PartialState.DeleteConfirmationShown(intent.requestNumber, intent.rowIndex))
        }

        HistoryObjectionIntent.OnDeleteConfirmationDismissed -> flow {
            emit(PartialState.DeleteConfirmationHidden)
        }

        is HistoryObjectionIntent.OnDeleteConfirmed -> handleDeleteConfirmed(intent.requestNumber, intent.rowIndex)

        is HistoryObjectionIntent.OnDescriptionChanged -> flow {
            emit(PartialState.DescriptionChanged(intent.description))
        }

        // TODO(history-objection): no double-tap guard yet — safe today only because
        // SubmitRequested is a no-op; add an isSubmitting guard once submit calls a real
        // endpoint (docs/vault/History-Objection.md notes this was the original intent).
        HistoryObjectionIntent.OnSubmitClicked -> {
            sendEvent(HistoryObjectionEvent.SubmitRequested)
            emptyFlow()
        }

        HistoryObjectionIntent.OnErrorDismissed -> flow {
            emit(PartialState.ErrorDismissed)
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

    private fun handleDeleteConfirmed(requestNumber: String, rowIndex: String?): Flow<PartialState> = flow {
        // flatMapMerge runs intents concurrently — drop a repeated confirm while the first
        // delete is still in flight rather than firing a second deletenotexist request.
        if (uiState.value.isDeleting) return@flow
        emit(PartialState.DeleteConfirmationHidden)

        if (rowIndex == null) {
            emit(PartialState.Error(getString(Res.string.history_objection_delete_missing_data_error)))
            return@flow
        }

        emit(PartialState.Deleting(true))
        try {
            deleteHistoryObjectionNotExistRequestUseCase(requestNumber, rowIndex).first()
            emitAll(loadHistoryObjectionData())
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.Deleting(false))
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
        // isLoading is left as-is here: checkStatusNotExist()/loadNotExistRequests() run
        // concurrently via merge() and loadHistoryObjectionData() always emits the authoritative
        // Loading(false) once both finish — clearing it on the first error would dismiss the
        // skeleton while the other flow is still in flight.
        is PartialState.Error -> currentState.copy(error = partialState.message)
        PartialState.ErrorDismissed -> currentState.copy(error = null)
        is PartialState.DeleteConfirmationShown -> currentState.copy(
            deleteConfirmationRequestNumber = partialState.requestNumber,
            deleteConfirmationRowIndex = partialState.rowIndex,
        )
        PartialState.DeleteConfirmationHidden -> currentState.copy(
            deleteConfirmationRequestNumber = null,
            deleteConfirmationRowIndex = null,
        )
        is PartialState.Deleting -> currentState.copy(isDeleting = partialState.isDeleting)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
