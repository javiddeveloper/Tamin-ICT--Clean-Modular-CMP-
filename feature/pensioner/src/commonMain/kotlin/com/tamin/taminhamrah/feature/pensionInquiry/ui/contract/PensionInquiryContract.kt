package com.tamin.taminhamrah.feature.pensionInquiry.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.pension.EdictPensionerPR
import com.tamin.taminhamrah.model.pension.PensionIdPR
import com.tamin.taminhamrah.model.pension.PensionInquiryPR

@Immutable
data class PensionInquiryUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val pensionList: List<PensionInquiryPR> = emptyList(),
    val pensionerIds: List<PensionIdPR> = emptyList(),
    val edictPensioner: EdictPensionerPR? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data class PensionListLoaded(val list: List<PensionInquiryPR>) : PartialState()
        data class PensionerIdsLoaded(val list: List<PensionIdPR>) : PartialState()
        data class EdictLoaded(val edict: EdictPensionerPR?) : PartialState()
    }
}

sealed class PensionInquiryIntent {
    data object LoadPensionInquiry : PensionInquiryIntent()
    data object LoadPensionerIds : PensionInquiryIntent()
    data class LoadEdict(val pensionerId: String) : PensionInquiryIntent()
}

sealed class PensionInquiryEvent {
    data class ShowToast(val message: String) : PensionInquiryEvent()
}
