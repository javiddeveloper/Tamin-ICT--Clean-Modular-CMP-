package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFilePR
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * صدور و مدیریت برگه پرداخت — عملیات menu option "۲" (shown instead of «مدیریت پرداخت اقساط»
 * unless the row's debitStatusCode is "51"). Three actions the old app's
 * `IssuanceAndManagementPaymentSheetFragment` wires up, in order of appearance:
 *  1. on open — load the payment sheets already issued for [debitNumber] (`getData()`).
 *  2. «گواهی پرداخت حق بیمه» — download the certificate PDF.
 *  3. «صدور برگه پرداخت» — issue a new payment sheet, behind a confirm dialog in the old UI.
 */
@Immutable
data class PaymentSheetUiState(
    val debitNumber: String = "",
    val branchCode: String = "",
    val isLoading: Boolean = false,
    val items: ImmutableList<PaymentSheetConstructionFilePR> = persistentListOf(),
    val error: String? = null,

    // Same shape as ContractAffairsUiState's PDF fields — showPdfViewer is ViewModel-owned state,
    // not local Compose state, so DismissPdfViewer can clear pdfDownload/pdfDownloadFailed through
    // the normal MVI loop instead of leaving stale data behind for the next open.
    val showPdfViewer: Boolean = false,
    val isPdfLoading: Boolean = false,
    val pdfDownload: PdfDownloadPR? = null,
    val pdfDownloadFailed: Boolean = false,

    val isIssuing: Boolean = false,
    val issuanceMessage: String? = null,
    val issuanceFailed: Boolean = false,
) {
    sealed interface PartialState {
        data class HeaderSeeded(val debitNumber: String, val branchCode: String) : PartialState
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val items: ImmutableList<PaymentSheetConstructionFilePR>) : PartialState

        data class PdfViewerVisibility(val visible: Boolean) : PartialState
        data class PdfLoading(val loading: Boolean) : PartialState
        data class PdfLoaded(val pdf: PdfDownloadPR?) : PartialState
        data class PdfFailed(val failed: Boolean) : PartialState

        data class IssuanceLoading(val loading: Boolean) : PartialState
        data class IssuanceSucceeded(val message: String?) : PartialState
        data class IssuanceFailed(val failed: Boolean) : PartialState
    }
}

sealed interface PaymentSheetIntent {
    /** Sent once from the Route with the values carried by [PaymentSheetUiState]. */
    data class Load(val debitNumber: String, val branchCode: String) : PaymentSheetIntent
    data object Retry : PaymentSheetIntent
    data object DownloadCertificate : PaymentSheetIntent
    data object DismissPdfViewer : PaymentSheetIntent
    data object IssuePaymentSheet : PaymentSheetIntent
    data object OnBackClicked : PaymentSheetIntent
}

sealed interface PaymentSheetEvent {
    data object NavigateBack : PaymentSheetEvent
    data class ShowError(val message: String) : PaymentSheetEvent
}
