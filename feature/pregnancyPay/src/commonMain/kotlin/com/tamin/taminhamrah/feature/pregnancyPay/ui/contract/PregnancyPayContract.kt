package com.tamin.taminhamrah.feature.pregnancyPay.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.pregnancyPay.ui.PregnancyPayStep
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlin.math.abs

@Immutable
data class PregnancyPayUiState(
    val currentStep: PregnancyPayStep = PregnancyPayStep.Landing,
    val isLoading: Boolean = false,
    val error: String? = null,
    val picker: PregnancyPayPicker = PregnancyPayPicker.NONE,

    val mainInfo: PregnancyPayMainInfoUi? = null,
    val isMaleBlocked: Boolean = false,

    val branch: PregnancyPayOptionUi? = null,
    val branchOptions: ImmutableList<PregnancyPayOptionUi> = persistentListOf(),

    val pregnancyStatus: PregnancyPayOptionUi? = null,
    val pregnancyStatusOptions: ImmutableList<PregnancyPayOptionUi> = persistentListOf(),
    val pregnancyType: PregnancyPayOptionUi? = null,
    val pregnancyTypeOptions: ImmutableList<PregnancyPayOptionUi> = persistentListOf(),
    val requestType: PregnancyPayOptionUi? = null,
    val requestTypeOptions: ImmutableList<PregnancyPayOptionUi> = persistentListOf(),

    val restStartDateLabel: String? = null,
    val restStartDateTimeStamp: Long? = null,
    val restEndDateLabel: String? = null,
    val restEndDateTimeStamp: Long? = null,
    val babyBirthDateLabel: String? = null,
    val babyBirthDateTimeStamp: Long? = null,

    val doctorName: String = "",
    val doctorCode: String = "",
    val childNationalCode: String = "",
    val childNationalCode2: String = "",
    val childNationalCode3: String = "",

    val doctorAndRequestError: String? = null,

    val activeDocumentId: String? = null,
    val documents: ImmutableMap<String, PregnancyPayDocumentState> = persistentMapOf(),
    val documentPickError: String? = null,
    val documentValidationError: String? = null,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val hasSubmitted: Boolean = false,
    val submittedResultMessage: String? = null,

    val estimateRestStartDateLabel: String? = null,
    val estimateRestStartDateTimeStamp: Long? = null,
    val estimateRestEndDateLabel: String? = null,
    val estimateRestEndDateTimeStamp: Long? = null,
    val estimateResult: PregnancyPayEstimateResultUi? = null,
    val isCalculatingEstimate: Boolean = false,
    val estimateError: String? = null,
) {
    val restDaysCount: Long?
        get() {
            val start = restStartDateTimeStamp ?: return null
            val end = restEndDateTimeStamp ?: return null
            if (end <= start) return null
            return (end - start) / MILLIS_PER_DAY
        }

    val canGoNextFromBranchAndRest: Boolean
        get() = branch != null && restDaysCount != null

    val estimateRestDaysCount: Long?
        get() {
            val start = estimateRestStartDateTimeStamp ?: return null
            val end = estimateRestEndDateTimeStamp ?: return null
            if (end <= start) return null
            return (end - start) / MILLIS_PER_DAY
        }

    val canCalculateEstimate: Boolean
        get() = estimateRestDaysCount != null && !isCalculatingEstimate

    val requiredChildNationalCodeCount: Int
        get() = when (pregnancyType?.id) {
            PREGNANCY_TYPE_TWINS -> 2
            PREGNANCY_TYPE_TRIPLET_OR_MORE -> 3
            else -> 1
        }

    private val hasAllChildNationalCodes: Boolean
        get() {
            if (childNationalCode.length != CHILD_NATIONAL_CODE_LENGTH) return false
            if (requiredChildNationalCodeCount >= 2 && childNationalCode2.length != CHILD_NATIONAL_CODE_LENGTH) return false
            if (requiredChildNationalCodeCount >= 3 && childNationalCode3.length != CHILD_NATIONAL_CODE_LENGTH) return false
            return true
        }

    val hasDuplicateChildNationalCode: Boolean
        get() {
            val codes = buildList {
                add(childNationalCode)
                if (requiredChildNationalCodeCount >= 2) add(childNationalCode2)
                if (requiredChildNationalCodeCount >= 3) add(childNationalCode3)
            }.filter { it.isNotBlank() }
            return codes.size != codes.toSet().size
        }

    val canGoNextFromPregnancyAndNewborn: Boolean
        get() = babyBirthDateTimeStamp != null &&
            pregnancyStatus != null &&
            pregnancyType != null &&
            hasAllChildNationalCodes &&
            !hasDuplicateChildNationalCode

    val canGoNextFromDoctorAndRequest: Boolean
        get() = requestType != null && doctorName.isNotBlank() && doctorCode.length == DOCTOR_CODE_LENGTH

    val babyBirthToRestStartDiffDays: Long?
        get() {
            val start = restStartDateTimeStamp ?: return null
            val birth = babyBirthDateTimeStamp ?: return null
            return abs(birth - start) / MILLIS_PER_DAY
        }

    val isAnyDocumentUploading: Boolean
        get() = documents.values.any { it is PregnancyPayDocumentState.Uploading }

    val requiredDocumentsUploadedCount: Int
        get() = PregnancyPayRequiredDocumentIds.count { documents[it] is PregnancyPayDocumentState.Uploaded }

    val canSubmitDocuments: Boolean
        get() = requiredDocumentsUploadedCount == PregnancyPayRequiredDocumentIds.size &&
            !isAnyDocumentUploading && !isSubmitting && !hasSubmitted

    val documentSubmissionPayload: List<PregnancyPayDocumentSubmissionUi>
        get() = documents.entries.mapNotNull { (documentId, state) ->
            (state as? PregnancyPayDocumentState.Uploaded)?.let {
                PregnancyPayDocumentSubmissionUi(documentFile = it.guid, documentType = documentId)
            }
        }

    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
    }

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState
        data class MainInfoLoaded(
            val mainInfo: PregnancyPayMainInfoUi?,
            val isMaleBlocked: Boolean,
            val branch: PregnancyPayOptionUi?,
            val branchOptions: ImmutableList<PregnancyPayOptionUi>,
        ) : PartialState
        data class OptionsLoaded(
            val pregnancyStatusOptions: ImmutableList<PregnancyPayOptionUi>,
            val pregnancyType: PregnancyPayOptionUi?,
            val pregnancyStatus: PregnancyPayOptionUi?,
            val pregnancyTypeOptions: ImmutableList<PregnancyPayOptionUi>,
            val requestTypeOptions: ImmutableList<PregnancyPayOptionUi>,
        ) : PartialState
        data class PickerChanged(val picker: PregnancyPayPicker) : PartialState
        data class DocumentSourceRequested(val documentId: String) : PartialState
        data class BranchSelected(val branch: PregnancyPayOptionUi) : PartialState
        data class PregnancyStatusSelected(val option: PregnancyPayOptionUi) : PartialState
        data class PregnancyTypeSelected(val option: PregnancyPayOptionUi) : PartialState
        data class RequestTypeSelected(val option: PregnancyPayOptionUi) : PartialState
        data class RestStartDateSelected(val millis: Long, val label: String) : PartialState
        data class RestEndDateSelected(val millis: Long, val label: String) : PartialState
        data class BabyBirthDateSelected(val millis: Long, val label: String) : PartialState
        data class DoctorNameChanged(val value: String) : PartialState
        data class DoctorCodeChanged(val value: String) : PartialState
        data class ChildNationalCodeChanged(val slot: Int, val value: String) : PartialState
        data class StepChanged(val step: PregnancyPayStep) : PartialState
        data class DoctorAndRequestValidationFailed(val message: String) : PartialState
        data class DocumentStateChanged(val documentId: String, val state: PregnancyPayDocumentState) : PartialState
        data class DocumentPickRejected(val message: String) : PartialState
        data class DocumentValidationFailed(val message: String) : PartialState
        data object DocumentsReadyForSubmission : PartialState
        data class Submitting(val isSubmitting: Boolean) : PartialState
        data class SubmitSucceeded(val resultMessage: String?) : PartialState
        data class SubmitFailed(val message: String) : PartialState
        data class EstimateRestStartDateSelected(val millis: Long, val label: String) : PartialState
        data class EstimateRestEndDateSelected(val millis: Long, val label: String) : PartialState
        data class EstimateCalculating(val isCalculating: Boolean) : PartialState
        data class EstimateCalculated(val result: PregnancyPayEstimateResultUi) : PartialState
        data class EstimateFailed(val message: String) : PartialState
    }
}

sealed interface PregnancyPayIntent {
    data object LoadInitialData : PregnancyPayIntent
    data object OnGenderBlockAcknowledged : PregnancyPayIntent
    data object OnLandingStartClicked : PregnancyPayIntent
    data object OnCalculateEstimateClicked : PregnancyPayIntent
    data class OnPickerRequested(val picker: PregnancyPayPicker) : PregnancyPayIntent
    data object OnPickerDismissed : PregnancyPayIntent
    data class OnBranchPicked(val option: PregnancyPayOptionUi) : PregnancyPayIntent
    data class OnPregnancyStatusPicked(val option: PregnancyPayOptionUi) : PregnancyPayIntent
    data class OnPregnancyTypePicked(val option: PregnancyPayOptionUi) : PregnancyPayIntent
    data class OnRequestTypePicked(val option: PregnancyPayOptionUi) : PregnancyPayIntent
    data class OnRestStartDatePicked(val millis: Long, val label: String) : PregnancyPayIntent
    data class OnRestEndDatePicked(val millis: Long, val label: String) : PregnancyPayIntent
    data class OnBabyBirthDatePicked(val millis: Long, val label: String) : PregnancyPayIntent
    data class OnDoctorNameChanged(val value: String) : PregnancyPayIntent
    data class OnDoctorCodeChanged(val value: String) : PregnancyPayIntent
    data class OnChildNationalCodeChanged(val slot: Int, val value: String) : PregnancyPayIntent
    data object OnNextFromBranchAndRestClicked : PregnancyPayIntent
    data object OnNextFromPregnancyAndNewbornClicked : PregnancyPayIntent
    data object OnNextFromDoctorAndRequestClicked : PregnancyPayIntent
    data class OnDocumentCardClicked(val documentId: String) : PregnancyPayIntent
    data class OnDocumentSourceSelected(
        val documentId: String,
        val source: PregnancyPayImageSource,
    ) : PregnancyPayIntent
    data class OnDocumentRemoveClicked(val documentId: String) : PregnancyPayIntent
    data class OnDocumentImagePicked(val documentId: String, val file: PlatformFile) : PregnancyPayIntent
    data class OnDocumentImagePickFailed(val message: String) : PregnancyPayIntent
    data object OnSubmitDocumentsClicked : PregnancyPayIntent
    data object OnSubmitSuccessAcknowledged : PregnancyPayIntent
    data class OnEstimateRestStartDatePicked(val millis: Long, val label: String) : PregnancyPayIntent
    data class OnEstimateRestEndDatePicked(val millis: Long, val label: String) : PregnancyPayIntent
    data object OnCalculateEstimateSubmitClicked : PregnancyPayIntent
    data object BackToPreviousStep : PregnancyPayIntent
}

sealed interface PregnancyPayEvent {
    data object NavigateBack : PregnancyPayEvent
    data class LaunchImagePicker(val documentId: String, val source: PregnancyPayImageSource) : PregnancyPayEvent
}
