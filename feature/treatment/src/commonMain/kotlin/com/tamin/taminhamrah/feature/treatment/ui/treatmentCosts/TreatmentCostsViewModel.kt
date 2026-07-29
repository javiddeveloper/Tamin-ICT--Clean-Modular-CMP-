package com.tamin.taminhamrah.feature.treatment.ui.treatmentCosts

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.treatment.ui.contract.CostsEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.CostsIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.CostsUiState
import com.tamin.taminhamrah.feature.treatment.ui.contract.CostsUiState.PartialState
import com.tamin.taminhamrah.mapper.treatment.toPresentation
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.useCases.treatment.GetTreatmentCostsPDFUseCase
import com.tamin.taminhamrah.useCases.treatment.GetTreatmentCostsUseCase
import com.tamin.taminhamrah.useCases.treatment.SendToInboxTreatmentCostsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import taminx.core.core_ui.Res
import taminx.core.core_ui.costs_sent_to_inbox

class TreatmentCostsViewModel(
    private val getTreatmentCostsUseCase: GetTreatmentCostsUseCase,
    private val getTreatmentCostsPDFUseCase: GetTreatmentCostsPDFUseCase,
    private val sendToInboxTreatmentCostsUseCase: SendToInboxTreatmentCostsUseCase
) : BaseViewModel<CostsUiState, PartialState, CostsEvent, CostsIntent>(
    initialState = CostsUiState()
) {

    override fun handleIntent(intent: CostsIntent): Flow<PartialState> {
        return when (intent) {
            is CostsIntent.LoadList -> loadList()
            is CostsIntent.DownloadPdf -> downloadPdf(intent)
            is CostsIntent.SendToInbox -> sendToInbox(intent)
            is CostsIntent.DismissPdfViewer -> flow { emit(PartialState.ViewerPdfChanged(null)) }
        }
    }

    private fun loadList(): Flow<PartialState> = flow {
        emit(PartialState.Reset)
        emit(PartialState.Loading(true))
        try {
            getTreatmentCostsUseCase().collect { list ->
                emit(PartialState.TreatmentCostsLoaded(list.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        }
    }

    /**
     * Fetches the certificate PDF. Only reached when the viewer finds no copy already on the
     * device, so one the person has downloaded before costs no request at all.
     */
    private fun downloadPdf(intent: CostsIntent.DownloadPdf): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        emit(PartialState.ViewerPdfChanged(null))
        try {
            getTreatmentCostsPDFUseCase(intent.repId).collect { pdfDn ->
                emit(PartialState.ViewerPdfChanged(pdfDn.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
            emit(PartialState.ViewerDownloadFailed)
        }
    }

    /**
     * Posts the certificate to the person's inbox.
     *
     * The outcome is announced as an event, not parked in the state: it happens once and is read
     * once, and the service's own payload is not something to show — the previous app replaced it
     * with a fixed confirmation too.
     */
    private fun sendToInbox(intent: CostsIntent.SendToInbox): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            sendToInboxTreatmentCostsUseCase(intent.repId).collect {
                sendEvent(CostsEvent.ShowToast(Res.string.costs_sent_to_inbox))
                emit(PartialState.Loading(false))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        }
    }

    override fun reduceState(
        currentState: CostsUiState,
        partialState: PartialState
    ): CostsUiState = when (partialState) {
        is PartialState.Reset -> CostsUiState()
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.TreatmentCostsLoaded -> currentState.copy(isLoading = false, treatmentCostList = partialState.list)
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
