package com.tamin.taminhamrah.feature.orotezprotez.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.orotezprotez.ui.OrotezProtezStep
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class OrotezProtezUiState(
    val currentStep: OrotezProtezStep = OrotezProtezStep.UserSelection,
    val isLoading: Boolean = false,
    val error: String? = null,
    val branch: OrotezProtezOptionUi? = null,
    val insuredPerson: OrotezProtezOptionUi? = null,
    val prescriptionDateLabel: String? = null,
    val prescriptionDateTimeStamp: Long? = null,
    val branchOptions: ImmutableList<OrotezProtezOptionUi> = persistentListOf(),
    val insuredPersonOptions: ImmutableList<OrotezProtezOptionUi> = persistentListOf(),
    val insuredPersonDetails: ImmutableMap<String, OrotezProtezInsuredDetailUi> = persistentMapOf(),
    val branchDetails: ImmutableMap<String, OrotezProtezBranchDetailUi> = persistentMapOf(),
    val mainInfo: OrotezProtezMainInfoUi? = null,
    val picker: OrotezProtezPicker = OrotezProtezPicker.NONE,
    val activeDocumentId: String? = null,
    val documents: ImmutableMap<String, OrotezProtezDocumentState> = persistentMapOf(),
    val documentPickError: String? = null,
    val documentValidationError: String? = null,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val hasSubmitted: Boolean = false,
    val submittedResultMessage: String? = null,
) {
    val canGoNext: Boolean
        get() = branch != null && insuredPerson != null && prescriptionDateTimeStamp != null
    val selectedInsuredDetail: OrotezProtezInsuredDetailUi?
        get() = insuredPerson?.id?.let { insuredPersonDetails[it] }

    val isAnyDocumentUploading: Boolean
        get() = documents.values.any { it is OrotezProtezDocumentState.Uploading }
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
    data class OnDocumentSourceSelected(
        val documentId: String,
        val source: OrotezProtezImageSource,
    ) : OrotezProtezIntent
    data class OnDocumentRemoveClicked(val documentId: String) : OrotezProtezIntent
    data class OnDocumentImagePicked(val documentId: String, val file: PlatformFile) : OrotezProtezIntent
    data class OnDocumentImagePickFailed(val message: String) : OrotezProtezIntent
    data object OnNextStepClicked : OrotezProtezIntent
    data object OnConfirmInsuredInfoClicked : OrotezProtezIntent
    data object OnSubmitDocumentsClicked : OrotezProtezIntent
    data object OnSubmitSuccessAcknowledged : OrotezProtezIntent
    data object BackToPreviousStep : OrotezProtezIntent
}

sealed interface OrotezProtezEvent {
    data object NavigateBack : OrotezProtezEvent
    data class LaunchImagePicker(val documentId: String, val source: OrotezProtezImageSource) : OrotezProtezEvent
}
