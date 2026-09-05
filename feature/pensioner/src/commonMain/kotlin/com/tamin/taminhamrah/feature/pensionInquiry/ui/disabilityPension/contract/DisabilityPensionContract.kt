package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.personal.DisabilityDependentPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

enum class DisabilityPensionStep {
    Terms,
    Dependents,
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
}

sealed interface DisabilityPensionEvent {
    data class ShowToast(val message: String) : DisabilityPensionEvent
    data object NavigateToAddDependent : DisabilityPensionEvent
}
