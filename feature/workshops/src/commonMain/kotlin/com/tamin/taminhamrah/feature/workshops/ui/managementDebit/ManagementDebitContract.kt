package com.tamin.taminhamrah.feature.workshops.ui.managementDebit

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.model.workshop.Article16DebtPR
import com.tamin.taminhamrah.model.workshop.Article16RequestStatus
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.StringResource

/**
 * State of رسیدگی به بدهی ماده ۱۶.
 *
 * The status filter runs over what is already loaded — the service takes no status parameter here,
 * so filtering is the screen's own. It is applied in [visibleDebts] rather than by rebuilding the
 * list, so picking a filter never re-fetches and never loses the loaded pages.
 */
@Immutable
data class ManagementDebitUiState(
    val workshopId: String = "",
    val branchCode: String = "",
    val workshopName: String = "",
    val list: PagedListState<Article16DebtPR> = PagedListState(),
    val draft: Article16Search = Article16Search(),
    val applied: Article16Search = Article16Search(),
    val isSearchOpen: Boolean = false,
    val statusFilter: Article16RequestStatus? = null,
    /** The row whose action sheet is open, or null while it is closed. */
    val actionsFor: Article16DebtPR? = null,
    val isBusy: Boolean = false,
    val viewerPdf: PdfDownloadPR? = null,
    /** پیام کارشناس, once fetched for a نقص مدارک row. */
    val expertMessage: String? = null,
) {
    /** What the list shows: the loaded rows, narrowed by the status filter when one is picked. */
    val visibleDebts: ImmutableList<Article16DebtPR>
        get() = statusFilter
            ?.let { status -> list.items.filter { it.status == status }.toImmutableList() }
            ?: list.items

    sealed interface PartialState {
        data class Opened(
            val workshopId: String,
            val branchCode: String,
            val workshopName: String,
        ) : PartialState

        data object Loading : PartialState
        data object LoadingMore : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val list: PagedListState<Article16DebtPR>) : PartialState
        data class DraftChanged(val draft: Article16Search) : PartialState
        data class Applied(val search: Article16Search) : PartialState
        data class SearchOpenChanged(val isOpen: Boolean) : PartialState
        data class StatusFilterChanged(val status: Article16RequestStatus?) : PartialState
        data class ActionsForChanged(val debt: Article16DebtPR?) : PartialState
        data class Busy(val isBusy: Boolean) : PartialState
        data class ViewerPdfChanged(val pdf: PdfDownloadPR?) : PartialState
        data class ExpertMessageChanged(val message: String?) : PartialState
    }
}

/** The two fields the ماده ۱۶ search panel submits. */
@Immutable
data class Article16Search(
    val debitNumber: String = "",
    val agreementRow: String = "",
) {
    val isNotEmpty: Boolean get() = debitNumber.isNotBlank() || agreementRow.isNotBlank()
}

sealed interface ManagementDebitIntent {
    data class Open(
        val workshopId: String,
        val branchCode: String,
        val workshopName: String,
    ) : ManagementDebitIntent

    data object LoadMore : ManagementDebitIntent
    data object Retry : ManagementDebitIntent
    data class SearchOpenChanged(val isOpen: Boolean) : ManagementDebitIntent
    data class DraftChanged(val draft: Article16Search) : ManagementDebitIntent
    data object ApplySearch : ManagementDebitIntent
    data object ClearSearch : ManagementDebitIntent
    data class StatusFilterChanged(val status: Article16RequestStatus?) : ManagementDebitIntent

    data class ActionsRequested(val debt: Article16DebtPR) : ManagementDebitIntent
    data object ActionsDismissed : ManagementDebitIntent

    /** درخواست رسیدگی — allowed only inside the one-day window after ابلاغ اجراییه. */
    data class RequestReview(val debt: Article16DebtPR) : ManagementDebitIntent

    /** اصلاح درخواست — the same form, told it is correcting a نقص مدارک request. */
    data class FixRequest(val debt: Article16DebtPR) : ManagementDebitIntent

    /** مشاهده درخواست — the filed request as a PDF. */
    data class ShowRequestPdf(val debt: Article16DebtPR) : ManagementDebitIntent

    /** پیام کارشناس — what the reviewer asked for. */
    data class ShowExpertMessage(val debt: Article16DebtPR) : ManagementDebitIntent

    data object DismissViewer : ManagementDebitIntent
    data object DismissExpertMessage : ManagementDebitIntent
}

sealed interface ManagementDebitEvent {
    /** The four-step request form opens on this debt; [status] is null for a first request. */
    data class OpenRequestForm(
        val debt: Article16DebtPR,
        val status: Article16RequestStatus?,
    ) : ManagementDebitEvent

    data class ShowMessage(val message: StringResource) : ManagementDebitEvent
}
