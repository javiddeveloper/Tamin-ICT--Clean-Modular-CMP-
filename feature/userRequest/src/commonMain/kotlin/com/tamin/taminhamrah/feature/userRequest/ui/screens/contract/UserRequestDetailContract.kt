package com.tamin.taminhamrah.feature.userRequest.ui.screens.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.ui.model.userRequest.UserRequestDetailPR

@Immutable
data class UserRequestDetailState(
    val isLoading: Boolean = false,
    val request: UserRequestDetailPR? = null,
    val error: String? = null,
    val requestReferenceId: String? = null,
    val requestType: Int? = null,
    val objectionNumber: Long? = null,
    val deferredInstallmentId: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Loaded(val request: UserRequestDetailPR) : PartialState()
        data class Error(val message: String?) : PartialState()
        data class SetReferenceId(val referenceId: String) : PartialState()
        data class SetRequestType(val requestType: Int) : PartialState()
        data class SetObjectionNumber(val objectionNumber: Long) : PartialState()
        data class SetDeferredInstallmentId(val deferredInstallmentId: String) : PartialState()
        data class UpdateDocuments(val documents: List<DocumentPR>) : PartialState()
    }
}

sealed interface UserRequestDetailIntent {
    data class LoadDetail(val requestId: Long, val requestType: Int? = null) : UserRequestDetailIntent
    data object NavigateBack : UserRequestDetailIntent
    data class LoadPregnancyDetails(val referenceId: String, val requestType: Int) : UserRequestDetailIntent
    data class LoadArticle16Details(val objectionNumber: Long) : UserRequestDetailIntent
    data class LoadDeferredInstallmentDetails(val installId: String) : UserRequestDetailIntent
    data class LoadFollowUpObjectionHistory(val referenceId: String) : UserRequestDetailIntent
    data class DownloadImage(val guid: String) : UserRequestDetailIntent
}

sealed interface UserRequestDetailEvent {
    data object NavigateBack : UserRequestDetailEvent
    data class ShowToast(val message: String) : UserRequestDetailEvent
    data class ShowDocumentOptions(val documents: List<DocumentPR>) : UserRequestDetailEvent
}

data class DocumentPR(
    val fileName: String?,
    val guid: String?,
    val fileType: String?
)
