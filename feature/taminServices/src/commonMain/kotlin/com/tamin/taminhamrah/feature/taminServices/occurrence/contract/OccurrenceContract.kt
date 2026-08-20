package com.tamin.taminhamrah.feature.taminServices.occurrence.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.taminServices.occurrence.model.OccurrenceDocTypePR
import com.tamin.taminhamrah.feature.taminServices.occurrence.model.OccurrencePersonalInfoPR
import com.tamin.taminhamrah.feature.taminServices.occurrence.model.UserInfoPR
import com.tamin.taminhamrah.feature.taminServices.occurrence.model.WorkshopItemPR
import com.tamin.taminhamrah.model.occurrence.OccurrenceUploadedDocDN
import com.tamin.taminhamrah.util.ValidationUtils

enum class OccurrenceStep {
    PERSON_INFO,
    WORKSHOP_INFO,
    JOB_DETAILS,
    WORK_HOURS,
    ACCIDENT_DETAILS,
    DOCUMENT_SUBMIT,
}

/** Tags a fatal load failure to the step it blocks, so only that step's [com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceErrorWrapper] shows it. */
enum class ErrorSource {
    GENERAL,
    USER_INFO,
    WORKSHOPS,
    INSURED_RELATION,
}

// ─────────────────────────────────────────────────────────────────────────────
// Per-Step State Data Classes
// ─────────────────────────────────────────────────────────────────────────────

data class PersonInfoStepState(
    val personalInfo: OccurrencePersonalInfoPR? = null,
    val userInfo: UserInfoPR? = null,
    val birthDate: String = "",
    val birthDateTimestamp: Long? = null,
)

data class WorkshopStepState(
    val workshops: List<WorkshopItemPR> = emptyList(),
    val selectedWorkshop: WorkshopItemPR? = null,
    val employerName: String = "",
    val employerPhone: String = "",
    val workshopAddress: String = "",
    val workshopPostalCode: String = "",
    val workshopPhone: String = "",
    val isWorkshopSpecLoading: Boolean = false,
)

data class JobDetailsStepState(
    val fullName: String = "",
    val nationality: String = "",
    val nationalityCode: String = "",
    val gender: String = "",
    val insuranceType: String = "",
    val insuranceTypeCode: String = "",
    val branchCode: String = "",
    val branchName: String = "",
    val employmentDate: String = "",
    val employmentDateTimestamp: Long? = null,
    val maritalStatus: String = "",
    val jobTitle: String = "",
    val workLocation: String = "",
)

data class WorkHoursStepState(
    val transportation: String = "",
    val workStartTime: String = "",
    val workEndTime: String = "",
    val homeAddress: String = "",
    val homePhone: String = "",
    val homePostalCode: String = "",
)

data class AccidentStepState(
    val accidentDate: String = "",
    val accidentDateTimestamp: Long? = null,
    val accidentTime: String = "",
    val accidentOutcomeId: String = "",
    val accidentOutcomeTitle: String = "",
    val exactLocation: String = "",
    val description: String = "",
)

data class DocumentSubmitStepState(
    val docTypes: List<OccurrenceDocTypePR> = emptyList(),
    val uploadedDocuments: List<OccurrenceUploadedDocDN> = emptyList(),
    val isUploadingDoc: Boolean = false,
    val uploadingTypeName: String = "",
    val uploadingFileName: String = "",
)

/**
 * Visibility of every bottom sheet / date-picker / dialog across the Occurrence flow, held in the
 * ViewModel instead of local `remember` so a configuration change (e.g. a theme switch, which
 * recreates the Activity) doesn't silently close whatever the user had open.
 */
data class OccurrenceDialogState(
    val showWarningSheet: Boolean = true,
    val showExitConfirmation: Boolean = false,
    val successTrackingCode: String? = null,
    val showBirthDatePicker: Boolean = false,
    val showWorkshopSheet: Boolean = false,
    val showMaritalSheet: Boolean = false,
    val showEmploymentDatePicker: Boolean = false,
    val showWorkStartTimePicker: Boolean = false,
    val showWorkEndTimePicker: Boolean = false,
    val showAccidentDatePicker: Boolean = false,
    val showAccidentTimePicker: Boolean = false,
    val showAccidentOutcomeSheet: Boolean = false,
    val pendingDocType: OccurrenceDocTypePR? = null,
    val showDocTypeSheet: Boolean = false,
    val showDocumentSourceSheet: Boolean = false,
)

// ─────────────────────────────────────────────────────────────────────────────
// Aggregate UI State
// ─────────────────────────────────────────────────────────────────────────────

@Immutable
data class OccurrenceUiState(
    val currentStep: OccurrenceStep = OccurrenceStep.PERSON_INFO,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val personInfo: PersonInfoStepState = PersonInfoStepState(),
    val workshop: WorkshopStepState = WorkshopStepState(),
    val jobDetails: JobDetailsStepState = JobDetailsStepState(),
    val workHours: WorkHoursStepState = WorkHoursStepState(),
    val accident: AccidentStepState = AccidentStepState(),
    val documentSubmit: DocumentSubmitStepState = DocumentSubmitStepState(),
    val errors: Map<ErrorSource, String> = emptyMap(),
    val dialogs: OccurrenceDialogState = OccurrenceDialogState(),
) {
    val isStep1Valid: Boolean get() = personInfo.birthDate.isNotBlank()
    val isStep2Valid: Boolean
        get() = workshop.selectedWorkshop != null &&
            workshop.employerName.isNotBlank() &&
            ValidationUtils.isPhoneNumberValid(workshop.employerPhone) &&
            workshop.workshopAddress.isNotBlank() &&
            ValidationUtils.isPhoneNumberValid(workshop.workshopPhone) &&
            workshop.workshopPostalCode.isNotBlank() &&
            ValidationUtils.isPostcodeValid(workshop.workshopPostalCode)
    val isStep3Valid: Boolean
        get() = jobDetails.employmentDate.isNotBlank() && jobDetails.maritalStatus.isNotBlank() &&
            jobDetails.jobTitle.isNotBlank() && jobDetails.workLocation.isNotBlank()
    val isStep4Valid: Boolean
        get() = workHours.transportation.isNotBlank() &&
            workHours.workStartTime.isNotBlank() &&
            workHours.workEndTime.isNotBlank() &&
            workHours.homeAddress.isNotBlank() &&
            ValidationUtils.isPhoneNumberValid(workHours.homePhone) &&
            workHours.homePostalCode.isNotBlank() &&
            ValidationUtils.isPostcodeValid(workHours.homePostalCode)
    val isStep5Valid: Boolean
        get() = accident.accidentDate.isNotBlank() && accident.accidentTime.isNotBlank() &&
            accident.accidentOutcomeId.isNotBlank() && accident.exactLocation.isNotBlank() &&
            accident.description.isNotBlank()
    val isStep6Valid: Boolean get() = documentSubmit.uploadedDocuments.size >= 2

    val stepNumber: Int get() = currentStep.ordinal + 1

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Submitting(val isSubmitting: Boolean) : PartialState
        data class Error(val message: String, val source: ErrorSource = ErrorSource.GENERAL) : PartialState
        data object GoToNextStep : PartialState
        data object GoToPreviousStep : PartialState
        data class PersonInfoUpdated(val personInfo: PersonInfoStepState) : PartialState
        data class UserInfoUpdated(val userInfo: UserInfoPR?) : PartialState
        data class WorkshopUpdated(val workshop: WorkshopStepState) : PartialState
        data class JobDetailsUpdated(val jobDetails: JobDetailsStepState) : PartialState
        data class WorkHoursUpdated(val workHours: WorkHoursStepState) : PartialState
        data class AccidentUpdated(val accident: AccidentStepState) : PartialState
        data class DocumentSubmitUpdated(val documentSubmit: DocumentSubmitStepState) : PartialState
        data class DialogsUpdated(val dialogs: OccurrenceDialogState) : PartialState
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Intents
// ─────────────────────────────────────────────────────────────────────────────

sealed interface OccurrenceIntent {
    data object LoadInitialData : OccurrenceIntent
    data object GoToNextStep : OccurrenceIntent
    data object GoToPreviousStep : OccurrenceIntent
    data class UpdatePersonInfo(val personInfo: PersonInfoStepState) : OccurrenceIntent
    data class SelectWorkshop(val workshop: WorkshopItemPR) : OccurrenceIntent
    data class UpdateWorkshop(val workshop: WorkshopStepState) : OccurrenceIntent
    data class UpdateJobDetails(val jobDetails: JobDetailsStepState) : OccurrenceIntent
    data class UpdateWorkHours(val workHours: WorkHoursStepState) : OccurrenceIntent
    data class UpdateAccident(val accident: AccidentStepState) : OccurrenceIntent
    data class UploadDocument(
        val typeId: Int,
        val typeName: String,
        val fileName: String,
        val fileBytes: ByteArray,
    ) : OccurrenceIntent

    data class RemoveDocument(val guid: String) : OccurrenceIntent
    data object SubmitOccurrence : OccurrenceIntent
    data class UpdateDialogs(val dialogs: OccurrenceDialogState) : OccurrenceIntent
}

// ─────────────────────────────────────────────────────────────────────────────
// Events
// ─────────────────────────────────────────────────────────────────────────────

sealed interface OccurrenceEvent {
    data class ShowToast(val message: String) : OccurrenceEvent
    data object NavigateBack : OccurrenceEvent
}
