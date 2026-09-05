package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract

import androidx.compose.runtime.Immutable

@Immutable
data class DisabilityPensionUiState(
    val isProfileLoading: Boolean = true,
    val applicantGenderTitle: String = "",
    val applicantFullName: String = "",
    val isTermsAccepted: Boolean = false,
    val showTermsValidationError: Boolean = false,
    val showRules: Boolean = false,
    val error: String? = null,
) {
    sealed interface PartialState {
        data class ProfileLoading(val isProfileLoading: Boolean) : PartialState
        data class ApplicantInfoLoaded(val genderTitle: String, val fullName: String) : PartialState
        data class TermsAcceptedChanged(val accepted: Boolean) : PartialState
        data class TermsValidationErrorChanged(val show: Boolean) : PartialState
        data class RulesVisibilityChanged(val show: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface DisabilityPensionIntent {
    data object Init : DisabilityPensionIntent
    data class TermsAcceptedChanged(val accepted: Boolean) : DisabilityPensionIntent
    data object ShowRulesClicked : DisabilityPensionIntent
    data object DismissRules : DisabilityPensionIntent
    data object NextStepClicked : DisabilityPensionIntent
}

sealed interface DisabilityPensionEvent {
    data class ShowToast(val message: String) : DisabilityPensionEvent
}
