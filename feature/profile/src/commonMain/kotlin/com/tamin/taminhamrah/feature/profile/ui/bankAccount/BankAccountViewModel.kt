package com.tamin.taminhamrah.feature.profile.ui.bankAccount

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.contract.BankAccountEvent
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.contract.BankAccountIntent
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.contract.BankAccountMode
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.contract.BankAccountPicker
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.contract.BankAccountUiState
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.contract.BankAccountUiState.PartialState
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.model.BankAccountDraftPR
import com.tamin.taminhamrah.mapper.bankAccount.toPresentation
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.bankAccount.GetBankAccountListUseCase
import com.tamin.taminhamrah.useCases.bankAccount.RegisterBankAccountUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class BankAccountViewModel(
    private val getBankAccountListUseCase: GetBankAccountListUseCase,
    private val registerBankAccountUseCase: RegisterBankAccountUseCase,
) : BaseViewModel<BankAccountUiState, PartialState, BankAccountEvent, BankAccountIntent>(
    initialState = BankAccountUiState()
) {

    override fun handleIntent(intent: BankAccountIntent): Flow<PartialState> = when (intent) {
        is BankAccountIntent.LoadAccounts -> loadAccounts()

        is BankAccountIntent.OnAddClicked -> flow {
            // A fresh draft each time, so a canceled attempt does not haunt the next one.
            emit(PartialState.DraftChanged(BankAccountDraftPR()))
            emit(PartialState.ModeChanged(BankAccountMode.ADD))
        }

        // Back leaves the form before it leaves the screen: the form is a second view of one page.
        is BankAccountIntent.OnBackClicked -> flow {
            if (uiState.value.mode == BankAccountMode.ADD) {
                emit(PartialState.ModeChanged(BankAccountMode.LIST))
            } else {
                sendEvent(BankAccountEvent.NavigateBack)
            }
        }

        is BankAccountIntent.OnPickerRequested -> flow {
            emit(PartialState.PickerChanged(intent.picker))
        }

        is BankAccountIntent.OnPickerDismissed -> flow {
            emit(PartialState.PickerChanged(BankAccountPicker.NONE))
        }

        is BankAccountIntent.OnStartDatePicked -> updateDraft {
            copy(startDateMillis = intent.millis, startDateLabel = intent.label)
        }

        is BankAccountIntent.OnBankPicked -> updateDraft { copy(bank = intent.bank) }

        is BankAccountIntent.OnAccountTypePicked -> updateDraft {
            copy(accountType = intent.accountType)
        }

        is BankAccountIntent.OnAccountNumberChanged -> flow {
            emit(PartialState.DraftChanged(uiState.value.draft.copy(accountNumber = intent.value)))
        }

        is BankAccountIntent.OnSubmitClicked -> submit()

        is BankAccountIntent.OnErrorDismissed -> flow { emit(PartialState.Error("")) }

        is BankAccountIntent.OnSuccessDismissed -> flow {
            emit(PartialState.SubmissionAcknowledged)
            emit(PartialState.ModeChanged(BankAccountMode.LIST))
        }
    }

    /** Choosing from a picker always closes it, so the two changes travel together. */
    private fun updateDraft(change: BankAccountDraftPR.() -> BankAccountDraftPR) = flow {
        emit(PartialState.DraftChanged(uiState.value.draft.change()))
        emit(PartialState.PickerChanged(BankAccountPicker.NONE))
    }

    private fun loadAccounts(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        getBankAccountListUseCase()
            .map { PartialState.AccountsLoaded(it.toPresentation().toImmutableList()) as PartialState }
            .catch { emit(PartialState.Error(it.toSingleLineMessage())) }
            .collect { emit(it) }
    }

    /**
     * Registering files a request rather than inserting an account, so the new one will not be in
     * the list that follows. The list is reloaded anyway: it is the only way to pick up a request
     * the service has since approved, and leaving stale rows on screen would be worse.
     */
    private fun submit(): Flow<PartialState> = flow {
        val draft = uiState.value.draft
        val bank = draft.bank
        val accountType = draft.accountType
        val startDateMillis = draft.startDateMillis

        if (draft.firstError() != null || bank == null ||
            accountType == null || startDateMillis == null
        ) {
            emit(PartialState.ValidationShown)
            return@flow
        }

        emit(PartialState.Submitting(true))
        registerBankAccountUseCase(
            accountNumber = draft.normalizedAccountNumber,
            bankCode = bank.code,
            accountTypeCode = accountType.code,
            startDateMillis = startDateMillis,
        )
            .catch { emit(PartialState.Error(it.toSingleLineMessage())) }
            .collect { referenceCode -> emit(PartialState.Submitted(referenceCode)) }

        if (uiState.value.error == null) {
            emitAll(loadAccounts())
        }
    }

    override fun reduceState(
        currentState: BankAccountUiState,
        partialState: PartialState,
    ): BankAccountUiState = when (partialState) {
        is PartialState.Loading ->
            currentState.copy(isLoading = partialState.isLoading, error = null)

        is PartialState.AccountsLoaded -> currentState.copy(
            isLoading = false,
            accounts = partialState.accounts,
            error = null,
        )

        is PartialState.ModeChanged -> currentState.copy(
            mode = partialState.mode,
            picker = BankAccountPicker.NONE,
            showValidation = false,
        )

        is PartialState.DraftChanged -> currentState.copy(draft = partialState.draft)

        is PartialState.PickerChanged -> currentState.copy(picker = partialState.picker)

        is PartialState.ValidationShown -> currentState.copy(showValidation = true)

        is PartialState.Submitting ->
            currentState.copy(isSubmitting = partialState.isSubmitting, error = null)

        is PartialState.Submitted -> currentState.copy(
            isSubmitting = false,
            hasSubmitted = true,
            submittedReferenceCode = partialState.referenceCode,
            showValidation = false,
        )

        is PartialState.SubmissionAcknowledged -> currentState.copy(
            hasSubmitted = false,
            submittedReferenceCode = null,
        )

        is PartialState.Error -> currentState.copy(
            isLoading = false,
            isSubmitting = false,
            error = partialState.message.ifEmpty { null },
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
