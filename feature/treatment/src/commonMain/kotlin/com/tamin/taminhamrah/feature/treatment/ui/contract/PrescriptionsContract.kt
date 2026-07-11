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
    val selectedNoteHeadId: String? = null
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
        data object PrescriptionCleared : PartialState()
    }
}

sealed class PrescriptionsIntent {
    data class LoadList(
        val nationalCode: String,
        val startDate: String? = null,
        val endDate: String? = null
    ) : PrescriptionsIntent()
    data class SelectPrescription(val noteHeadID: String, val nationalCode: String) : PrescriptionsIntent()
    data object ClearSelectedPrescription : PrescriptionsIntent()
    data class DownloadPdf(val prescriptionID: String) : PrescriptionsIntent()
    data class DownloadTestResult(val patientID: String?, val noteHeadEprescID: String?) : PrescriptionsIntent()
    data class TogglePdfDialog(val show: Boolean) : PrescriptionsIntent()
}

sealed class PrescriptionsEvent {
    data class ShowToast(val message: String) : PrescriptionsEvent()
}
