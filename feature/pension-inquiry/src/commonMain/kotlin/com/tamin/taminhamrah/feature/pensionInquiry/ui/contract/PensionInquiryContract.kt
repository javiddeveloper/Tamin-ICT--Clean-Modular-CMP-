package com.tamin.taminhamrah.feature.pensionInquiry.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.pension.PensionIdPR
import com.tamin.taminhamrah.model.pension.PensionInquiryPR
import com.tamin.taminhamrah.model.pension.RecipientPR

@Immutable
data class PensionInquiryUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val pensionList: List<PensionInquiryPR> = emptyList(),
    val pensionerIds: List<PensionIdPR> = emptyList(),
    val recipients: List<RecipientPR> = emptyList()
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data class PensionListLoaded(val list: List<PensionInquiryPR>) : PartialState()
        data class PensionerIdsLoaded(val list: List<PensionIdPR>) : PartialState()
        data class RecipientsLoaded(val list: List<RecipientPR>) : PartialState()
    }
}

sealed class PensionInquiryIntent {
    data object LoadPensionInquiry : PensionInquiryIntent()
    data object LoadPensionerIds : PensionInquiryIntent()
    data object LoadRecipients : PensionInquiryIntent()
}

sealed class PensionInquiryEvent {
    data class ShowToast(val message: String) : PensionInquiryEvent()
}
