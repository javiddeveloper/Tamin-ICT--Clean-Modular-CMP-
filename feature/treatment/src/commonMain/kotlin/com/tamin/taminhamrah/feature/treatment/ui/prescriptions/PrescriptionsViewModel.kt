package com.tamin.taminhamrah.feature.treatment.ui.prescriptions

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsUiState
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsUiState.PartialState
import com.tamin.taminhamrah.mapper.treatment.toPresentation
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.treatment.DownloadLabResultPdfUseCase
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionDetailUseCase
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionListUseCase
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionPriceUseCase
import com.tamin.taminhamrah.useCases.treatment.GetPrescriptionPdfFileUseCase
import com.tamin.taminhamrah.util.getCurrentTimestamp
import com.tamin.taminhamrah.util.getSixMonthsAgoTimestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class PrescriptionsViewModel(
    private val identityInfoUseCase: IdentityInfoUseCase,
    private val getElectronicPrescriptionListUseCase: GetElectronicPrescriptionListUseCase,
    private val getElectronicPrescriptionDetailUseCase: GetElectronicPrescriptionDetailUseCase,
    private val getElectronicPrescriptionPriceUseCase: GetElectronicPrescriptionPriceUseCase,
    private val getPrescriptionPdfFileUseCase: GetPrescriptionPdfFileUseCase,
    private val downloadLabResultPdfUseCase: DownloadLabResultPdfUseCase
) : BaseViewModel<PrescriptionsUiState, PartialState, PrescriptionsEvent, PrescriptionsIntent>(
    initialState = PrescriptionsUiState()
) {

    override fun handleIntent(intent: PrescriptionsIntent): Flow<PartialState> {
        return when (intent) {
            is PrescriptionsIntent.LoadList -> loadList(intent)
            is PrescriptionsIntent.SelectPrescription -> selectPrescription(intent)
            is PrescriptionsIntent.ClearSelectedPrescription -> flow { emit(PartialState.PrescriptionCleared) }
            is PrescriptionsIntent.DownloadPdf -> downloadPdf(intent)
            is PrescriptionsIntent.DownloadLabResult -> downloadLabResult(intent)
            is PrescriptionsIntent.TogglePdfDialog -> flow { emit(PartialState.TogglePdfDialog(intent.show)) }
            is PrescriptionsIntent.LoadRecordPrices -> loadRecordPrices(intent)
        }
    }

    /**
     * The signed-in person's national code, from the identity use case rather than the token
     * store — the same source the treatment hub uses, so both screens agree on who is signed in.
     *
     * Falls back to "0", never "": these values are URL path segments, so an empty one collapses
     * `patient-history/{nationalCode}/...` into a double slash and the endpoint 404s. The previous
     * app used the same placeholder.
     */
    private suspend fun getLoggedNationalCode(): String = try {
        identityInfoUseCase().first().nationalId?.takeIf { it.isNotBlank() } ?: NO_NATIONAL_CODE
    } catch (e: Exception) {
        NO_NATIONAL_CODE
    }

    private fun loadList(intent: PrescriptionsIntent.LoadList): Flow<PartialState> = flow {
        emit(PartialState.Reset)
        emit(PartialState.Loading(true))
        val nationalCode = getLoggedNationalCode()
        // Defaults to the «۶ ماه اخیر» period the records filter advertises.
        val startD = intent.startDate ?: getSixMonthsAgoTimestamp()
        val endD = intent.endDate ?: getCurrentTimestamp()

        try {
            // One request per category. «همه» asks for each type and the lists are merged here,
            // newest first; a single-type tab is just a list of one. The repository encodes how
            // "self" versus a dependant is addressed, so the raw patient code is handed straight in.
            val perType = intent.requestTypeIds.map { requestTypeId ->
                getElectronicPrescriptionListUseCase(
                    requestTypeId,
                    nationalCode,
                    intent.nationalCode,
                    startD,
                    endD,
                )
            }
            combine(perType) { lists ->
                lists.toList()
                    .flatten()
                    .sortedByDescending { it.prescDate }
            }.collect { merged ->
                emit(PartialState.PrescriptionsLoaded(merged.toPresentation()))
            }
                } catch (e: Exception) {
            emit(PartialState.Error(e.messageOr(ERROR_LOAD_LIST)))
        }
    }

    private fun selectPrescription(intent: PrescriptionsIntent.SelectPrescription): Flow<PartialState> = flow {
        emit(PartialState.PrescriptionSelected(intent.noteHeadID))
        emit(PartialState.Loading(true))
        val nationalCode = getLoggedNationalCode()

        try {
            // Raw patient code and flagSata; the repository encodes "self" and an absent flagSata
            // the way the endpoint expects.
            getElectronicPrescriptionDetailUseCase(intent.noteHeadID, nationalCode, intent.nationalCode, intent.flagSata, intent.type).collect { list ->
                emit(PartialState.PrescriptionDetailsLoaded(list.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.messageOr(ERROR_LOAD_DETAIL)))
        }

        try {
            getElectronicPrescriptionPriceUseCase(intent.noteHeadID, nationalCode).collect { list ->
                emit(PartialState.PrescriptionPricesLoaded(list.toPresentation()))
            }
        } catch (e: Exception) {
            // Price is supplementary to the prescription details; ignore its failure.
        }
    }

    /**
     * Fetches the insured share for each record.
     *
     * One request per record, because the price endpoint is keyed by a single `noteHeadID` and the
     * list carries no amount. Only run when the cost filter is actually in use, and records whose
     * price fails are simply left out rather than failing the whole search.
     */
    private fun loadRecordPrices(intent: PrescriptionsIntent.LoadRecordPrices): Flow<PartialState> =
        flow {
            emit(PartialState.LoadingPrices(true))
            val nationalCode = getLoggedNationalCode()
            val prices = mutableMapOf<String, Long>()
            intent.noteHeadIds.forEach { noteHeadId ->
                try {
                    getElectronicPrescriptionPriceUseCase(noteHeadId, nationalCode)
                        .first()
                        .firstOrNull()
                        ?.let { price ->
                            price.headInsuPayment?.let { prices[noteHeadId] = it }
                        }
                } catch (e: Exception) {
                    // A record without a price stays unfiltered rather than disappearing.
                }
            }
            emit(PartialState.RecordPricesLoaded(prices))
            emit(PartialState.LoadingPrices(false))
        }

    private fun downloadPdf(intent: PrescriptionsIntent.DownloadPdf): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getPrescriptionPdfFileUseCase(intent.prescriptionID).collect { pdfDn ->
                emit(PartialState.PdfLoaded(pdfDn.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.messageOr(ERROR_RECEIVE_FILE)))
        }
    }

    private fun downloadLabResult(intent: PrescriptionsIntent.DownloadLabResult): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        val patientID = intent.patientID ?: ""
        val noteHeadEprescID = intent.noteHeadEprescID ?: ""
        val currentUserNationalCode = getLoggedNationalCode()

        try {
            downloadLabResultPdfUseCase(patientID, noteHeadEprescID, currentUserNationalCode).collect { pdfDn ->
                emit(PartialState.PdfLoaded(pdfDn.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.messageOr(ERROR_RECEIVE_FILE)))
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
        is PartialState.RecordPricesLoaded -> currentState.copy(
            recordPrices = currentState.recordPrices + partialState.prices,
        )
        is PartialState.LoadingPrices -> currentState.copy(isLoadingPrices = partialState.isLoading)
        is PartialState.PrescriptionCleared -> currentState.copy(
            selectedNoteHeadId = null,
            prescriptionDetailList = emptyList(),
            prescriptionPriceList = emptyList()
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

/**
 * The message shown when a request fails.
 *
 * Falls back to [fallback] when the exception carries nothing readable: a null here would leave
 * `error` null and the screen would render "no records" for what was actually a failure. The
 * wording follows the previous app, which named the failed step rather than showing raw errors.
 */
private fun Throwable.messageOr(fallback: String): String =
    message?.takeIf { it.isNotBlank() } ?: fallback

private const val ERROR_LOAD_LIST = "خطا در دریافت سوابق درمانی"
private const val ERROR_LOAD_DETAIL = "خطا در دریافت جزئیات نسخه"
private const val ERROR_RECEIVE_FILE = "خطا در دریافت فایل"

/** Placeholder for an unknown national code, matching the previous app's path segment. */
private const val NO_NATIONAL_CODE = "0"
