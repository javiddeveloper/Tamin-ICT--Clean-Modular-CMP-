package com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.pension.PensionIdPR
import com.tamin.taminhamrah.model.pension.EdictPensionerPR
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR

@Immutable
data class EdictUiState(
    val isLoading: Boolean = false,
    val isSendingToInbox: Boolean = false,
    val error: String? = null,
    val pensionerIds: List<PensionIdPR> = emptyList(),
    val selectedPensionerId: String? = null,
    val startDate: String = "",
    val edictPensioner: EdictPensionerPR? = null,
    val showPensionerSheet: Boolean = false,
    val showSearchSheet: Boolean = false,
    val searchYear: String = "1405",
    val searchMonth: String = "",
    val isDateFilteredBySearch: Boolean = false,
    val showSendConfirmation: Boolean = false,
    val showSendSuccess: Boolean = false,
    val viewerPdf: PdfDownloadPR? = null,
    val viewerDownloadFailed: Boolean = false,
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class SendingToInbox(val isSending: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class PensionerIdsLoaded(val list: List<PensionIdPR>) : PartialState
        data class SelectedPensionerIdChanged(val id: String) : PartialState
        data class StartDateChanged(val date: String) : PartialState
        data class EdictLoaded(val edict: EdictPensionerPR?) : PartialState
        data class ShowPensionerSheet(val show: Boolean) : PartialState
        data class ShowSearchSheet(val show: Boolean) : PartialState
        data class SearchYearChanged(val year: String) : PartialState
        data class SearchMonthChanged(val month: String) : PartialState
        data class DateFilteredBySearch(val filtered: Boolean) : PartialState
        data class ShowSendConfirmation(val show: Boolean) : PartialState
        data class ShowSendSuccess(val show: Boolean) : PartialState
        data class ViewerPdfChanged(val pdf: PdfDownloadPR?) : PartialState
        data object ViewerDownloadFailed : PartialState
    }
}

sealed interface EdictIntent {
    data object LoadPensionerIds : EdictIntent
    data class ChangeSelectedPensionerId(val id: String) : EdictIntent
    data class ChangeStartDate(val date: String) : EdictIntent
    data object LoadEdict : EdictIntent
    data object RequestSendToInbox : EdictIntent
    data object ConfirmSendToInbox : EdictIntent
    data object DismissSendConfirmation : EdictIntent
    data object DismissSendSuccess : EdictIntent
    data object DownloadPdf : EdictIntent
    data object DismissPdfViewer : EdictIntent
    data object ShowPensionerSheet : EdictIntent
    data object DismissPensionerSheet : EdictIntent
    data object ShowSearchSheet : EdictIntent
    data object DismissSearchSheet : EdictIntent
    data class ChangeSearchYear(val year: String) : EdictIntent
    data class ChangeSearchMonth(val month: String) : EdictIntent
    data object ApplySearch : EdictIntent
    data object ClearDateFilter : EdictIntent
}

sealed interface EdictEvent {
    data class ShowToast(val message: String) : EdictEvent
}
