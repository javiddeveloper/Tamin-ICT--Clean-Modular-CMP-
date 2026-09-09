package com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryPR

/**
 * State of استعلام بدهی کارگاه — a single record, not a list.
 *
 * A service answer with no record is [isEmpty] rather than a silently blank screen, which is what
 * the old one showed.
 */
@Immutable
data class WorkshopDebtInquiryUiState(
    val workshopId: String = "",
    val branchCode: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val inquiry: WorkshopDebtInquiryPR? = null,
) {
    val isEmpty: Boolean get() = !isLoading && error == null && inquiry == null

    sealed interface PartialState {
        data class Opened(val workshopId: String, val branchCode: String) : PartialState
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val inquiry: WorkshopDebtInquiryPR) : PartialState
    }
}

sealed interface WorkshopDebtInquiryIntent {
    data class Open(val workshopId: String, val branchCode: String) : WorkshopDebtInquiryIntent
    data object Retry : WorkshopDebtInquiryIntent
}

sealed interface WorkshopDebtInquiryEvent
