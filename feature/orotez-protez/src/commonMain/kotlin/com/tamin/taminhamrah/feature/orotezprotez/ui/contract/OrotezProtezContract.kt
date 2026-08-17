package com.tamin.taminhamrah.feature.orotezprotez.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.orotezprotez.ui.OrotezProtezStep
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.orotez_protez_document_ear_mold
import taminx.core.core_ui.orotez_protez_document_hearing_aid_warranty
import taminx.core.core_ui.orotez_protez_document_invoice
import taminx.core.core_ui.orotez_protez_document_prescription

/** One selectable row inside the branch/insured-person picker sheets. */
@Immutable
data class OrotezProtezOptionUi(
    val id: String,
    val label: String,
    /** The insured person's insurance number line. Null for branch options, which have none. */
    val subtitle: String? = null,
)

/** Which picker sheet is open, if any — only one can be at a time. */
enum class OrotezProtezPicker { NONE, BRANCH, INSURED_PERSON, DATE, DOCUMENT_SOURCE }

/** Where a document image would come from — the choice made in the step-3 source sheet. */
enum class OrotezProtezImageSource { CAMERA, GALLERY }

/**
 * One document slot in step 3's upload checklist. Per-document upload state (file, progress,
 * error) is looked up by [id] from [OrotezProtezUiState.documents], the same way step 2 looks up
 * [OrotezProtezInsuredDetailUi] by insured-person id.
 */
@Immutable
data class OrotezProtezDocumentUi(
    val id: String,
    val titleRes: StringResource,
    val isRequired: Boolean,
)

/**
 * The step-3 document checklist. [OrotezProtezDocumentUi.id] doubles as the `documentType` code
 * sent in [OrotezProtezDocumentSubmissionUi] — these are the real backend codes (Constants.kt's
 * `*_OROTEZ_IMAGE_TYPE`/`*_HEARING_AIDS_IMAGE_TYPE` in the legacy client).
 */
val OrotezProtezDocumentChecklist: ImmutableList<OrotezProtezDocumentUi> = persistentListOf(
    OrotezProtezDocumentUi(
        id = "0401",
        titleRes = Res.string.orotez_protez_document_prescription,
        isRequired = true,
    ),
    OrotezProtezDocumentUi(
        id = "0402",
        titleRes = Res.string.orotez_protez_document_invoice,
        isRequired = true,
    ),
    OrotezProtezDocumentUi(
        id = "0403",
        titleRes = Res.string.orotez_protez_document_ear_mold,
        isRequired = false,
    ),
    OrotezProtezDocumentUi(
        id = "0404",
        titleRes = Res.string.orotez_protez_document_hearing_aid_warranty,
        isRequired = false,
    ),
)

/**
 * Per-document upload state, keyed by [OrotezProtezDocumentUi.id] in
 * [OrotezProtezUiState.documents]. [platformFile] is the picked/captured temp file — kept around
 * so it can be reused for a large preview and deleted once it's no longer needed (replaced,
 * removed, or the screen is left).
 */
@Immutable
sealed interface OrotezProtezDocumentState {
    data object Empty : OrotezProtezDocumentState

    data class Uploading(
        val platformFile: PlatformFile,
        val bytes: ByteArray,
    ) : OrotezProtezDocumentState

    data class Uploaded(
        val guid: String,
        val platformFile: PlatformFile,
        val bytes: ByteArray,
    ) : OrotezProtezDocumentState

    data class Failed(
        val message: String,
        val platformFile: PlatformFile? = null,
        val bytes: ByteArray? = null,
    ) : OrotezProtezDocumentState
}

/** The temp file backing this state, if any — used for cleanup ([io.github.vinceglb.filekit.delete]) and dedup checks. */
fun OrotezProtezDocumentState.platformFileOrNull(): PlatformFile? = when (this) {
    is OrotezProtezDocumentState.Uploading -> platformFile
    is OrotezProtezDocumentState.Uploaded -> platformFile
    is OrotezProtezDocumentState.Failed -> platformFile
    OrotezProtezDocumentState.Empty -> null
}

/** The raw bytes backing this state, if any — used for the inline/large preview and duplicate-content checks. */
fun OrotezProtezDocumentState.bytesOrNull(): ByteArray? = when (this) {
    is OrotezProtezDocumentState.Uploading -> bytes
    is OrotezProtezDocumentState.Uploaded -> bytes
    is OrotezProtezDocumentState.Failed -> bytes
    OrotezProtezDocumentState.Empty -> null
}

/**
 * What the final submit sends for one uploaded document — `documentFile` is the upload's guid,
 * `documentType` is [OrotezProtezDocumentUi.id].
 */
data class OrotezProtezDocumentSubmissionUi(
    val documentFile: String,
    val documentType: String,
)

/** The read-only registry record shown on step 2, keyed off the step-1 [OrotezProtezOptionUi.id]. */
@Immutable
data class OrotezProtezInsuredDetailUi(
    val fullName: String,
    val firstName: String,
    val lastName: String,
    val relation: String,
    val relationCode: String,
    val nationalCode: String,
    val birthCertificateNumber: String,
    val issuePlace: String,
    val birthDateLabel: String,
    val bookletValidUntilLabel: String,
)

/** Raw branch/workshop codes behind a step-1 [OrotezProtezOptionUi], needed for the final submit body. */
@Immutable
data class OrotezProtezBranchDetailUi(
    val branchCode: String?,
    val branchName: String?,
)

/**
 * The policyholder fields loaded once in step 1 ([com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDN])
 * and carried forward untouched to the final submit body — never re-entered or derived from UI state.
 */
@Immutable
data class OrotezProtezMainInfoUi(
    val risuid: String?,
    val nationalCode: String?,
    val firstName: String?,
    val lastName: String?,
    val mobileNumber: String?,
)

@Immutable
data class OrotezProtezUiState(
    val currentStep: OrotezProtezStep = OrotezProtezStep.UserSelection,
    val isLoading: Boolean = false,
    val error: String? = null,
    val branch: OrotezProtezOptionUi? = null,
    val insuredPerson: OrotezProtezOptionUi? = null,
    val prescriptionDateLabel: String? = null,
    /** Midnight local-time of [prescriptionDateLabel]'s Jalali date, set alongside it. Needed by the final submit body. */
    val prescriptionDateTimeStamp: Long? = null,
    val branchOptions: ImmutableList<OrotezProtezOptionUi> = persistentListOf(),
    val insuredPersonOptions: ImmutableList<OrotezProtezOptionUi> = persistentListOf(),
    /** Registry detail per insured-person id — loaded once, looked up by [insuredPerson]. */
    val insuredPersonDetails: ImmutableMap<String, OrotezProtezInsuredDetailUi> = persistentMapOf(),
    /** Raw branch/workshop codes per [OrotezProtezOptionUi.id] — loaded once, looked up by [branch]. */
    val branchDetails: ImmutableMap<String, OrotezProtezBranchDetailUi> = persistentMapOf(),
    /** The policyholder loaded once in step 1, carried forward for the final submit body. */
    val mainInfo: OrotezProtezMainInfoUi? = null,
    val picker: OrotezProtezPicker = OrotezProtezPicker.NONE,
    /** The document whose card was tapped to open [OrotezProtezPicker.DOCUMENT_SOURCE]. */
    val activeDocumentId: String? = null,
    /** Upload state per [OrotezProtezDocumentUi.id] — absent/[OrotezProtezDocumentState.Empty] entries render the dashed empty card. */
    val documents: ImmutableMap<String, OrotezProtezDocumentState> = persistentMapOf(),
    /** Rejection reason for the most recent pick (duplicate image, wrong format, camera permission denied, ...). */
    val documentPickError: String? = null,
    /** Set only after a submit attempt fails validation — cleared on the next successful pick/remove. */
    val documentValidationError: String? = null,
    val isSubmitting: Boolean = false,
    /** The final submit network call's failure message, if the last attempt failed. */
    val submitError: String? = null,
    /** Whether the final submit has already succeeded once — the submit button stays disabled past that point. */
    val hasSubmitted: Boolean = false,
    /** The backend's own confirmation text, when the final submit succeeds and it returns one. */
    val submittedResultMessage: String? = null,
) {
    val canGoNext: Boolean
        get() = branch != null && insuredPerson != null && prescriptionDateTimeStamp != null

    /** Step 2's whole content: derived from the step-1 selection, never stored independently. */
    val selectedInsuredDetail: OrotezProtezInsuredDetailUi?
        get() = insuredPerson?.id?.let { insuredPersonDetails[it] }

    val isAnyDocumentUploading: Boolean
        get() = documents.values.any { it is OrotezProtezDocumentState.Uploading }

    /** The exact payload the final submit sends for documents. */
    val documentSubmissionPayload: List<OrotezProtezDocumentSubmissionUi>
        get() = documents.entries.mapNotNull { (documentId, state) ->
            (state as? OrotezProtezDocumentState.Uploaded)?.let {
                OrotezProtezDocumentSubmissionUi(documentFile = it.guid, documentType = documentId)
            }
        }

    val uploadedDocumentIds: Set<String>
        get() = documents.filterValues { it is OrotezProtezDocumentState.Uploaded }.keys

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState
        data class DataLoaded(
            val branch: OrotezProtezOptionUi?,
            val branchOptions: ImmutableList<OrotezProtezOptionUi>,
            val branchDetails: ImmutableMap<String, OrotezProtezBranchDetailUi>,
            val insuredPersonOptions: ImmutableList<OrotezProtezOptionUi>,
            val insuredPersonDetails: ImmutableMap<String, OrotezProtezInsuredDetailUi>,
            val mainInfo: OrotezProtezMainInfoUi?,
        ) : PartialState
        data class PickerChanged(val picker: OrotezProtezPicker) : PartialState
        data class DocumentSourceRequested(val documentId: String) : PartialState
        data class BranchSelected(val branch: OrotezProtezOptionUi) : PartialState
        data class InsuredPersonSelected(val insuredPerson: OrotezProtezOptionUi) : PartialState
        data class PrescriptionDateSelected(val millis: Long, val label: String) : PartialState
        data class StepChanged(val step: OrotezProtezStep) : PartialState
        data class DocumentStateChanged(val documentId: String, val state: OrotezProtezDocumentState) : PartialState
        data class DocumentPickRejected(val message: String) : PartialState
        data class DocumentValidationFailed(val message: String) : PartialState
        data object DocumentsReadyForSubmission : PartialState
        data class Submitting(val isSubmitting: Boolean) : PartialState
        data class SubmitSucceeded(val resultMessage: String?) : PartialState
        data class SubmitFailed(val message: String) : PartialState
    }
}

sealed interface OrotezProtezIntent {
    data object LoadInitialData : OrotezProtezIntent
    data class OnPickerRequested(val picker: OrotezProtezPicker) : OrotezProtezIntent
    data object OnPickerDismissed : OrotezProtezIntent
    data class OnBranchPicked(val option: OrotezProtezOptionUi) : OrotezProtezIntent
    data class OnInsuredPersonPicked(val option: OrotezProtezOptionUi) : OrotezProtezIntent
    data class OnPrescriptionDatePicked(val millis: Long, val label: String) : OrotezProtezIntent
    data class OnDocumentCardClicked(val documentId: String) : OrotezProtezIntent
    /** Chosen in the step-3 source sheet — closes the sheet and asks the UI to launch the matching FileKit picker. */
    data class OnDocumentSourceSelected(
        val documentId: String,
        val source: OrotezProtezImageSource,
    ) : OrotezProtezIntent
    /** The "حذف تصویر" row in the source sheet, only shown once a document has a file. */
    data class OnDocumentRemoveClicked(val documentId: String) : OrotezProtezIntent
    /** Camera/gallery returned a file for [documentId] — read, dedup-check and upload it. */
    data class OnDocumentImagePicked(val documentId: String, val file: PlatformFile) : OrotezProtezIntent
    /** Picker failed before a file was even produced: camera permission denied, cancelled read, etc. Doesn't touch [OrotezProtezUiState.documents] — only shows a transient message. */
    data class OnDocumentImagePickFailed(val message: String) : OrotezProtezIntent
    data object OnNextStepClicked : OrotezProtezIntent
    data object OnConfirmInsuredInfoClicked : OrotezProtezIntent
    data object OnSubmitDocumentsClicked : OrotezProtezIntent
    /** Goes to the previous step, or exits the feature (via [OrotezProtezEvent.NavigateBack]) from step 1. */
    data object BackToPreviousStep : OrotezProtezIntent
}

sealed interface OrotezProtezEvent {
    data object NavigateBack : OrotezProtezEvent
    /** Only a Composable can call FileKit's picker launchers — the ViewModel asks the screen to do it. */
    data class LaunchImagePicker(val documentId: String, val source: OrotezProtezImageSource) : OrotezProtezEvent
}
