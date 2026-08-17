package com.tamin.taminhamrah.feature.treatment.ui.medicalConfirmations

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.treatment.ui.contract.ConfirmationsEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.ConfirmationsIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.ConfirmationsUiState
import com.tamin.taminhamrah.feature.treatment.ui.contract.ConfirmationsUiState.PartialState
import com.tamin.taminhamrah.mapper.treatment.toPresentation
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.treatment.GetMedicalConfirmationPDFUseCase
import com.tamin.taminhamrah.useCases.treatment.GetMedicalConfirmationsUseCase
import com.tamin.taminhamrah.useCases.treatment.SendToInboxMedicalConfirmationUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class MedicalConfirmationsViewModel(
    private val getMedicalConfirmationsUseCase: GetMedicalConfirmationsUseCase,
    private val getMedicalConfirmationPDFUseCase: GetMedicalConfirmationPDFUseCase,
    private val sendToInboxMedicalConfirmationUseCase: SendToInboxMedicalConfirmationUseCase,
) : BaseViewModel<ConfirmationsUiState, PartialState, ConfirmationsEvent, ConfirmationsIntent>(
    initialState = ConfirmationsUiState()
) {

    override fun handleIntent(intent: ConfirmationsIntent): Flow<PartialState> {
        return when (intent) {
            is ConfirmationsIntent.LoadList -> loadList()
            is ConfirmationsIntent.DownloadPdf -> downloadPdf(intent)
            is ConfirmationsIntent.SendToInbox -> sendToInbox(intent)
            is ConfirmationsIntent.DismissPdfViewer -> flow { emit(PartialState.ViewerPdfChanged(null)) }
        }
    }

    private fun loadList(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        getMedicalConfirmationsUseCase().collect { list ->
            emit(PartialState.ConfirmationsLoaded(list.toPresentation().toImmutableList()))
        }
    }.catch { emit(PartialState.Error(it.toSingleLineMessage())) }

    private fun downloadPdf(intent: ConfirmationsIntent.DownloadPdf): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        emit(PartialState.ViewerPdfChanged(null))
        try {
            getMedicalConfirmationPDFUseCase(intent.repId).collect { pdfDn ->
                emit(PartialState.ViewerPdfChanged(pdfDn.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
            emit(PartialState.ViewerDownloadFailed)
        }
    }

    private fun sendToInbox(intent: ConfirmationsIntent.SendToInbox): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            sendToInboxMedicalConfirmationUseCase(intent.repId).collect {
                sendEvent(ConfirmationsEvent.SavedToInbox)
                emit(PartialState.Loading(false))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        }
    }

    override fun reduceState(
        currentState: ConfirmationsUiState,
        partialState: PartialState
    ): ConfirmationsUiState = when (partialState) {
        is PartialState.Reset -> ConfirmationsUiState()
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.ConfirmationsLoaded -> currentState.copy(isLoading = false, confirmationList = partialState.list)
        is PartialState.ViewerPdfChanged -> currentState.copy(
            isLoading = false,
            viewerPdf = partialState.pdf,
            viewerDownloadFailed = false,
        )
        is PartialState.ViewerDownloadFailed -> currentState.copy(
            isLoading = false,
            viewerDownloadFailed = true,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
