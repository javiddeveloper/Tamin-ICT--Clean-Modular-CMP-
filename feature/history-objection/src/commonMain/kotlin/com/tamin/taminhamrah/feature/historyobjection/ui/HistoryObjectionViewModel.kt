package com.tamin.taminhamrah.feature.historyobjection.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionEvent
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionIntent
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionUiState
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionUiState.PartialState
import com.tamin.taminhamrah.mapper.historyObjection.toPresentation
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.historyObjection.CheckHistoryObjectionStatusNotExistUseCase
import com.tamin.taminhamrah.useCases.historyObjection.ConfirmHistoryObjectionNotExistUseCase
import com.tamin.taminhamrah.useCases.historyObjection.DeleteHistoryObjectionNotExistRequestUseCase
import com.tamin.taminhamrah.useCases.historyObjection.FinalConfirmHistoryObjectionNotExistUseCase
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
import taminx.core.core_ui.history_objection_confirm_send_rejected_error
import taminx.core.core_ui.history_objection_delete_missing_data_error

class HistoryObjectionViewModel(
    private val checkHistoryObjectionStatusNotExistUseCase: CheckHistoryObjectionStatusNotExistUseCase,
    private val getHistoryObjectionNotExistRequestsUseCase: GetHistoryObjectionNotExistRequestsUseCase,
    private val deleteHistoryObjectionNotExistRequestUseCase: DeleteHistoryObjectionNotExistRequestUseCase,
    private val confirmHistoryObjectionNotExistUseCase: ConfirmHistoryObjectionNotExistUseCase,
    private val finalConfirmHistoryObjectionNotExistUseCase: FinalConfirmHistoryObjectionNotExistUseCase,
) : BaseViewModel<HistoryObjectionUiState, PartialState, HistoryObjectionEvent, HistoryObjectionIntent>(
    initialState = HistoryObjectionUiState()
) {

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
            sendEvent(HistoryObjectionEvent.NavigateToEditNotExistRequest(intent.requestNumber, intent.rowIndex))
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

        HistoryObjectionIntent.OnSubmitClicked -> flow {
            emit(PartialState.SubmitConfirmationShown)
        }

        HistoryObjectionIntent.OnSubmitConfirmationDismissed -> flow {
            emit(PartialState.SubmitConfirmationDismissed)
        }

        HistoryObjectionIntent.OnSubmitConfirmed -> handleSubmitConfirmed()

        HistoryObjectionIntent.OnTrackingNumberAcknowledged -> flow {
            emit(PartialState.TrackingNumberDismissed)
            emitAll(loadHistoryObjectionData())
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

    private fun handleSubmitConfirmed(): Flow<PartialState> = flow {
        if (uiState.value.isSubmitting) return@flow
        emit(PartialState.SubmitConfirmationDismissed)

        emit(PartialState.Submitting(true))
        try {
            val description = uiState.value.description.takeIf { it.isNotBlank() }
            val confirmed = confirmHistoryObjectionNotExistUseCase(description).first()
            if (confirmed) {
                val trackingNumber = finalConfirmHistoryObjectionNotExistUseCase().first()
                emit(PartialState.SubmitSucceeded(trackingNumber))
            } else {
                emit(PartialState.Error(getString(Res.string.history_objection_confirm_send_rejected_error)))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.Submitting(false))
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
        PartialState.SubmitConfirmationShown -> currentState.copy(showSubmitConfirmationDialog = true)
        PartialState.SubmitConfirmationDismissed -> currentState.copy(showSubmitConfirmationDialog = false)
        is PartialState.Submitting -> currentState.copy(isSubmitting = partialState.isSubmitting)
        is PartialState.SubmitSucceeded -> currentState.copy(trackingNumber = partialState.trackingNumber)
        PartialState.TrackingNumberDismissed -> currentState.copy(trackingNumber = null)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
