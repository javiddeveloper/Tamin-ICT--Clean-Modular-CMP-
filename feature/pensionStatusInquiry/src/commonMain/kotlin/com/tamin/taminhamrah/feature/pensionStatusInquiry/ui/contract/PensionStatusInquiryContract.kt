package com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.certificate.RecipientPR
import com.tamin.taminhamrah.model.pension.PensionInquiryPR

@Immutable
data class PensionStatusInquiryUiState(
    val isLoading: Boolean = false,
    val isSendingCertificate: Boolean = false,
    val error: String? = null,
    val pensionList: List<PensionInquiryPR> = emptyList(),
    val successMessage: String? = null,
    val showCertificateSheet: Boolean = false,
    val showRecipientsSheet: Boolean = false,
    val recipients: List<RecipientPR> = emptyList(),
    val isLoadingRecipients: Boolean = false,
    val recipientSearchQuery: String = "",
    val selectedRecipient: RecipientPR? = null,
    val branchName: String = "",
    val showRecipientError: Boolean = false,
) {
    val filteredRecipients: List<RecipientPR>
        get() = if (recipientSearchQuery.isBlank()) recipients
        else recipients.filter { it.name.contains(recipientSearchQuery.trim()) }

    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data class PensionListLoaded(val list: List<PensionInquiryPR>) : PartialState()
        data class SendingCertificate(val isSending: Boolean) : PartialState()
        data class SendSuccess(val message: String?) : PartialState()
        data object DismissSuccess : PartialState()
        data object DismissError : PartialState()
        data class CertificateSheetVisible(val visible: Boolean) : PartialState()
        data class RecipientsSheetVisible(val visible: Boolean) : PartialState()
        data class LoadingRecipients(val isLoading: Boolean) : PartialState()
        data class RecipientsLoaded(val list: List<RecipientPR>) : PartialState()
        data class RecipientSearchChanged(val query: String) : PartialState()
        data class RecipientSelected(val recipient: RecipientPR) : PartialState()
        data class BranchNameChanged(val name: String) : PartialState()
        data object RecipientMissing : PartialState()
    }
}

sealed class PensionStatusInquiryIntent {
    data object Load : PensionStatusInquiryIntent()
    data object OnBackClicked : PensionStatusInquiryIntent()
    data object OnRetry : PensionStatusInquiryIntent()
    data object DismissError : PensionStatusInquiryIntent()
    data object DismissSuccess : PensionStatusInquiryIntent()
    data object OnSendCertificateClicked : PensionStatusInquiryIntent()
    data object DismissCertificateSheet : PensionStatusInquiryIntent()
    data object OnSelectRecipientClicked : PensionStatusInquiryIntent()
    data object DismissRecipientsSheet : PensionStatusInquiryIntent()
    data class OnRecipientSearchChanged(val query: String) : PensionStatusInquiryIntent()
    data class OnRecipientSelected(val recipient: RecipientPR) : PensionStatusInquiryIntent()
    data class OnBranchNameChanged(val name: String) : PensionStatusInquiryIntent()
    data object OnIssueCertificateClicked : PensionStatusInquiryIntent()
}

sealed class PensionStatusInquiryEvent {
    data object NavigateBack : PensionStatusInquiryEvent()
    data class ShowToast(val message: String) : PensionStatusInquiryEvent()
}
