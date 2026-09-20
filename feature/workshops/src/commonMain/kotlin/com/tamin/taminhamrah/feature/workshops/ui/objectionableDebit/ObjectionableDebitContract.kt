package com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachment
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.model.workshop.WorkShopDebtPR
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ws_form_err_agree
import taminx.core.core_ui.ws_form_err_docs

/**
 * State of اعتراض به بدهی.
 *
 * Each row offers exactly one action, and which one is decided by the debt itself
 * ([WorkShopDebtPR.objectionKind]) rather than by the screen — filing against an estimate, filing
 * against a primary vote, or viewing an objection already filed.
 */
@Immutable
data class ObjectionableDebitUiState(
    val workshopId: String = "",
    val branchCode: String = "",
    val list: PagedListState<WorkShopDebtPR> = PagedListState(),
    /** The row whose deadline is being checked; its action shows progress meanwhile. */
    val checkingDebitNumber: String? = null,
    val isDownloading: Boolean = false,
    val viewerPdf: PdfDownloadPR? = null,
    /** ثبت اعتراض به بدهی, once a row's filing window has been confirmed open. */
    val form: ObjectionFormState? = null,
    /** The tracking code of the objection just filed, held until its dialog is dismissed. */
    val filedReferenceCode: String? = null,
) {
    /** A row's action is waiting on the service — its deadline check or its PDF. */
    val isBusy: Boolean get() = isDownloading || checkingDebitNumber != null

    sealed interface PartialState {
        data class Opened(val workshopId: String, val branchCode: String) : PartialState
        data object Loading : PartialState
        data object LoadingMore : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val list: PagedListState<WorkShopDebtPR>) : PartialState
        data class Checking(val debitNumber: String?) : PartialState
        data class Downloading(val isDownloading: Boolean) : PartialState
        data class ViewerPdfChanged(val pdf: PdfDownloadPR?) : PartialState

        // ---------------------------------------------------- ثبت اعتراض به بدهی
        data class FormChanged(val form: ObjectionFormState?) : PartialState
        data class FormDebtOpenChanged(val isOpen: Boolean) : PartialState
        data class FormDescriptionChanged(val text: String) : PartialState
        data class FormDepositChanged(val isDeposit: Boolean) : PartialState
        data class FormConfirmedChanged(val isConfirmed: Boolean) : PartialState
        data class FormAttachmentAdded(val attachment: WorkshopAttachment) : PartialState
        data class FormAttachmentRemoved(val index: Int) : PartialState
        data object FormSubmitRejected : PartialState
        data class FormUploadingChanged(val isUploading: Boolean) : PartialState
        data class FormSubmittingChanged(val isSubmitting: Boolean) : PartialState
        data class FormConfirmVisible(val isVisible: Boolean) : PartialState
        data class FiledChanged(val referenceCode: String?) : PartialState
    }
}

/**
 * ثبت اعتراض به بدهی — a single step, and everything it needs to submit.
 *
 * [uploaded] keeps only what the service handed back for each attachment: a guid it resolves
 * later. The bytes are never held, so a long form does not sit on a pile of photos.
 */
@Immutable
data class ObjectionFormState(
    val debt: WorkShopDebtPR,
    val isDebtOpen: Boolean = true,
    val description: String = "",
    val attachments: PersistentList<WorkshopAttachment> = persistentListOf(),
    val isDeposit: Boolean = false,
    val isConfirmed: Boolean = false,
    /**
     * Whether submit has been attempted.
     *
     * Errors stay hidden until it has: telling someone their form is incomplete before they
     * have finished filling it in is noise, not help.
     */
    val hasTriedSubmit: Boolean = false,
    val isUploading: Boolean = false,
    val isSubmitting: Boolean = false,
    /**
     * Whether the last word is being asked for.
     *
     * Raised only once every rule below has passed, so the dialog never appears over a form
     * that would be refused anyway.
     */
    val isConfirmVisible: Boolean = false,
) {
    val isBusy: Boolean get() = isUploading || isSubmitting

    /** Which rule is stopping to submit, or null once none is. */
    val error: StringResource?
        get() = when {
            !hasTriedSubmit -> null
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

sealed interface ObjectionableDebitIntent {
    data class Open(val workshopId: String, val branchCode: String) : ObjectionableDebitIntent
    data object LoadMore : ObjectionableDebitIntent
    data object Retry : ObjectionableDebitIntent

    /** The row's single action; what it does follows from the debt's objection kind. */
    data class RowAction(val debt: WorkShopDebtPR) : ObjectionableDebitIntent

    // -------------------------------------------------------- ثبت اعتراض به بدهی
    data object FormDismissed : ObjectionableDebitIntent
    data class FormDebtOpenChanged(val isOpen: Boolean) : ObjectionableDebitIntent
    data class FormDescriptionChanged(val text: String) : ObjectionableDebitIntent
    data class FormDepositChanged(val isDeposit: Boolean) : ObjectionableDebitIntent
    data class FormConfirmedChanged(val isConfirmed: Boolean) : ObjectionableDebitIntent

    /** A picked file, with the type the user filed it under. */
    class FormAddDocument(
        val fileName: String,
        val bytes: ByteArray,
        val typeCode: String,
    ) : ObjectionableDebitIntent

    data class FormRemoveDocument(val index: Int) : ObjectionableDebitIntent

    /** The footer button: it asks for confirmation rather than filing straight away. */
    data object FormSubmit : ObjectionableDebitIntent
    data object FormConfirmDismissed : ObjectionableDebitIntent
    data object FormConfirmAccepted : ObjectionableDebitIntent
    data object DismissViewer : ObjectionableDebitIntent
    data object DismissFiled : ObjectionableDebitIntent
}

sealed interface ObjectionableDebitEvent {

    /**
     * Something the service refused, in its own words.
     *
     * Raised rather than folded into the list's error state, because a form covers the
     * list — a 403 on an upload used to update a screen nobody could see.
     */
    data class ShowServerMessage(val message: String) : ObjectionableDebitEvent

    data class ShowMessage(val message: StringResource) : ObjectionableDebitEvent
}
