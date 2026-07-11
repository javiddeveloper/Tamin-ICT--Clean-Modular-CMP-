package com.tamin.taminhamrah.feature.treatment.ui.treatmentCosts

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.treatment.ui.contract.CostsEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.CostsIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.CostsUiState
import com.tamin.taminhamrah.feature.treatment.ui.contract.CostsUiState.PartialState
import com.tamin.taminhamrah.mapper.treatment.toPresentation
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.useCases.treatment.GetTreatmentCostsPDFUseCase
import com.tamin.taminhamrah.useCases.treatment.GetTreatmentCostsUseCase
import com.tamin.taminhamrah.useCases.treatment.SendToInboxTreatmentCostsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
            is CostsIntent.TogglePdfDialog -> flow { emit(PartialState.TogglePdfDialog(intent.show)) }
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
            emit(PartialState.Error(e.message))
        }
    }

    private fun downloadPdf(intent: CostsIntent.DownloadPdf): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getTreatmentCostsPDFUseCase(intent.repId).collect { pdfDn ->
                emit(PartialState.PdfLoaded(pdfDn.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun sendToInbox(intent: CostsIntent.SendToInbox): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            sendToInboxTreatmentCostsUseCase(intent.repId).collect { response ->
                emit(PartialState.SendToInboxDone(response))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
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
        is PartialState.PdfLoaded -> currentState.copy(isLoading = false, viewerPdf = partialState.pdf, showPdfDialog = true)
        is PartialState.TogglePdfDialog -> currentState.copy(showPdfDialog = partialState.show)
        is PartialState.SendToInboxDone -> currentState.copy(isLoading = false, sendToInboxResult = partialState.response)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
