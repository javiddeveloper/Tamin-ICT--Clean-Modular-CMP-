package com.tamin.taminhamrah.feature.treatment.ui.contract

import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR

data class PrescriptionsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val prescriptionList: List<ElectronicPrescriptionPR> = emptyList(),
    val prescriptionDetailList: List<ElectronicPrescriptionDetailPR> = emptyList(),
    val prescriptionPriceList: List<ElectronicPrescriptionPricePR> = emptyList(),
    val viewerPdf: PdfDownloadPR? = null,
    val showPdfDialog: Boolean = false,
    val selectedNoteHeadId: String? = null,

    /**
     * Insured share per record, keyed by `noteHeadEprescID`.
     *
     * The list endpoint carries no amount, so these are fetched one record at a time and only
     * when the advanced search filters on cost — see [PrescriptionsIntent.LoadRecordPrices].
     */
    val recordPrices: Map<String, Long> = emptyMap(),
    val isLoadingPrices: Boolean = false
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data object Reset : PartialState()
        data class PrescriptionsLoaded(val list: List<ElectronicPrescriptionPR>) : PartialState()
        data class PrescriptionDetailsLoaded(val list: List<ElectronicPrescriptionDetailPR>) : PartialState()
        data class PrescriptionPricesLoaded(val list: List<ElectronicPrescriptionPricePR>) : PartialState()
        data class PdfLoaded(val pdf: PdfDownloadPR) : PartialState()
        data class TogglePdfDialog(val show: Boolean) : PartialState()
        data class PrescriptionSelected(val noteHeadID: String) : PartialState()
        data class RecordPricesLoaded(val prices: Map<String, Long>) : PartialState()
        data class LoadingPrices(val isLoading: Boolean) : PartialState()
        data object PrescriptionCleared : PartialState()
    }
}

sealed class PrescriptionsIntent {
    data class LoadList(
        val nationalCode: String,
        /**
         * Categories to query. More than one means the «همه» tab, which the endpoint cannot
         * express, so the results are fetched per type and merged.
         */
        val requestTypeIds: List<String>,
        val startDate: String? = null,
        val endDate: String? = null
    ) : PrescriptionsIntent()

    /**
     * [noteHeadID] is the record's `noteHeadEprescID`, not its tracking code, and [type] and
     * [flagSata] come from the record itself — the detail endpoint keys on all three.
     */
    data class SelectPrescription(
        val noteHeadID: String,
        val nationalCode: String,
        val type: String,
        val flagSata: String
    ) : PrescriptionsIntent()
    data object ClearSelectedPrescription : PrescriptionsIntent()
    data class DownloadPdf(val prescriptionID: String) : PrescriptionsIntent()
    data class DownloadLabResult(val patientID: String?, val noteHeadEprescID: String?) : PrescriptionsIntent()
    data class TogglePdfDialog(val show: Boolean) : PrescriptionsIntent()

    /** Fetches the amount for each record, so the advanced search can filter on cost. */
    data class LoadRecordPrices(val noteHeadIds: List<String>, val nationalCode: String) :
        PrescriptionsIntent()
}

sealed class PrescriptionsEvent {
    data class ShowToast(val message: String) : PrescriptionsEvent()
}
