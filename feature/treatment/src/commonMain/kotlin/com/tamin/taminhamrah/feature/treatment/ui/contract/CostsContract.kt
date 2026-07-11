package com.tamin.taminhamrah.feature.treatment.ui.contract

import com.tamin.taminhamrah.model.treatment.TreatmentCostPR
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR

data class CostsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val treatmentCostList: List<TreatmentCostPR> = emptyList(),
    val viewerPdf: PdfDownloadPR? = null,
    val showPdfDialog: Boolean = false,
    val sendToInboxResult: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data object Reset : PartialState()
        data class TreatmentCostsLoaded(val list: List<TreatmentCostPR>) : PartialState()
        data class PdfLoaded(val pdf: PdfDownloadPR) : PartialState()
        data class TogglePdfDialog(val show: Boolean) : PartialState()
        data class SendToInboxDone(val response: String) : PartialState()
    }
}

sealed class CostsIntent {
    data object LoadList : CostsIntent()
    data class DownloadPdf(val repId: String) : CostsIntent()
    data class SendToInbox(val repId: String) : CostsIntent()
    data class TogglePdfDialog(val show: Boolean) : CostsIntent()
}

sealed class CostsEvent {
    data class ShowToast(val message: String) : CostsEvent()
}
