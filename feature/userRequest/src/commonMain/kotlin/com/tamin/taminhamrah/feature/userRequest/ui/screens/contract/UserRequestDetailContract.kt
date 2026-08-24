package com.tamin.taminhamrah.feature.userRequest.ui.screens.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.userRequest.UserRequestPR

@Immutable
data class UserRequestDetailState(
    val isLoading: Boolean = false,
    val request: UserRequestPR? = null,
    val error: String? = null,
    /** GUID of the document currently being downloaded, or null when none is in flight. */
    val downloadingDocumentGuid: String? = null,
    /** Non-null while a downloaded document is being previewed. */
    val documentPreview: DocumentPreview? = null,
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Loaded(val request: UserRequestPR) : PartialState()
        data class Error(val message: String?) : PartialState()
        data class DocumentDownloading(val guid: String?) : PartialState()
        data class DocumentPreviewReady(val preview: DocumentPreview) : PartialState()
        data object DocumentPreviewDismissed : PartialState()
    }
}

/** A downloaded document ready to preview. [imageData] is the raw base64 payload from the server. */
@Immutable
data class DocumentPreview(
    val title: String,
    val imageData: String,
)

sealed interface UserRequestDetailIntent {
    data class LoadDetail(
        val requestId: Long,
        val refCode: String,
        val requestTypeId: Long,
        val title: String,
        val referenceId: String = "",
    ) : UserRequestDetailIntent

    data class DownloadDocument(
        val guid: String,
        val title: String,
    ) : UserRequestDetailIntent

    data object DismissDocumentPreview : UserRequestDetailIntent

    data object NavigateBack : UserRequestDetailIntent
}

sealed interface UserRequestDetailEvent {
    data object NavigateBack : UserRequestDetailEvent
    data class ShowToast(val message: String) : UserRequestDetailEvent
}
