package com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.pension.PensionIdPR
import com.tamin.taminhamrah.model.pension.PayRollPR
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR

@Immutable
data class PayRollUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val pensionerIds: List<PensionIdPR> = emptyList(),
    val selectedPensionerId: String? = null,
    val startDate: String = "",
    val paymentType: String = "",
    val payRollList: List<PayRollPR> = emptyList(),
    /**
     * The fetched payroll PDF waiting to be shown, or null when none has been asked for.
     *
     * Cleared as each download starts: the payload is a single-use stream, so a stale one would
     * render as an empty file. Whether the viewer is open is the screen's business — it may not
     * need a download at all if the device already has the file.
     */
    val payRollPDF: PdfDownloadPR? = null,
    /** The last requested payroll could not be fetched, so the viewer can stop waiting for it. */
    val viewerDownloadFailed: Boolean = false,
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class PensionerIdsLoaded(val list: List<PensionIdPR>) : PartialState
        data class SelectedPensionerIdChanged(val id: String) : PartialState
        data class StartDateChanged(val date: String) : PartialState
        data class PaymentTypeChanged(val type: String) : PartialState
        data class PayRollLoaded(val payRoll: List<PayRollPR>) : PartialState
        data class ViewerPdfChanged(val pdf: PdfDownloadPR?) : PartialState
        data object ViewerDownloadFailed : PartialState
    }
}

sealed interface PayRollIntent {
    data object LoadPensionerIds : PayRollIntent
    data class ChangeSelectedPensionerId(val id: String) : PayRollIntent
    data class ChangeStartDate(val date: String) : PayRollIntent
    data class ChangePaymentType(val type: String) : PayRollIntent
    data object LoadPayRoll : PayRollIntent
    data object LoadPayRollPDF : PayRollIntent

    /** The viewer was closed; drops the fetched PDF so a re-open never shows a drained stream. */
    data object DismissPdfViewer : PayRollIntent
}

sealed interface PayRollEvent {
    data class ShowToast(val message: String) : PayRollEvent
}
