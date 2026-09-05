package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.personal.DisabilityDependentPR
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

enum class DisabilityPensionStep {
    Terms,
    Dependents,
    IdentityContact,
    Workshop,
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
    val error: String? = null,
) {
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
}

sealed interface DisabilityPensionEvent {
    data class ShowToast(val message: String) : DisabilityPensionEvent
    data object NavigateToAddDependent : DisabilityPensionEvent
}
