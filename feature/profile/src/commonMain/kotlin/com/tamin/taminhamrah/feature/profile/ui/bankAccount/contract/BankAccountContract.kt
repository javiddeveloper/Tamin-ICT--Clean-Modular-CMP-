package com.tamin.taminhamrah.feature.profile.ui.bankAccount.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.model.BankAccountDraftPR
import com.tamin.taminhamrah.model.bankAccount.AccountType
import com.tamin.taminhamrah.model.bankAccount.Bank
import com.tamin.taminhamrah.model.bankAccount.BankAccountPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** Which of the two views the one screen is showing; the design routes both through one page. */
enum class BankAccountMode { LIST, ADD }

/** Which picker is open, if any — only one can be at a time. */
enum class BankAccountPicker { NONE, DATE, BANK, TYPE }

@Immutable
data class BankAccountUiState(
    val isLoading: Boolean = false,
    val accounts: ImmutableList<BankAccountPR> = persistentListOf(),
    val mode: BankAccountMode = BankAccountMode.LIST,
    val draft: BankAccountDraftPR = BankAccountDraftPR(),
    val picker: BankAccountPicker = BankAccountPicker.NONE,
    /** Set only after a submit attempt, so the form does not scold a user who is still typing. */
    val showValidation: Boolean = false,
    val isSubmitting: Boolean = false,
    /** Every API failure, already turned into the sentence the dialog shows. */
    val error: String? = null,
    /**
     * Whether a request has been accepted. Separate from [submittedReferenceCode] because the code
     * is optional — inferring success from it hides a successful submit whenever it is absent.
     */
    val hasSubmitted: Boolean = false,
    /** The tracking code of the filed request, when the service returns one. */
    val submittedReferenceCode: String? = null,
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class AccountsLoaded(val accounts: ImmutableList<BankAccountPR>) : PartialState
        data class ModeChanged(val mode: BankAccountMode) : PartialState
        data class DraftChanged(val draft: BankAccountDraftPR) : PartialState
        data class PickerChanged(val picker: BankAccountPicker) : PartialState
        data object ValidationShown : PartialState
        data class Submitting(val isSubmitting: Boolean) : PartialState
        data class Submitted(val referenceCode: String?) : PartialState
        data object SubmissionAcknowledged : PartialState
        data class Error(val message: String) : PartialState
    }
}

sealed interface BankAccountIntent {
    data object LoadAccounts : BankAccountIntent
    data object OnAddClicked : BankAccountIntent
    data object OnBackClicked : BankAccountIntent
    data object OnErrorDismissed : BankAccountIntent
    data object OnSuccessDismissed : BankAccountIntent

    data class OnPickerRequested(val picker: BankAccountPicker) : BankAccountIntent
    data object OnPickerDismissed : BankAccountIntent

    data class OnStartDatePicked(val millis: Long, val label: String) : BankAccountIntent
    data class OnBankPicked(val bank: Bank) : BankAccountIntent
    data class OnAccountTypePicked(val accountType: AccountType) : BankAccountIntent
    data class OnAccountNumberChanged(val value: String) : BankAccountIntent

    data object OnSubmitClicked : BankAccountIntent
}

sealed interface BankAccountEvent {
    data object NavigateBack : BankAccountEvent
}
