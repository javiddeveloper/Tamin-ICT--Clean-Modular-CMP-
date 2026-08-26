package com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.pension.PaymentTypeDN
import com.tamin.taminhamrah.model.pension.PensionIdPR
import com.tamin.taminhamrah.model.pension.PayRollPR
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR

@Immutable
data class PayRollUiState(
    val isLoading: Boolean = false,
    val hasLoadedOnce: Boolean = false,
    val isSendingToInbox: Boolean = false,
    val error: String? = null,
    val pensionerIds: List<PensionIdPR> = emptyList(),
    val selectedPensionerId: String? = null,
    val startDate: String = "",
    val paymentType: String = PaymentTypeDN.MONTHLY.code,
    val payRollList: List<PayRollPR> = emptyList(),
    val showPensionerSheet: Boolean = false,
    val showSearchSheet: Boolean = false,
    val searchYear: String = "",
    val searchMonth: String = "",
    val searchPaymentType: String = PaymentTypeDN.MONTHLY.code,
    val isDateFilteredBySearch: Boolean = false,
    val showSendSuccess: Boolean = false,
    val showNoPensionerDialog: Boolean = false,
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
        data class SendingToInbox(val isSending: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class PensionerIdsLoaded(val list: List<PensionIdPR>) : PartialState
        data class SelectedPensionerIdChanged(val id: String) : PartialState
        data class StartDateChanged(val date: String) : PartialState
        data class PaymentTypeChanged(val type: String) : PartialState
        data class PayRollLoaded(val payRoll: List<PayRollPR>) : PartialState
        data class ShowPensionerSheet(val show: Boolean) : PartialState
        data class ShowSearchSheet(val show: Boolean) : PartialState
        data class SearchYearChanged(val year: String) : PartialState
        data class SearchMonthChanged(val month: String) : PartialState
        data class SearchPaymentTypeChanged(val type: String) : PartialState
        data class DateFilteredBySearch(val filtered: Boolean) : PartialState
        data class ShowSendSuccess(val show: Boolean) : PartialState
        data class ShowNoPensionerDialog(val show: Boolean) : PartialState
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
    data object RequestSendToInbox : PayRollIntent
    data object DismissSendSuccess : PayRollIntent
    data object LoadPayRollPDF : PayRollIntent

    /** The viewer was closed; drops the fetched PDF so a re-open never shows a drained stream. */
    data object DismissPdfViewer : PayRollIntent

    data object ShowPensionerSheet : PayRollIntent
    data object DismissPensionerSheet : PayRollIntent
    data object ShowSearchSheet : PayRollIntent
    data object DismissSearchSheet : PayRollIntent
    data class ChangeSearchYear(val year: String) : PayRollIntent
    data class ChangeSearchMonth(val month: String) : PayRollIntent
    data class ChangeSearchPaymentType(val type: String) : PayRollIntent
    data object ApplySearch : PayRollIntent
    data object ClearDateFilter : PayRollIntent
    data object DismissNoPensionerDialog : PayRollIntent
}

sealed interface PayRollEvent {
    data class ShowToast(val message: String) : PayRollEvent
    data object NavigateBack : PayRollEvent
}
