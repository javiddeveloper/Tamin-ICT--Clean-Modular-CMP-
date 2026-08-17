package com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.model.workshop.WorkShopDebtPR
import org.jetbrains.compose.resources.StringResource

/**
 * State of اعتراض به بدهی.
 *
 * Each row offers exactly one action, and which one is decided by the debt itself
 * ([WorkShopDebtPR.objectionKind]) rather than by the screen — filing against an estimate, filing
 * against a primary vote, or viewing an objection already filed.
 */
@Immutable
data class ObjectionableDebitUiState(
    val workshopId: String = "",
    val branchCode: String = "",
    val list: PagedListState<WorkShopDebtPR> = PagedListState(),
    /** The row whose deadline is being checked; its action shows progress meanwhile. */
    val checkingDebitNumber: String? = null,
    val isDownloading: Boolean = false,
    val viewerPdf: PdfDownloadPR? = null,
) {
    sealed interface PartialState {
        data class Opened(val workshopId: String, val branchCode: String) : PartialState
        data object Loading : PartialState
        data object LoadingMore : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val list: PagedListState<WorkShopDebtPR>) : PartialState
        data class Checking(val debitNumber: String?) : PartialState
        data class Downloading(val isDownloading: Boolean) : PartialState
        data class ViewerPdfChanged(val pdf: PdfDownloadPR?) : PartialState
    }
}

sealed interface ObjectionableDebitIntent {
    data class Open(val workshopId: String, val branchCode: String) : ObjectionableDebitIntent
    data object LoadMore : ObjectionableDebitIntent
    data object Retry : ObjectionableDebitIntent

    /** The row's single action; what it does follows from the debt's objection kind. */
    data class RowAction(val debt: WorkShopDebtPR) : ObjectionableDebitIntent
    data object DismissViewer : ObjectionableDebitIntent
}

sealed interface ObjectionableDebitEvent {
    /** In time to object — the form opens with this debt. */
    data class OpenObjectionForm(val debt: WorkShopDebtPR) : ObjectionableDebitEvent

    data class ShowMessage(val message: StringResource) : ObjectionableDebitEvent
}
