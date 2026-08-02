package com.tamin.taminhamrah.feature.treatment.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

/** Immutable by construction: the reducer only ever `copy`s, and no collection here is mutated. */
@Immutable
data class PrescriptionsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val prescriptionList: List<ElectronicPrescriptionPR> = emptyList(),
    val prescriptionDetailList: List<ElectronicPrescriptionDetailPR> = emptyList(),
    val prescriptionPriceList: ImmutableList<ElectronicPrescriptionPricePR> = persistentListOf(),
    /**
     * The fetched PDF waiting to be shown, or null when none has been asked for.
     *
     * Cleared as each download starts: the payload is a single-use stream, so a stale one would
     * render as an empty file. Which viewer is open, and under what name it is saved, is the
     * screen's business — it may not need a download at all if the device already has the file.
     */
    val viewerPdf: PdfDownloadPR? = null,
    /** The last requested export could not be fetched, so the viewer can stop waiting for it. */
    val viewerDownloadFailed: Boolean = false,
    val selectedNoteHeadId: String? = null,

    /**
     * Full price breakdown per record, keyed by `noteHeadEprescID`.
     *
     * The list endpoint carries no amount, so these are fetched one record at a time — see
     * [PrescriptionsIntent.LoadRecordPrices]. They feed both the «سهم شما» shown on each list card
     * (`headSsoPayment`) and the advanced search's cost filter.
     */
    val recordPrices: ImmutableMap<String, ElectronicPrescriptionPricePR> = persistentMapOf(),
    val isLoadingPrices: Boolean = false
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data object Reset : PartialState()
        data class PrescriptionsLoaded(val list: List<ElectronicPrescriptionPR>) : PartialState()
        data class PrescriptionDetailsLoaded(val list: List<ElectronicPrescriptionDetailPR>) : PartialState()
        data class PrescriptionPricesLoaded(val list: List<ElectronicPrescriptionPricePR>) : PartialState()
        data class ViewerPdfChanged(val pdf: PdfDownloadPR?) : PartialState()
        data object ViewerDownloadFailed : PartialState()
        data class PrescriptionSelected(val noteHeadID: String) : PartialState()
        data class RecordPricesLoaded(val prices: Map<String, ElectronicPrescriptionPricePR>) : PartialState()
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

    /** The viewer was closed; drops the fetched PDF so a re-open never shows a drained stream. */
    data object DismissPdfViewer : PrescriptionsIntent()

    /** Fetches the amount for each record, so the advanced search can filter on cost. */
    data class LoadRecordPrices(val noteHeadIds: List<String>, val nationalCode: String) :
        PrescriptionsIntent()
}

sealed class PrescriptionsEvent {
    data class ShowToast(val message: String) : PrescriptionsEvent()
}
