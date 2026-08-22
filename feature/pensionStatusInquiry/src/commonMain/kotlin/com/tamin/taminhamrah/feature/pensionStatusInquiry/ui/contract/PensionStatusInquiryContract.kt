package com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.pension.PensionInquiryPR

@Immutable
data class PensionStatusInquiryUiState(
    val isLoading: Boolean = false,
    val isSendingCertificate: Boolean = false,
    val error: String? = null,
    val pensionList: List<PensionInquiryPR> = emptyList(),
    val successMessage: String? = null,
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data class PensionListLoaded(val list: List<PensionInquiryPR>) : PartialState()
        data class SendingCertificate(val isSending: Boolean) : PartialState()
        data class SendSuccess(val message: String?) : PartialState()
        data object DismissSuccess : PartialState()
        data object DismissError : PartialState()
    }
}

sealed class PensionStatusInquiryIntent {
    data object Load : PensionStatusInquiryIntent()
    data object OnBackClicked : PensionStatusInquiryIntent()
    data object OnRetry : PensionStatusInquiryIntent()
    data object DismissError : PensionStatusInquiryIntent()
    data object DismissSuccess : PensionStatusInquiryIntent()
    data class OnSendCertificateClicked(val item: PensionInquiryPR) : PensionStatusInquiryIntent()
}

sealed class PensionStatusInquiryEvent {
    data object NavigateBack : PensionStatusInquiryEvent()
    data class ShowToast(val message: String) : PensionStatusInquiryEvent()
}
