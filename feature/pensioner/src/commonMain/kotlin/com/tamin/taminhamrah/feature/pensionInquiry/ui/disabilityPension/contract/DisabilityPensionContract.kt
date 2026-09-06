package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.personal.DisabilityDependentPR
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoPR
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.RegisteredMedicalCommissionPR
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf

enum class DisabilityPensionStep {
    Terms,
    Dependents,
    IdentityContact,
    Workshop,
    CommissionRecord,
    Documents,
    Summary,
}

enum class LandlinePhoneError {
    Blank,
    InvalidPrefix,
    InvalidLength,
}

enum class AddressError {
    Blank,
    TooShort,
    InvalidCharacters,
}

@Immutable
data class DisabilityPensionUiState(
    val currentStep: DisabilityPensionStep = DisabilityPensionStep.Terms,
    val isProfileLoading: Boolean = true,
    val applicantGenderTitle: String = "",
    val applicantFullName: String = "",
    val isTermsAccepted: Boolean = false,
    val showTermsValidationError: Boolean = false,
    val showRules: Boolean = false,
    val isDependentsLoading: Boolean = false,
    val dependents: ImmutableList<DisabilityDependentPR> = persistentListOf(),
    val expandedDependentIds: ImmutableSet<String> = persistentSetOf(),
    val isDependentsListConfirmed: Boolean = false,
    val showDependentsConfirmationError: Boolean = false,
    val showRefreshConfirmDialog: Boolean = false,
    val isRefreshingDependents: Boolean = false,
    val identityInfo: DisabilityPersonalInfoPR? = null,
    val identityAgeYears: String = "",
    val isIdentityDetailsExpanded: Boolean = false,
    val landlinePhone: String = "",
    val landlinePhoneError: LandlinePhoneError? = null,
    val address: String = "",
    val addressError: AddressError? = null,
    val isIdentityConfirmed: Boolean = false,
    val showIdentityConfirmationError: Boolean = false,
    val workshopName: String = "",
    val workshopNameError: Boolean = false,
    val activityType: String = "",
    val employerName: String = "",
    val workshopAddress: String = "",
    val workshopAddressError: Boolean = false,
    val isWorkshopConfirmed: Boolean = false,
    val showWorkshopConfirmationError: Boolean = false,
    val isInsuranceRecordLoading: Boolean = false,
    val insuranceRecordDays: String = "",
    val insuranceRecordMonths: String = "",
    val insuranceRecordYears: String = "",
    val insuranceRecordTotalDays: String = "",
    val hasCommissionObjection: Boolean? = null,
    val showCommissionValidationError: Boolean = false,
    val showRegisteredRequestsSheet: Boolean = false,
    val isRegisteredRequestsLoading: Boolean = false,
    val registeredRequests: ImmutableList<RegisteredMedicalCommissionPR> = persistentListOf(),
    val showMedicalCommissionPdfViewer: Boolean = false,
    val medicalCommissionPdf: PdfDownloadPR? = null,
    val medicalCommissionPdfDownloadFailed: Boolean = false,
    val documents: ImmutableMap<String, DisabilityDocumentState> = persistentMapOf(),
    val activeDocumentId: String? = null,
    val showDocumentSourceSheet: Boolean = false,
    val documentPickError: String? = null,
    val showDocumentsConfirmDialog: Boolean = false,
    val isFinalConfirmed: Boolean = false,
    val showFinalConfirmationError: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitTrackingCode: String? = null,
    val showExitConfirmDialog: Boolean = false,
    val isEditingFromSummary: Boolean = false,
    val error: String? = null,
) {
    val isAnyDocumentUploading: Boolean
        get() = documents.values.any { it is DisabilityDocumentState.Uploading }

    sealed interface PartialState {
        data class ProfileLoading(val isProfileLoading: Boolean) : PartialState
        data class ApplicantInfoLoaded(val genderTitle: String, val fullName: String) : PartialState
        data class TermsAcceptedChanged(val accepted: Boolean) : PartialState
        data class TermsValidationErrorChanged(val show: Boolean) : PartialState
        data class RulesVisibilityChanged(val show: Boolean) : PartialState
        data class StepChanged(val step: DisabilityPensionStep) : PartialState
        data class DependentsLoading(val isLoading: Boolean) : PartialState
        data class DependentsLoaded(val dependents: ImmutableList<DisabilityDependentPR>) : PartialState
        data class DependentCardToggled(val id: String) : PartialState
        data class DependentsConfirmedChanged(val accepted: Boolean) : PartialState
        data class DependentsConfirmationErrorChanged(val show: Boolean) : PartialState
        data class RefreshConfirmDialogVisibilityChanged(val show: Boolean) : PartialState
        data class RefreshingDependentsChanged(val isRefreshing: Boolean) : PartialState
        data class IdentityLoaded(val info: DisabilityPersonalInfoPR) : PartialState
        data class IdentityAgeLoaded(val years: String) : PartialState
        data class IdentityDetailsExpandedChanged(val expanded: Boolean) : PartialState
        data class LandlinePhoneChanged(val value: String, val error: LandlinePhoneError?) : PartialState
        data class AddressChanged(val value: String, val error: AddressError?) : PartialState
        data class IdentityConfirmedChanged(val accepted: Boolean) : PartialState
        data class IdentityConfirmationErrorChanged(val show: Boolean) : PartialState
        data class WorkshopNameChanged(val value: String, val error: Boolean) : PartialState
        data class ActivityTypeChanged(val value: String) : PartialState
        data class EmployerNameChanged(val value: String) : PartialState
        data class WorkshopAddressChanged(val value: String, val error: Boolean) : PartialState
        data class WorkshopConfirmedChanged(val accepted: Boolean) : PartialState
        data class WorkshopConfirmationErrorChanged(val show: Boolean) : PartialState
        data class InsuranceRecordLoading(val isLoading: Boolean) : PartialState
        data class InsuranceRecordLoaded(
            val days: String,
            val months: String,
            val years: String,
            val totalDays: String,
        ) : PartialState
        data class CommissionObjectionChanged(val hasObjection: Boolean?) : PartialState
        data class CommissionValidationErrorChanged(val show: Boolean) : PartialState
        data class RegisteredRequestsSheetVisibilityChanged(val show: Boolean) : PartialState
        data class RegisteredRequestsLoading(val isLoading: Boolean) : PartialState
        data class RegisteredRequestsLoaded(val requests: ImmutableList<RegisteredMedicalCommissionPR>) : PartialState
        data class MedicalCommissionPdfViewerVisibilityChanged(val show: Boolean) : PartialState
        data class MedicalCommissionPdfChanged(val pdf: PdfDownloadPR?) : PartialState
        data object MedicalCommissionPdfDownloadFailed : PartialState
        data class DocumentSourceRequested(val documentId: String) : PartialState
        data object DocumentSourceSheetDismissed : PartialState
        data class DocumentStateChanged(val documentId: String, val state: DisabilityDocumentState) : PartialState
        data class DocumentPickRejected(val message: String) : PartialState
        data class DocumentsConfirmDialogVisibilityChanged(val show: Boolean) : PartialState
        data class FinalConfirmedChanged(val accepted: Boolean) : PartialState
        data class FinalConfirmationErrorChanged(val show: Boolean) : PartialState
        data class SubmittingChanged(val isSubmitting: Boolean) : PartialState
        data class SubmitSucceeded(val trackingCode: String) : PartialState
        data class ExitConfirmDialogVisibilityChanged(val show: Boolean) : PartialState
        data class EditingFromSummaryChanged(val editing: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface DisabilityPensionIntent {
    data object Init : DisabilityPensionIntent
    data class TermsAcceptedChanged(val accepted: Boolean) : DisabilityPensionIntent
    data object ShowRulesClicked : DisabilityPensionIntent
    data object DismissRules : DisabilityPensionIntent
    data object NextStepClicked : DisabilityPensionIntent
    data object PreviousStepClicked : DisabilityPensionIntent
    data class DependentCardToggled(val id: String) : DisabilityPensionIntent
    data class DependentsListConfirmedChanged(val accepted: Boolean) : DisabilityPensionIntent
    data object AddDependentClicked : DisabilityPensionIntent
    data object RefreshDependentsClicked : DisabilityPensionIntent
    data object ConfirmRefreshDependents : DisabilityPensionIntent
    data object DismissRefreshConfirm : DisabilityPensionIntent
    data object DependentsResumed : DisabilityPensionIntent
    data object ToggleIdentityDetails : DisabilityPensionIntent
    data class LandlinePhoneChanged(val value: String) : DisabilityPensionIntent
    data class AddressChanged(val value: String) : DisabilityPensionIntent
    data class IdentityConfirmedChanged(val accepted: Boolean) : DisabilityPensionIntent
    data class WorkshopNameChanged(val value: String) : DisabilityPensionIntent
    data class ActivityTypeChanged(val value: String) : DisabilityPensionIntent
    data class EmployerNameChanged(val value: String) : DisabilityPensionIntent
    data class WorkshopAddressChanged(val value: String) : DisabilityPensionIntent
    data class WorkshopConfirmedChanged(val accepted: Boolean) : DisabilityPensionIntent
    data class CommissionObjectionChanged(val hasObjection: Boolean) : DisabilityPensionIntent
    data object HistoryObjectionLinkClicked : DisabilityPensionIntent
    data object ShowRegisteredRequestsClicked : DisabilityPensionIntent
    data object DismissRegisteredRequestsSheet : DisabilityPensionIntent
    data object ShowMedicalCommissionPdfViewerClicked : DisabilityPensionIntent
    data object DownloadMedicalCommissionPdfClicked : DisabilityPensionIntent
    data object DismissMedicalCommissionPdfViewer : DisabilityPensionIntent
    data class DocumentCardClicked(val documentId: String) : DisabilityPensionIntent
    data class DocumentSourceSelected(
        val documentId: String,
        val source: DisabilityDocumentImageSource,
    ) : DisabilityPensionIntent
    data class DocumentRemoveClicked(val documentId: String) : DisabilityPensionIntent
    data class DocumentImagePicked(val documentId: String, val file: PlatformFile) : DisabilityPensionIntent
    data class DocumentImagePickFailed(val message: String) : DisabilityPensionIntent
    data object DismissDocumentSourceSheet : DisabilityPensionIntent
    data object ConfirmDocumentsSubmission : DisabilityPensionIntent
    data object DismissDocumentsConfirmDialog : DisabilityPensionIntent
    data class FinalConfirmedChanged(val accepted: Boolean) : DisabilityPensionIntent
    data class EditSummarySectionClicked(val step: DisabilityPensionStep) : DisabilityPensionIntent
    data object SubmitSuccessAcknowledged : DisabilityPensionIntent
    data object CloseClicked : DisabilityPensionIntent
    data object DismissExitConfirmDialog : DisabilityPensionIntent
    data object ConfirmExitClicked : DisabilityPensionIntent
}

sealed interface DisabilityPensionEvent {
    data class ShowToast(val message: String) : DisabilityPensionEvent
    data object NavigateToAddDependent : DisabilityPensionEvent
    data object NavigateBack : DisabilityPensionEvent
    data class LaunchImagePicker(
        val documentId: String,
        val source: DisabilityDocumentImageSource,
    ) : DisabilityPensionEvent
}
