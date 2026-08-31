package com.tamin.taminhamrah.feature.taminServices.funeralAllowance

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceEvent
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceIntent
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceUiState
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceUiState.PartialState
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.model.toPR
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.model.toSubmitParams
import com.tamin.taminhamrah.mapper.bankAccount.toPresentation
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.bankAccount.GetBankAccountListUseCase
import com.tamin.taminhamrah.useCases.funeralAllowance.ConfirmFuneralAccountCorrectionUseCase
import com.tamin.taminhamrah.useCases.funeralAllowance.GetFuneralAllowanceInfoUseCase
import com.tamin.taminhamrah.useCases.funeralAllowance.SubmitFuneralAllowanceRequestUseCase
import com.tamin.taminhamrah.useCases.funeralAllowance.ValidateDeceasedUseCase
import com.tamin.taminhamrah.util.ValidationUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.funeral_allowance_error_invalid_national_id

class FuneralAllowanceViewModel(
    private val getBankAccountListUseCase: GetBankAccountListUseCase,
    private val getFuneralAllowanceInfoUseCase: GetFuneralAllowanceInfoUseCase,
    private val validateDeceasedUseCase: ValidateDeceasedUseCase,
    private val submitFuneralAllowanceRequestUseCase: SubmitFuneralAllowanceRequestUseCase,
    private val confirmFuneralAccountCorrectionUseCase: ConfirmFuneralAccountCorrectionUseCase,
) : BaseViewModel<FuneralAllowanceUiState, PartialState, FuneralAllowanceEvent, FuneralAllowanceIntent>(
    initialState = FuneralAllowanceUiState(),
) {

    init {
        sendIntent(FuneralAllowanceIntent.LoadInfo)
    }

    override fun handleIntent(intent: FuneralAllowanceIntent): Flow<PartialState> = when (intent) {
        is FuneralAllowanceIntent.LoadInfo -> checkBankAccountThenLoadInfo()
        is FuneralAllowanceIntent.Retry -> checkBankAccountThenLoadInfo()
        is FuneralAllowanceIntent.DeceasedNationalCodeChanged -> onNationalCodeChanged(intent.value)
        is FuneralAllowanceIntent.ValidateDeceased -> validateDeceased()
        is FuneralAllowanceIntent.SubmitRequest -> submitRequest()
        is FuneralAllowanceIntent.ConfirmAccountCorrection -> confirmAccountCorrection()
        is FuneralAllowanceIntent.GoToNextStep -> flow { emit(PartialState.GoToNextStep) }
        is FuneralAllowanceIntent.GoToPreviousStep -> flow { emit(PartialState.GoToPreviousStep) }
        is FuneralAllowanceIntent.SelectBankAccount -> flow { emit(PartialState.BankAccountSelected(intent.bankAccount)) }
        is FuneralAllowanceIntent.ToggleAccountConfirmation -> flow { emit(PartialState.AccountConfirmationToggled(intent.isConfirmed)) }
        is FuneralAllowanceIntent.ShowBankAccountBottomSheet -> flow { emit(PartialState.ShowBankAccountBottomSheet(intent.show)) }
        is FuneralAllowanceIntent.NavigateToBankAccount -> flow {
            emit(PartialState.DismissNoBankAccountDialog)
            sendEvent(FuneralAllowanceEvent.NavigateToBankAccount)
        }
        is FuneralAllowanceIntent.DismissNoBankAccountDialog -> flow {
            emit(PartialState.DismissNoBankAccountDialog)
            sendEvent(FuneralAllowanceEvent.NavigateBack)
        }
    }

    /**
     * Pre-flight gate: the funeral-allowance flow can only run once the user has
     * at least one registered bank account. Empty list -> block behind the
     * no-bank-account dialog; otherwise continue into [loadInfo].
     */
    private fun checkBankAccountThenLoadInfo(): Flow<PartialState> = flow {
        emit(PartialState.ClearError)
        emit(PartialState.CheckingBankAccount(true))
        val hasBankAccount = try {
            getBankAccountListUseCase().first().isNotEmpty()
        } catch (e: Exception) {
            emit(PartialState.CheckingBankAccount(false))
            val message = e.toSingleLineMessage()
            emit(PartialState.Error(message))
            sendEvent(FuneralAllowanceEvent.ShowErrorToast(message))
            return@flow
        }
        emit(PartialState.CheckingBankAccount(false))
        if (!hasBankAccount) {
            emit(PartialState.NoBankAccount)
            return@flow
        }
        emitAll(loadInfo())
    }

    private fun loadInfo(): Flow<PartialState> = flow {
        try {
            val bankAccounts = getBankAccountListUseCase().first().map { it.toPresentation() }
            val info = getFuneralAllowanceInfoUseCase()
            emit(PartialState.InfoLoaded(info.toPR(), bankAccounts))
        } catch (e: Exception) {
            val message = e.toSingleLineMessage()
            emit(PartialState.Error(message))
            sendEvent(FuneralAllowanceEvent.ShowErrorToast(message))
        }
    }.onStart {
        emit(PartialState.ClearError)
        emit(PartialState.Loading(true))
    }.onCompletion {
        emit(PartialState.Loading(false))
    }

    private fun onNationalCodeChanged(value: String): Flow<PartialState> = flow {
        val digits = value.filter { it.isDigit() }.take(10)
        emit(PartialState.DeceasedNationalCodeChanged(digits))
        emit(PartialState.DeceasedNationalCodeError(null))
        // Any edit invalidates a previous eligibility result.
        emit(PartialState.DeceasedValidationCleared)
    }

    private fun validateDeceased(): Flow<PartialState> = flow {
        val nationalCode = uiState.value.deceasedNationalCode
        if (!ValidationUtils.isNationalIdValid(nationalCode)) {
            emit(PartialState.DeceasedNationalCodeError(getString(Res.string.funeral_allowance_error_invalid_national_id)))
            return@flow
        }
        emit(PartialState.ValidatingDeceased(true))
        try {
            val validation = validateDeceasedUseCase(nationalCode)
            if (validation.isEligible) {
                emit(PartialState.DeceasedValidated(validation.toPR()))
            } else {
                emit(PartialState.DeceasedValidationCleared)
                sendEvent(FuneralAllowanceEvent.ShowInfoMessage(validation.message))
            }
        } catch (e: Exception) {
            sendEvent(FuneralAllowanceEvent.ShowErrorToast(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.ValidatingDeceased(false))
        }
    }

    private fun submitRequest(): Flow<PartialState> = flow {
        val state = uiState.value
        val info = state.info ?: return@flow
        if (!state.canSubmitRequest) return@flow
        emit(PartialState.Submitting(true))
        try {
            val params = info.toSubmitParams(state.deceasedNationalCode)
            val message = submitFuneralAllowanceRequestUseCase(params)
            sendEvent(FuneralAllowanceEvent.ShowSuccessMessage(message))
        } catch (e: Exception) {
            sendEvent(FuneralAllowanceEvent.ShowErrorToast(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.Submitting(false))
        }
    }

    private fun confirmAccountCorrection(): Flow<PartialState> = flow {
        val requestId = uiState.value.info?.registeredRequest?.requestId
        if (requestId == null) {
            emit(PartialState.DeceasedValidationCleared)
            return@flow
        }
        emit(PartialState.ConfirmingCorrection(true))
        try {
            val message = confirmFuneralAccountCorrectionUseCase(requestId.toString())
            sendEvent(FuneralAllowanceEvent.ShowSuccessMessage(message))
        } catch (e: Exception) {
            sendEvent(FuneralAllowanceEvent.ShowErrorToast(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.ConfirmingCorrection(false))
        }
    }

    override fun reduceState(
        currentState: FuneralAllowanceUiState,
        partialState: PartialState,
    ): FuneralAllowanceUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.ClearError -> currentState.copy(errorMessage = null)
        is PartialState.CheckingBankAccount -> currentState.copy(
            isCheckingBankAccount = partialState.inProgress,
            isLoading = partialState.inProgress,
        )

        is PartialState.NoBankAccount -> currentState.copy(
            isLoading = false,
            isCheckingBankAccount = false,
            showNoBankAccountDialog = true,
        )

        is PartialState.DismissNoBankAccountDialog ->
            currentState.copy(showNoBankAccountDialog = false)
        is PartialState.InfoLoaded -> currentState.copy(
            isLoading = false,
            errorMessage = null,
            info = partialState.info,
            bankAccounts = partialState.bankAccounts,
            selectedBankAccount = partialState.bankAccounts.firstOrNull { it.isActive } ?: partialState.bankAccounts.firstOrNull(),
        )

        is PartialState.DeceasedNationalCodeChanged ->
            currentState.copy(deceasedNationalCode = partialState.value)

        is PartialState.DeceasedNationalCodeError ->
            currentState.copy(deceasedNationalCodeError = partialState.message)

        is PartialState.ValidatingDeceased ->
            currentState.copy(isValidatingDeceased = partialState.inProgress)

        is PartialState.DeceasedValidated ->
            currentState.copy(deceasedValidation = partialState.validation)

        is PartialState.DeceasedValidationCleared ->
            currentState.copy(deceasedValidation = null)

        is PartialState.Submitting -> currentState.copy(isSubmitting = partialState.inProgress)
        is PartialState.ConfirmingCorrection ->
            currentState.copy(isConfirmingCorrection = partialState.inProgress)

        is PartialState.GoToNextStep -> currentState.copy(
            currentStep = com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceStep.values().getOrNull(currentState.currentStep.ordinal + 1) ?: currentState.currentStep
        )
        is PartialState.GoToPreviousStep -> currentState.copy(
            currentStep = com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceStep.values().getOrNull(currentState.currentStep.ordinal - 1) ?: currentState.currentStep
        )
        is PartialState.BankAccountSelected -> currentState.copy(
            selectedBankAccount = partialState.bankAccount,
            showBankAccountBottomSheet = false
        )
        is PartialState.AccountConfirmationToggled -> currentState.copy(
            isAccountConfirmed = partialState.isConfirmed
        )
        is PartialState.ShowBankAccountBottomSheet -> currentState.copy(
            showBankAccountBottomSheet = partialState.show
        )

        is PartialState.Error -> currentState.copy(
            isLoading = false,
            errorMessage = partialState.message.ifBlank { null },
        )
    }

    override fun createErrorState(message: String): PartialState {
        sendEvent(FuneralAllowanceEvent.ShowErrorToast(message))
        return PartialState.Loading(false)
    }
}
