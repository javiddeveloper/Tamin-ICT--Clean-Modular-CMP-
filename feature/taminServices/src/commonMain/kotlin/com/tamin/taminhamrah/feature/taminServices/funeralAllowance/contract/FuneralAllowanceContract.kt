package com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.model.DeceasedValidationPR
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.model.FuneralAllowanceInfoPR

enum class FuneralAllowanceStep {
    APPLICANT_INFO,
    DECEASED_INFO,
}

@Immutable
data class FuneralAllowanceUiState(
    val currentStep: FuneralAllowanceStep = FuneralAllowanceStep.APPLICANT_INFO,
    val isLoading: Boolean = false,
    /** True while the pre-flight bank-account check is running. */
    val isCheckingBankAccount: Boolean = false,
    /**
     * The user has no registered bank account, so the whole request flow is
     * blocked behind the no-bank-account dialog until they register one.
     */
    val showNoBankAccountDialog: Boolean = false,
    val isValidatingDeceased: Boolean = false,
    val isSubmitting: Boolean = false,
    val isConfirmingCorrection: Boolean = false,
    val info: FuneralAllowanceInfoPR? = null,
    /** Deceased national id the user typed in the eligibility-inquiry field. */
    val deceasedNationalCode: String = "",
    val deceasedNationalCodeError: String? = null,
    /** Populated by a successful eligibility inquiry; only [DeceasedValidationPR.isEligible] unlocks submit. */
    val deceasedValidation: DeceasedValidationPR? = null,
    val errorMessage: String? = null,
) {
    val hasInfo: Boolean get() = info != null

    /** The bank-account-issue branch replaces the whole inquiry flow with the "fix account" action. */
    val showBankAccountIssueFlow: Boolean get() = info?.hasBankAccountIssue == true

    val canSubmitRequest: Boolean
        get() = deceasedValidation?.isEligible == true && !isSubmitting

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data object ClearError : PartialState

        data class CheckingBankAccount(val inProgress: Boolean) : PartialState

        /** Bank-account check came back empty — block the flow behind the dialog. */
        data object NoBankAccount : PartialState
        data object DismissNoBankAccountDialog : PartialState

        data class InfoLoaded(val info: FuneralAllowanceInfoPR) : PartialState

        data class DeceasedNationalCodeChanged(val value: String) : PartialState
        data class DeceasedNationalCodeError(val message: String?) : PartialState

        data class ValidatingDeceased(val inProgress: Boolean) : PartialState
        data class DeceasedValidated(val validation: DeceasedValidationPR) : PartialState
        data object DeceasedValidationCleared : PartialState

        data class Submitting(val inProgress: Boolean) : PartialState
        data class ConfirmingCorrection(val inProgress: Boolean) : PartialState

        data object GoToNextStep : PartialState
        data object GoToPreviousStep : PartialState

        data class Error(val message: String) : PartialState
    }
}

sealed interface FuneralAllowanceIntent {
    data object LoadInfo : FuneralAllowanceIntent
    data object Retry : FuneralAllowanceIntent

    /** "ثبت شماره حساب بانکی" — leave for the bank-account screen. */
    data object NavigateToBankAccount : FuneralAllowanceIntent

    /** "بعداً" — dismiss the no-bank-account dialog and leave the flow. */
    data object DismissNoBankAccountDialog : FuneralAllowanceIntent

    data class DeceasedNationalCodeChanged(val value: String) : FuneralAllowanceIntent

    /** "استعلام شرایط" — check the deceased's eligibility. */
    data object ValidateDeceased : FuneralAllowanceIntent

    data object GoToNextStep : FuneralAllowanceIntent
    data object GoToPreviousStep : FuneralAllowanceIntent

    /** "ثبت درخواست" — register the funeral-allowance request. */
    data object SubmitRequest : FuneralAllowanceIntent

    /** "مشکل شماره حساب خود را برطرف نموده‌ام" — re-submit the rejected request. */
    data object ConfirmAccountCorrection : FuneralAllowanceIntent
}

sealed interface FuneralAllowanceEvent {
    /** A non-eligibility / informational backend message shown in a dialog. */
    data class ShowInfoMessage(val message: String) : FuneralAllowanceEvent

    /** Success message shown before returning to the previous screen. */
    data class ShowSuccessMessage(val message: String) : FuneralAllowanceEvent

    data class ShowErrorToast(val message: String) : FuneralAllowanceEvent

    data object NavigateBack : FuneralAllowanceEvent

    /** Send the user to the bank-account screen to register an account. */
    data object NavigateToBankAccount : FuneralAllowanceEvent
}
