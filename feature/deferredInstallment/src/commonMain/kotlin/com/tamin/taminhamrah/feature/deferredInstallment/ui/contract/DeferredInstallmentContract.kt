package com.tamin.taminhamrah.feature.deferredInstallment.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.deferredInstallment.ui.DeferredInstallmentStep
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

const val GUARANTEE_FOR_OTHERS = "1"
const val GUARANTEE_FOR_SELF = "0"
const val MIN_INSTALLMENT_AMOUNT = 500_000L
const val MIN_INSTALLMENT_COUNT = 12
const val MAX_INSTALLMENT_COUNT = 240

@Immutable
data class DeferredInstallmentOptionUi(
    val id: String,
    val label: String,
    val subtitle: String? = null,
)

enum class DeferredInstallmentPicker {
    NONE,
    GUARANTEE,
    BANK,
    BIRTH_DATE,
}

@Immutable
data class DeferredInstallmentUiState(
    val currentStep: DeferredInstallmentStep = DeferredInstallmentStep.CertificateRequest,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isLoadingBanks: Boolean = false,
    val error: String? = null,
    val submitError: String? = null,
    val picker: DeferredInstallmentPicker = DeferredInstallmentPicker.NONE,
    val pensionerId: String = "",
    val pensionerIds: ImmutableList<String> = persistentListOf(),
    val guaranteeType: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val nationalId: String = "",
    val birthDateLabel: String? = null,
    val birthDateIso: String? = null,
    val bank: DeferredInstallmentOptionUi? = null,
    val bankOptions: ImmutableList<DeferredInstallmentOptionUi> = persistentListOf(),
    val bankQuery: String = "",
    val branchName: String = "",
    val installmentAmountDigits: String = "",
    val installmentCountDigits: String = "",
    val guaranteeAmountDigits: String = "",
    val showConfirmDialog: Boolean = false,
    val submittedRefCode: String? = null,
    val hasSubmitted: Boolean = false,
    val fieldError: DeferredInstallmentFieldError? = null,
) {
    val isGuaranteeForOthers: Boolean get() = guaranteeType == GUARANTEE_FOR_OTHERS

    val installmentAmount: Long? get() = installmentAmountDigits.toLongOrNull()
    val installmentCount: Int? get() = installmentCountDigits.toIntOrNull()
    val repaymentAmount: Long?
        get() {
            val amount = installmentAmount ?: return null
            val count = installmentCount ?: return null
            return amount * count
        }
    val minimumGuaranteeAmount: Long?
        get() = repaymentAmount?.let { it + (it / 5) }
    val guaranteeAmount: Long? get() = guaranteeAmountDigits.toLongOrNull()

    val canGoNext: Boolean
        get() {
            if (pensionerId.isBlank() || guaranteeType.isNullOrBlank()) return false
            if (!isGuaranteeForOthers) return true
            return firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                nationalId.length == 10 &&
                !birthDateIso.isNullOrBlank()
        }

    val canSubmit: Boolean
        get() {
            val amount = installmentAmount ?: return false
            val count = installmentCount ?: return false
            val repayment = repaymentAmount ?: return false
            val guarantee = guaranteeAmount ?: return false
            return bank != null &&
                branchName.isNotBlank() &&
                amount >= MIN_INSTALLMENT_AMOUNT &&
                count in MIN_INSTALLMENT_COUNT..MAX_INSTALLMENT_COUNT &&
                guarantee >= (minimumGuaranteeAmount ?: repayment)
        }

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState
        data class PensionerLoaded(val ids: ImmutableList<String>, val selectedId: String) : PartialState
        data class PickerChanged(val picker: DeferredInstallmentPicker) : PartialState
        data class GuaranteeSelected(val type: String) : PartialState
        data class FirstNameChanged(val value: String) : PartialState
        data class LastNameChanged(val value: String) : PartialState
        data class NationalIdChanged(val value: String) : PartialState
        data class BirthDateSelected(val label: String, val iso: String) : PartialState
        data class StepChanged(val step: DeferredInstallmentStep) : PartialState
        data class BanksLoading(val isLoading: Boolean) : PartialState
        data class BanksLoaded(val options: ImmutableList<DeferredInstallmentOptionUi>, val query: String) : PartialState
        data class BankSelected(val bank: DeferredInstallmentOptionUi) : PartialState
        data class BranchChanged(val value: String) : PartialState
        data class InstallmentAmountChanged(val digits: String) : PartialState
        data class InstallmentCountChanged(val digits: String) : PartialState
        data class GuaranteeAmountChanged(val digits: String) : PartialState
        data class FieldError(val error: DeferredInstallmentFieldError?) : PartialState
        data class ConfirmDialogVisible(val visible: Boolean) : PartialState
        data class Submitting(val isSubmitting: Boolean) : PartialState
        data class SubmitSucceeded(val refCode: String?) : PartialState
        data class SubmitFailed(val message: String) : PartialState
    }
}

enum class DeferredInstallmentFieldError {
    GUARANTEE,
    FIRST_NAME,
    LAST_NAME,
    NATIONAL_ID,
    BIRTH_DATE,
    BANK,
    BRANCH,
    AMOUNT,
    AMOUNT_MIN,
    COUNT,
    COUNT_RANGE,
    GUARANTEE_AMOUNT,
    PENSIONER,
}

sealed interface DeferredInstallmentIntent {
    data object LoadInitialData : DeferredInstallmentIntent
    data class OnPickerRequested(val picker: DeferredInstallmentPicker) : DeferredInstallmentIntent
    data object OnPickerDismissed : DeferredInstallmentIntent
    data class OnGuaranteePicked(val type: String) : DeferredInstallmentIntent
    data class OnFirstNameChanged(val value: String) : DeferredInstallmentIntent
    data class OnLastNameChanged(val value: String) : DeferredInstallmentIntent
    data class OnNationalIdChanged(val value: String) : DeferredInstallmentIntent
    data class OnBirthDatePicked(val year: Int, val month: Int, val day: Int) : DeferredInstallmentIntent
    data object OnNextStepClicked : DeferredInstallmentIntent
    data object BackToPreviousStep : DeferredInstallmentIntent
    data class OnBankQueryChanged(val query: String) : DeferredInstallmentIntent
    data class OnBankPicked(val option: DeferredInstallmentOptionUi) : DeferredInstallmentIntent
    data class OnBranchChanged(val value: String) : DeferredInstallmentIntent
    data class OnInstallmentAmountChanged(val value: String) : DeferredInstallmentIntent
    data class OnInstallmentCountChanged(val value: String) : DeferredInstallmentIntent
    data class OnGuaranteeAmountChanged(val value: String) : DeferredInstallmentIntent
    data object OnSubmitClicked : DeferredInstallmentIntent
    data object OnConfirmDismissed : DeferredInstallmentIntent
    data object OnConfirmSubmit : DeferredInstallmentIntent
    data object OnSubmitSuccessAcknowledged : DeferredInstallmentIntent
}

sealed interface DeferredInstallmentEvent {
    data object NavigateBack : DeferredInstallmentEvent
}
