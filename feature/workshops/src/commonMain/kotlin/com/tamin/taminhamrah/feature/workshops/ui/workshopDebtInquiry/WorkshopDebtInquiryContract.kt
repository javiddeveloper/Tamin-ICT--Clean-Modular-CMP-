package com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry

import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryPR

data class WorkshopDebtInquiryUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val inquiryResult: WorkshopDebtInquiryPR? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class DebtInquiryLoaded(val result: WorkshopDebtInquiryPR?) : PartialState
    }
}

sealed interface WorkshopDebtInquiryIntent {
    data class LoadDebtInquiry(
        val workshopId: String?,
        val branchCode: String?
    ) : WorkshopDebtInquiryIntent
}

sealed interface WorkshopDebtInquiryEvent {
    data class ShowToast(val message: String) : WorkshopDebtInquiryEvent
}
