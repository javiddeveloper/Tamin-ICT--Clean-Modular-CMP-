package com.tamin.taminhamrah.feature.myinbox.ui.contract

import com.tamin.taminhamrah.model.inbox.PermitDurationPR
import com.tamin.taminhamrah.model.inbox.PersonalInboxItemPR
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizePR
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class MyInboxUiState(
    val isLoading: Boolean = false,
    val items: List<PersonalInboxItemPR> = emptyList(),
    val size: PersonalInboxSizePR? = null,
    val error: String? = null,
    val showInquiryPermitSheet: Boolean = false,
    val selectedItemIdForPermit: Long? = null,
    val permitDurations: ImmutableList<PermitDurationPR> = persistentListOf(),
    val showPdfViewer: Boolean = false,
    val selectedPdfId: Long? = null,
    val pdfDownload: PdfDownloadPR? = null,
    val pdfDownloadFailed: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val selectedItemIdForDelete: Long? = null,
    val showCancelLicenseConfirmation: Boolean = false,
    val selectedItemIdForCancelLicense: Long? = null,
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState
        data class ItemsLoaded(val items: List<PersonalInboxItemPR>) : PartialState
        data class SizeLoaded(val size: PersonalInboxSizePR) : PartialState
        data class ShowInquiryPermitSheet(val itemId: Long) : PartialState
        data object HideInquiryPermitSheet : PartialState
        data class DurationsLoaded(val durations: ImmutableList<PermitDurationPR>) : PartialState
        data class ShowPdfViewer(val id: Long) : PartialState
        data object HidePdfViewer : PartialState
        data class PdfLoaded(val pdf: PdfDownloadPR?) : PartialState
        data class PdfDownloadError(val failed: Boolean) : PartialState
        data class ShowDeleteConfirmation(val id: Long) : PartialState
        data object HideDeleteConfirmation : PartialState
        data class ShowCancelLicenseConfirmation(val id: Long) : PartialState
        data object HideCancelLicenseConfirmation : PartialState
    }
}

sealed interface MyInboxIntent {
    data object LoadInbox : MyInboxIntent
    data object LoadDurations : MyInboxIntent
    data object OnBackClicked : MyInboxIntent
    data class OnCopyClicked(val id: Long) : MyInboxIntent
    data class OnItemActionClicked(val id: Long, val actionValue: String) : MyInboxIntent
    data class ShowInquiryPermit(val id: Long) : MyInboxIntent
    data object DismissInquiryPermit : MyInboxIntent
    data class ConfirmInquiryPermit(val id: Long, val duration: PermitDurationPR) : MyInboxIntent
    data class CancelInquiryPermit(val id: Long) : MyInboxIntent
    data class RequestPdfDownload(val id: Long) : MyInboxIntent
    data object DismissPdfViewer : MyInboxIntent
    data class ShowDeleteConfirmation(val id: Long) : MyInboxIntent
    data object DismissDeleteConfirmation : MyInboxIntent
    data class ConfirmDeleteRequest(val id: Long) : MyInboxIntent
    data class ShowCancelLicenseConfirmation(val id: Long) : MyInboxIntent
    data object DismissCancelLicenseConfirmation : MyInboxIntent
    data class ConfirmCancelLicense(val id: Long) : MyInboxIntent
}

sealed interface MyInboxEvent {
    data object NavigateBack : MyInboxEvent
    data class CopyToClipboard(val id: Long) : MyInboxEvent
    data class ShowError(val message: String) : MyInboxEvent
}
