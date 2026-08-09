package com.tamin.taminhamrah.feature.profile.ui.electronicFile.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.model.DocumentTarget
import com.tamin.taminhamrah.model.erecords.ElectronicFilePR
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class ElectronicFileUiState(
    /** Every document the endpoint returned, in one request. */
    val documents: ImmutableList<ElectronicFilePR> = persistentListOf(),
    val isLoading: Boolean = true,
    /** Non-null when the list request failed; the viewer reports its own failure separately. */
    val errorMessage: String? = null,
    /** The document target being viewed, or null when the grid is on top. */
    val openTarget: DocumentTarget? = null,
    /** Fetched PDF download object for TaminPdfViewer, or null while loading. */
    val pdfPR: PdfDownloadPR? = null,
    val downloadFailed: Boolean = false,
    val nationalCode: String = "",
) {
    sealed interface PartialState {
        data object Loading : PartialState
        data class DocumentsLoaded(val documents: ImmutableList<ElectronicFilePR>) : PartialState
        data class TargetOpened(val target: DocumentTarget?) : PartialState
        data class PdfLoaded(val pdf: PdfDownloadPR) : PartialState
        data class NationalCodeLoaded(val nationalCode: String) : PartialState
        data object DownloadFailed : PartialState
        data class Error(val message: String) : PartialState
    }
}

sealed interface ElectronicFileIntent {
    data object LoadNationalCode : ElectronicFileIntent
    data object LoadDocuments : ElectronicFileIntent
    data class OpenDocument(val document: ElectronicFilePR) : ElectronicFileIntent
    data class DownloadPdf(val url: String) : ElectronicFileIntent
    data object DismissViewer : ElectronicFileIntent
    data object OnBackClicked : ElectronicFileIntent
}

sealed interface ElectronicFileEvent {
    data object NavigateBack : ElectronicFileEvent
}
