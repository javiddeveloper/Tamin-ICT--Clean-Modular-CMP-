package com.tamin.taminhamrah.feature.pensionInquiry.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.pension.PensionInquiryPR

@Immutable
data class PensionInquiryUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val pensionList: List<PensionInquiryPR> = emptyList(),
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data class PensionListLoaded(val list: List<PensionInquiryPR>) : PartialState()
    }
}

sealed class PensionInquiryIntent {
    data object LoadPensionInquiry : PensionInquiryIntent()
}

sealed class PensionInquiryEvent {
    data class ShowToast(val message: String) : PensionInquiryEvent()
}
