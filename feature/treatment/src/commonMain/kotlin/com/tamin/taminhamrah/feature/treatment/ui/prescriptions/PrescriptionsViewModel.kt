package com.tamin.taminhamrah.feature.treatment.ui.prescriptions

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsUiState
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsUiState.PartialState
import com.tamin.taminhamrah.mapper.treatment.toPresentation
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.useCases.treatment.DownloadTestResultPdfUseCase
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionDetailUseCase
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionListUseCase
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionPriceUseCase
import com.tamin.taminhamrah.useCases.treatment.GetPrescriptionPdfFileUseCase
import com.tamin.taminhamrah.util.getCurrentTimestamp
import com.tamin.taminhamrah.util.getSixMonthsAgoTimestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PrescriptionsViewModel(
    private val tokenStoreManager: TokenStoreManager,
    private val getElectronicPrescriptionListUseCase: GetElectronicPrescriptionListUseCase,
    private val getElectronicPrescriptionDetailUseCase: GetElectronicPrescriptionDetailUseCase,
    private val getElectronicPrescriptionPriceUseCase: GetElectronicPrescriptionPriceUseCase,
    private val getPrescriptionPdfFileUseCase: GetPrescriptionPdfFileUseCase,
    private val downloadTestResultPdfUseCase: DownloadTestResultPdfUseCase
) : BaseViewModel<PrescriptionsUiState, PartialState, PrescriptionsEvent, PrescriptionsIntent>(
    initialState = PrescriptionsUiState()
) {

    override fun handleIntent(intent: PrescriptionsIntent): Flow<PartialState> {
        return when (intent) {
            is PrescriptionsIntent.LoadList -> loadList(intent)
            is PrescriptionsIntent.SelectPrescription -> selectPrescription(intent)
            is PrescriptionsIntent.ClearSelectedPrescription -> flow { emit(PartialState.PrescriptionCleared) }
            is PrescriptionsIntent.DownloadPdf -> downloadPdf(intent)
            is PrescriptionsIntent.DownloadTestResult -> downloadTestResult(intent)
            is PrescriptionsIntent.TogglePdfDialog -> flow { emit(PartialState.TogglePdfDialog(intent.show)) }
        }
    }

    private fun getLoggedNationalCode(): String {
        return tokenStoreManager.getUserId() ?: ""
    }

    private fun loadList(intent: PrescriptionsIntent.LoadList): Flow<PartialState> = flow {
        emit(PartialState.Reset)
        emit(PartialState.Loading(true))
        val nationalCode = getLoggedNationalCode()
        val dependantCode = if (intent.nationalCode == nationalCode) "0" else intent.nationalCode
        // Defaults to the «۶ ماه اخیر» period the records filter advertises.
        val startD = intent.startDate ?: getSixMonthsAgoTimestamp()
        val endD = intent.endDate ?: getCurrentTimestamp()

        try {
            getElectronicPrescriptionListUseCase("1", nationalCode, dependantCode, startD, endD).collect { list ->
                emit(PartialState.PrescriptionsLoaded(list.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun selectPrescription(intent: PrescriptionsIntent.SelectPrescription): Flow<PartialState> = flow {
        emit(PartialState.PrescriptionSelected(intent.noteHeadID))
        emit(PartialState.Loading(true))
        val nationalCode = getLoggedNationalCode()
        val childCode = if (intent.nationalCode == nationalCode) "0" else intent.nationalCode

        try {
            getElectronicPrescriptionDetailUseCase(intent.noteHeadID, nationalCode, childCode, "null", "1").collect { list ->
                emit(PartialState.PrescriptionDetailsLoaded(list.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }

        try {
            getElectronicPrescriptionPriceUseCase(intent.noteHeadID, nationalCode).collect { list ->
                emit(PartialState.PrescriptionPricesLoaded(list.toPresentation()))
            }
        } catch (e: Exception) {
            // Price is supplementary to the prescription details; ignore its failure.
        }
    }

    private fun downloadPdf(intent: PrescriptionsIntent.DownloadPdf): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getPrescriptionPdfFileUseCase(intent.prescriptionID).collect { pdfDn ->
                emit(PartialState.PdfLoaded(pdfDn.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun downloadTestResult(intent: PrescriptionsIntent.DownloadTestResult): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        val patientID = intent.patientID ?: ""
        val noteHeadEprescID = intent.noteHeadEprescID ?: ""
        val currentUserNationalCode = getLoggedNationalCode()

        try {
            downloadTestResultPdfUseCase(patientID, noteHeadEprescID, currentUserNationalCode).collect { pdfDn ->
                emit(PartialState.PdfLoaded(pdfDn.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    override fun reduceState(
        currentState: PrescriptionsUiState,
        partialState: PartialState
    ): PrescriptionsUiState = when (partialState) {
        is PartialState.Reset -> PrescriptionsUiState()
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.PrescriptionsLoaded -> currentState.copy(isLoading = false, prescriptionList = partialState.list)
        is PartialState.PrescriptionDetailsLoaded -> currentState.copy(isLoading = false, prescriptionDetailList = partialState.list)
        is PartialState.PrescriptionPricesLoaded -> currentState.copy(isLoading = false, prescriptionPriceList = partialState.list)
        is PartialState.PdfLoaded -> currentState.copy(isLoading = false, viewerPdf = partialState.pdf, showPdfDialog = true)
        is PartialState.TogglePdfDialog -> currentState.copy(showPdfDialog = partialState.show)
        is PartialState.PrescriptionSelected -> currentState.copy(selectedNoteHeadId = partialState.noteHeadID)
        is PartialState.PrescriptionCleared -> currentState.copy(
            selectedNoteHeadId = null,
            prescriptionDetailList = emptyList(),
            prescriptionPriceList = emptyList()
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
