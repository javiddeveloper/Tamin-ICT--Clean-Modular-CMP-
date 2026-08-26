package com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.model.common.isValidIranianNationalId
import taminx.core.core_ui.ws_form_err_docs
import taminx.core.core_ui.ws_form_err_agree
import taminx.core.core_ui.abs_form_err_national_id
import taminx.core.core_ui.abs_form_err_incomplete
import taminx.core.core_ui.Res
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.PersistentList
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachment
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.workshop.NewMemberRequestStatus
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberPR
import org.jetbrains.compose.resources.StringResource

/**
 * State of نام نویسی غیر حضوری بیمه شده.
 *
 * The branch reaches the query as a typed field here. In the old client it traveled under one key
 * and was read under another, so the screen never made a request at all and showed an empty list
 * with no error.
 */
@Immutable
data class WorkshopRecentlyAddedMembersUiState(
    val workshopId: String = "",
    val branchCode: String = "",
    val list: PagedListState<WorkshopNewMemberPR> = PagedListState(),
    val draft: NewMemberSearch = NewMemberSearch(),
    val applied: NewMemberSearch = NewMemberSearch(),
    val isSearchOpen: Boolean = false,
    /** The row being confirmed or deleted; its actions show progress meanwhile. */
    val busyPersonalId: Long? = null,
    /** The blank declaration form, once fetched — shown in the app's PDF viewer. */
    val declarationPdf: PdfDownloadPR? = null,
    /** افزودن پرسنل جدید, once the list has asked for it. */
    val form: RegistrationFormState? = null,
) {
    sealed interface PartialState {
        data class Opened(val workshopId: String, val branchCode: String) : PartialState
        data object Loading : PartialState
        data object LoadingMore : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val list: PagedListState<WorkshopNewMemberPR>) : PartialState
        data class DraftChanged(val draft: NewMemberSearch) : PartialState
        data class Applied(val search: NewMemberSearch) : PartialState
        data class SearchOpenChanged(val isOpen: Boolean) : PartialState
        data class Busy(val personalId: Long?) : PartialState

        // --------------------------------------------------- نام‌نویسی غیرحضوری
        data class FormChanged(val form: RegistrationFormState?) : PartialState
        data class FormStepChanged(val step: Int) : PartialState
        data class FormFieldChanged(val field: RegistrationField, val value: String) :
            PartialState

        data class FormOptionPicked(
            val picker: RegistrationPicker,
            val option: PickedOption,
        ) : PartialState

        data class FormSummaryToggled(val isOpen: Boolean) : PartialState
        data class FormConfirmedChanged(val isConfirmed: Boolean) : PartialState
        data class FormAttachmentAdded(val attachment: WorkshopAttachment) : PartialState
        data class FormAttachmentRemoved(val index: Int) : PartialState
        data object FormNextRejected : PartialState
        data class FormUploadingChanged(val isUploading: Boolean) : PartialState
        data class FormSubmittingChanged(val isSubmitting: Boolean) : PartialState
        data class FormDeclarationDownloading(val isDownloading: Boolean) : PartialState
        data class DeclarationPdfChanged(val pdf: PdfDownloadPR?) : PartialState
        data class FormPickerOpened(val picker: RegistrationPicker?) : PartialState
        data class FormPickerQueryChanged(val query: String) : PartialState
        data class FormPickerLoading(val isLoading: Boolean) : PartialState
        data class FormPickerOptionsLoaded(val options: PersistentList<PickedOption>) :
            PartialState

    }
}

/** The two fields the search sheet submits. */
@Immutable
data class NewMemberSearch(
    val nationalId: String = "",
    val status: NewMemberRequestStatus? = null,
) {
    val isNotEmpty: Boolean get() = nationalId.isNotBlank() || status != null
}

/**
 * نام‌نویسی غیرحضوری — three steps: who the person is, where they are from and what they will do,
 * then the documents that evidence it.
 *
 * [uploaded] keeps only the guides the service handed back for each image, never the bytes.
 */
@Immutable
data class RegistrationFormState(
    val step: Int = 1,
    val firstName: String = "",
    val lastName: String = "",
    val nationalId: String = "",
    val birthDate: String = "",
    val birthCity: PickedOption? = null,
    val issueCity: PickedOption? = null,
    val job: PickedOption? = null,
    val startDate: String = "",
    val attachments: PersistentList<WorkshopAttachment> = persistentListOf(),
    val isConfirmed: Boolean = false,
    val isSummaryOpen: Boolean = true,
    val hasTriedNext: Boolean = false,
    val isUploading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isDownloadingDeclaration: Boolean = false,
    /** Set when a draft was re-opened, so the create updates that person. */
    val personalId: Long? = null,
    /** Which lookup sheet is open, and what it has to offer. */
    val picker: RegistrationPicker? = null,
    val pickerQuery: String = "",
    val pickerOptions: PersistentList<PickedOption> = persistentListOf(),
    val isPickerLoading: Boolean = false,
) {
    val isBusy: Boolean get() = isUploading || isSubmitting
    val isLastStep: Boolean get() = step == REGISTRATION_FORM_STEPS
    val fullName: String get() = "$firstName $lastName".trim()

    /** Whether the step the user is on has everything it needs. */
    val isStepComplete: Boolean
        get() = when (step) {
            1 -> firstName.isNotBlank() && lastName.isNotBlank() &&
                nationalId.isNotBlank() && birthDate.isNotBlank()

            2 -> birthCity != null && issueCity != null && job != null && startDate.isNotBlank()
            else -> attachments.isNotEmpty() && isConfirmed
        }

    /** Which rule is stopping this step, or null once none is. */
    val error: StringResource?
        get() = when {
            !hasTriedNext -> null
            step == 1 && !isStepComplete -> Res.string.abs_form_err_incomplete
            step == 1 && !isValidIranianNationalId(nationalId) ->
                Res.string.abs_form_err_national_id

            step == 2 && !isStepComplete -> Res.string.abs_form_err_incomplete
            step == REGISTRATION_FORM_STEPS && attachments.isEmpty() ->
                Res.string.ws_form_err_docs

            step == REGISTRATION_FORM_STEPS && !isConfirmed -> Res.string.ws_form_err_agree
            else -> null
        }
}

/** A value chosen from a lookup: what the service files, and what the field shows. */
@Immutable
data class PickedOption(val code: String, val label: String)

/** Which typed field of the registration an edit is for. */
enum class RegistrationField { FIRST_NAME, LAST_NAME, NATIONAL_ID, BIRTH_DATE, START_DATE }

/** Which lookup a picker is currently asking for. */
enum class RegistrationPicker { BIRTH_CITY, ISSUE_CITY, JOB }

/** How many steps نام‌نویسی غیرحضوری has. */
const val REGISTRATION_FORM_STEPS = 3


sealed interface WorkshopRecentlyAddedMembersIntent {
    data class Open(
        val workshopId: String,
        val branchCode: String,
    ) : WorkshopRecentlyAddedMembersIntent

    data object LoadMore : WorkshopRecentlyAddedMembersIntent
    data object Retry : WorkshopRecentlyAddedMembersIntent
    data class SearchOpenChanged(val isOpen: Boolean) : WorkshopRecentlyAddedMembersIntent
    data class DraftChanged(val draft: NewMemberSearch) : WorkshopRecentlyAddedMembersIntent
    data object ApplySearch : WorkshopRecentlyAddedMembersIntent
    data object ClearSearch : WorkshopRecentlyAddedMembersIntent

    /** تایید — only a drafted registration can be confirmed. */
    data class Confirm(val member: WorkshopNewMemberPR) : WorkshopRecentlyAddedMembersIntent

    /** حذف — same guard as confirm. */
    data class Delete(val member: WorkshopNewMemberPR) : WorkshopRecentlyAddedMembersIntent

    data class Edit(val member: WorkshopNewMemberPR) : WorkshopRecentlyAddedMembersIntent
    data class Follow(val member: WorkshopNewMemberPR) : WorkshopRecentlyAddedMembersIntent

    // ------------------------------------------------------- نام‌نویسی غیرحضوری
    data object FormDismissed : WorkshopRecentlyAddedMembersIntent
    data object FormNext : WorkshopRecentlyAddedMembersIntent
    data object FormPrev : WorkshopRecentlyAddedMembersIntent
    data class FormFieldChanged(val field: RegistrationField, val value: String) :
        WorkshopRecentlyAddedMembersIntent

    data class FormSummaryToggled(val isOpen: Boolean) : WorkshopRecentlyAddedMembersIntent
    data class FormConfirmedChanged(val isConfirmed: Boolean) :
        WorkshopRecentlyAddedMembersIntent

    /** Jumps back to a step from the summary's «ویرایش اطلاعات». */
    data class FormStepRequested(val step: Int) : WorkshopRecentlyAddedMembersIntent

    data class FormOptionPicked(
        val picker: RegistrationPicker,
        val option: PickedOption,
    ) : WorkshopRecentlyAddedMembersIntent

    /** Opens a lookup sheet; null closes it. */
    data class FormPickerOpened(val picker: RegistrationPicker?) :
        WorkshopRecentlyAddedMembersIntent

    data class FormPickerQueryChanged(val query: String) : WorkshopRecentlyAddedMembersIntent

    /** «دریافت فرم اظهارنامهٔ نام‌نویسی» — the blank declaration the person fills in. */
    data object FormDownloadDeclaration : WorkshopRecentlyAddedMembersIntent

    data object DeclarationViewerDismissed : WorkshopRecentlyAddedMembersIntent

    /** A picked image, with the type the user filed it under. */
    class FormAddDocument(
        val fileName: String,
        val bytes: ByteArray,
        val typeCode: String,
    ) : WorkshopRecentlyAddedMembersIntent

    data class FormRemoveDocument(val index: Int) : WorkshopRecentlyAddedMembersIntent
}

sealed interface WorkshopRecentlyAddedMembersEvent {
    /** Confirmed, with the tracking code the service returned. A success, shown as one. */
    data class Confirmed(val referenceCode: String) : WorkshopRecentlyAddedMembersEvent

    data class ShowMessage(val message: StringResource) : WorkshopRecentlyAddedMembersEvent

    /** The member form opens on this registration; a new one is [personalRequestId] `0`. */
    /** Filed, with the tracking code the service returned. */
    data class RegistrationFiled(val referenceCode: String) : WorkshopRecentlyAddedMembersEvent

    /** پیگیری — the request cartable, opened on this registration rather than on nothing. */
    data class OpenCartable(val referenceCode: String) : WorkshopRecentlyAddedMembersEvent
}
