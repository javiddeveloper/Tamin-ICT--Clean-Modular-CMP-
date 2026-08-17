package com.tamin.taminhamrah.feature.treatment.ui.contract

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import com.tamin.taminhamrah.model.treatment.MedicalConfirmationPR
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import org.jetbrains.compose.resources.StringResource

@Immutable
data class ConfirmationsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val confirmationList: ImmutableList<MedicalConfirmationPR> = persistentListOf(),
    val viewerPdf: PdfDownloadPR? = null,
    val viewerDownloadFailed: Boolean = false,
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data object Reset : PartialState()
        data class ConfirmationsLoaded(val list: ImmutableList<MedicalConfirmationPR>) : PartialState()
        data class ViewerPdfChanged(val pdf: PdfDownloadPR?) : PartialState()
        data object ViewerDownloadFailed : PartialState()
    }
}


sealed class ConfirmationsIntent {
    data object LoadList : ConfirmationsIntent()
    data class DownloadPdf(val repId: String) : ConfirmationsIntent()
    data class SendToInbox(val repId: String) : ConfirmationsIntent()
    data object DismissPdfViewer : ConfirmationsIntent()
}

sealed class ConfirmationsEvent {
    data class ShowToast(val message: StringResource) : ConfirmationsEvent()

    /** The certificate reached the personal inbox; the design acknowledges this with a modal. */
    data object SavedToInbox : ConfirmationsEvent()
}
