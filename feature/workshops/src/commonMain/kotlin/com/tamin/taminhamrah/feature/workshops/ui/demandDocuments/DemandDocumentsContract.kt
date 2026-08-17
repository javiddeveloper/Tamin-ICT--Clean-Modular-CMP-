package com.tamin.taminhamrah.feature.workshops.ui.demandDocuments

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocPR

/** State of اسناد مطالبه — the documents behind one debt, each openable as a PDF. */
@Immutable
data class DemandDocumentsUiState(
    val debitNumber: String = "",
    val branchCode: String = "",
    val list: PagedListState<WorkshopDemandDocPR> = PagedListState(),
    val isDownloading: Boolean = false,
    /** Non-null while the viewer is open. */
    val viewerPdf: PdfDownloadPR? = null,
    val downloadFailed: Boolean = false,
) {
    sealed interface PartialState {
        data class Opened(val debitNumber: String, val branchCode: String) : PartialState
        data object Loading : PartialState
        data object LoadingMore : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val list: PagedListState<WorkshopDemandDocPR>) : PartialState
        data class Downloading(val isDownloading: Boolean) : PartialState
        data class ViewerPdfChanged(val pdf: PdfDownloadPR?) : PartialState
        data object DownloadFailed : PartialState
    }
}

sealed interface DemandDocumentsIntent {
    data class Open(val debitNumber: String, val branchCode: String) : DemandDocumentsIntent
    data object LoadMore : DemandDocumentsIntent
    data object Retry : DemandDocumentsIntent

    /** مشاهده جزئیات محاسبه — the turnover PDF of the debt this list belongs to. */
    data object ShowCalculationPdf : DemandDocumentsIntent
    data object DismissViewer : DemandDocumentsIntent
}

sealed interface DemandDocumentsEvent
