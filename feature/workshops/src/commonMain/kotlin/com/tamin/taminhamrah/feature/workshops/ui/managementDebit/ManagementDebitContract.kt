package com.tamin.taminhamrah.feature.workshops.ui.managementDebit

import androidx.compose.runtime.Immutable
import taminx.core.core_ui.ws_form_err_docs
import taminx.core.core_ui.ws_form_err_agree
import taminx.core.core_ui.Res
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.PersistentList
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachment
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.model.workshop.ArticleSixteenDebtPR
import com.tamin.taminhamrah.model.workshop.ArticleSixteenRequestStatus
import com.tamin.taminhamrah.model.workshop.ArticleSixteenWorkshopInfoPR
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
    val list: PagedListState<ArticleSixteenDebtPR> = PagedListState(),
    val draft: ArticleSixteenSearch = ArticleSixteenSearch(),
    val applied: ArticleSixteenSearch = ArticleSixteenSearch(),
    val isSearchOpen: Boolean = false,
    val statusFilter: ArticleSixteenRequestStatus? = null,
    val isBusy: Boolean = false,
    val viewerPdf: PdfDownloadPR? = null,
    /** پیام کارشناس, once fetched for a نقص مدارک row. */
    val expertMessage: String? = null,
    /** درخواست رسیدگی به بدهی, once a row has asked for it. */
    val form: ArticleSixteenFormState? = null,
    /** The tracking code of the request just filed, held until its dialog is dismissed. */
    val filedReferenceCode: String? = null,
) {
    /** What the list shows: the loaded rows, narrowed by the status filter when one is picked. */
    val visibleDebts: ImmutableList<ArticleSixteenDebtPR>
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
        data class Loaded(val list: PagedListState<ArticleSixteenDebtPR>) : PartialState
        data class DraftChanged(val draft: ArticleSixteenSearch) : PartialState
        data class Applied(val search: ArticleSixteenSearch) : PartialState
        data class SearchOpenChanged(val isOpen: Boolean) : PartialState
        data class StatusFilterChanged(val status: ArticleSixteenRequestStatus?) : PartialState
        data class Busy(val isBusy: Boolean) : PartialState
        data class ViewerPdfChanged(val pdf: PdfDownloadPR?) : PartialState

        // -------------------------------------------- درخواست رسیدگی به بدهی
        data class FormChanged(val form: ArticleSixteenFormState?) : PartialState
        data class FormStepChanged(val step: Int) : PartialState
        data class FormDebtOpenChanged(val isOpen: Boolean) : PartialState
        data class FormWorkshopOpenChanged(val isOpen: Boolean) : PartialState
        data class FormConfirmedChanged(val isConfirmed: Boolean) : PartialState
        data class FormAttachmentAdded(val attachment: WorkshopAttachment) : PartialState
        data class FormAttachmentRemoved(val index: Int) : PartialState
        data object FormSubmitRejected : PartialState
        data class FormUploadingChanged(val isUploading: Boolean) : PartialState
        data class FormSubmittingChanged(val isSubmitting: Boolean) : PartialState
        data object FormResubmitNoticeDismissed : PartialState
        data class ExpertMessageChanged(val message: String?) : PartialState
        data class FiledChanged(val referenceCode: String?) : PartialState
    }
}

/** The two fields the ماده ۱۶ search panel submits. */
@Immutable
data class ArticleSixteenSearch(
    val debitNumber: String = "",
    val agreementRow: String = "",
) {
    val isNotEmpty: Boolean get() = debitNumber.isNotBlank() || agreementRow.isNotBlank()
}

/**
 * درخواست رسیدگی به بدهی — two steps: check what is being asked about, then attach the
 * evidence for it.
 *
 * [uploaded] keeps only the guides the service handed back, never the bytes.
 */
@Immutable
data class ArticleSixteenFormState(
    val debt: ArticleSixteenDebtPR,
    val workshopInfo: ArticleSixteenWorkshopInfoPR = ArticleSixteenWorkshopInfoPR(),
    val step: Int = 1,
    val isDebtOpen: Boolean = true,
    val isWorkshopOpen: Boolean = false,
    val attachments: PersistentList<WorkshopAttachment> = persistentListOf(),
    val isConfirmed: Boolean = false,
    val hasTriedSubmit: Boolean = false,
    val isUploading: Boolean = false,
    val isSubmitting: Boolean = false,
    /**
     * The one-resubmission warning an اصلاح درخواست opens with, as the old app showed it: a
     * نقص مدارک request can be corrected only once.
     */
    val isResubmitNoticeOpen: Boolean = false,
) {
    val isBusy: Boolean get() = isUploading || isSubmitting
    val isLastStep: Boolean get() = step == ARTICLE_SIXTEEN_FORM_STEPS

    /** Which rule is stopping to submit, or null once none is. */
    val error: StringResource?
        get() = when {
            !hasTriedSubmit || !isLastStep -> null
            attachments.isEmpty() -> Res.string.ws_form_err_docs
            !isConfirmed -> Res.string.ws_form_err_agree
            else -> null
        }

    /**
     * Whether the missing-document rule is the one stopping this form.
     *
     * The panel draws that one itself, as a border; the error line prints whatever is left.
     */
    val isDocumentsError: Boolean
        get() = error == Res.string.ws_form_err_docs
}

/** How many steps درخواست رسیدگی به بدهی has. */
const val ARTICLE_SIXTEEN_FORM_STEPS = 2

sealed interface ManagementDebitIntent {
    data class Open(
        val workshopId: String,
        val branchCode: String,
        val workshopName: String,
    ) : ManagementDebitIntent

    data object LoadMore : ManagementDebitIntent
    data object Retry : ManagementDebitIntent
    data class SearchOpenChanged(val isOpen: Boolean) : ManagementDebitIntent
    data class DraftChanged(val draft: ArticleSixteenSearch) : ManagementDebitIntent
    data object ApplySearch : ManagementDebitIntent
    data object ClearSearch : ManagementDebitIntent

    /** Applies [search] as it stands — what removing one of the applied-search chips does. */
    data class ReplaceSearch(val search: ArticleSixteenSearch) : ManagementDebitIntent
    data class StatusFilterChanged(val status: ArticleSixteenRequestStatus?) : ManagementDebitIntent

    /** درخواست رسیدگی — allowed only inside the one-year window after ابلاغ اجراییه. */
    data class RequestReview(val debt: ArticleSixteenDebtPR) : ManagementDebitIntent

    /** اصلاح درخواست — the same form, told it is correcting a نقص مدارک request. */
    data class FixRequest(val debt: ArticleSixteenDebtPR) : ManagementDebitIntent

    /** مشاهده درخواست — the filed request as a PDF. */
    data class ShowRequestPdf(val debt: ArticleSixteenDebtPR) : ManagementDebitIntent

    /** پیام کارشناس — what the reviewer asked for. */
    data class ShowExpertMessage(val debt: ArticleSixteenDebtPR) : ManagementDebitIntent

    data object DismissViewer : ManagementDebitIntent
    data object DismissExpertMessage : ManagementDebitIntent
    data object DismissFiled : ManagementDebitIntent

    // ------------------------------------------------ درخواست رسیدگی به بدهی
    data object FormDismissed : ManagementDebitIntent
    data object FormResubmitNoticeDismissed : ManagementDebitIntent
    data object FormNext : ManagementDebitIntent
    data object FormPrev : ManagementDebitIntent
    data class FormDebtOpenChanged(val isOpen: Boolean) : ManagementDebitIntent
    data class FormWorkshopOpenChanged(val isOpen: Boolean) : ManagementDebitIntent
    data class FormConfirmedChanged(val isConfirmed: Boolean) : ManagementDebitIntent

    /** A picked file, with the ground the user filed it under. */
    class FormAddDocument(
        val fileName: String,
        val bytes: ByteArray,
        val typeCode: String,
    ) : ManagementDebitIntent

    data class FormRemoveDocument(val index: Int) : ManagementDebitIntent
}

sealed interface ManagementDebitEvent {

    /**
     * Something the service refused, in its own words.
     *
     * Raised rather than folded into the list's error state, because a form covers the
     * list — a 403 on an upload used to update a screen nobody could see.
     */
    data class ShowServerMessage(val message: String) : ManagementDebitEvent

    data class ShowMessage(val message: StringResource) : ManagementDebitEvent
}
