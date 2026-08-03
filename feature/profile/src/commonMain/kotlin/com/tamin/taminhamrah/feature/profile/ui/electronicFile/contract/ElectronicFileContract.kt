package com.tamin.taminhamrah.feature.profile.ui.electronicFile.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.model.DocumentTarget
import com.tamin.taminhamrah.model.erecords.ElectronicFilePR
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR

/**
 * Only the viewer's state lives here. The document list is a `PagingData` stream on the
 * ViewModel: it is neither immutable nor stable, and putting it in state would defeat the
 * skipping the grid depends on.
 */
@Immutable
data class ElectronicFileUiState(
    /** The document target being viewed, or null when the grid is on top. */
    val openTarget: DocumentTarget? = null,
    /** Fetched PDF download object for TaminPdfViewer, or null while loading. */
    val pdfPR: PdfDownloadPR? = null,
    val downloadFailed: Boolean = false,
    val nationalCode: String = "",
) {
    sealed interface PartialState {
        data class TargetOpened(val target: DocumentTarget?) : PartialState
        data class PdfLoaded(val pdf: PdfDownloadPR) : PartialState
        data class NationalCodeLoaded(val nationalCode: String) : PartialState
        data object DownloadFailed : PartialState
        data class Error(val message: String) : PartialState
    }
}

sealed interface ElectronicFileIntent {
    data object LoadNationalCode : ElectronicFileIntent
    data class OpenDocument(val document: ElectronicFilePR) : ElectronicFileIntent
    data class DownloadPdf(val url: String) : ElectronicFileIntent
    data object DismissViewer : ElectronicFileIntent
    data object OnBackClicked : ElectronicFileIntent
}

sealed interface ElectronicFileEvent {
    data object NavigateBack : ElectronicFileEvent
}
