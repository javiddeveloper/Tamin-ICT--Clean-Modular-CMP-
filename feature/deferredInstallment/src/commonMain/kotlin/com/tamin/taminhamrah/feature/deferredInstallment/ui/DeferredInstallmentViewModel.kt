package com.tamin.taminhamrah.feature.deferredInstallment.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.DeferredInstallmentEvent
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.DeferredInstallmentFieldError
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.DeferredInstallmentIntent
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.DeferredInstallmentOptionUi
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.DeferredInstallmentPicker
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.DeferredInstallmentUiState
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.DeferredInstallmentUiState.PartialState
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.GUARANTEE_FOR_OTHERS
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.MAX_INSTALLMENT_COUNT
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.MIN_INSTALLMENT_AMOUNT
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.MIN_INSTALLMENT_COUNT
import com.tamin.taminhamrah.mapper.common.toPresentation
import com.tamin.taminhamrah.model.pension.installment.BankDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequestDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.common.GetBeneficiaryUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import com.tamin.taminhamrah.useCases.pension.SendRequestDeferredInstallmentCertificateUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DeferredInstallmentViewModel(
    private val getPensionerIdUseCase: GetPensionerIdUseCase,
    private val getBeneficiaryUseCase: GetBeneficiaryUseCase,
    private val sendRequestUseCase: SendRequestDeferredInstallmentCertificateUseCase,
) : BaseViewModel<DeferredInstallmentUiState, PartialState, DeferredInstallmentEvent, DeferredInstallmentIntent>(
    initialState = DeferredInstallmentUiState(),
) {

    init {
        sendIntent(DeferredInstallmentIntent.LoadInitialData)
    }

    override fun handleIntent(intent: DeferredInstallmentIntent): Flow<PartialState> = when (intent) {
        DeferredInstallmentIntent.LoadInitialData -> loadInitialData()
        is DeferredInstallmentIntent.OnPickerRequested -> handlePickerRequested(intent.picker)
        DeferredInstallmentIntent.OnPickerDismissed -> flow {
            emit(PartialState.PickerChanged(DeferredInstallmentPicker.NONE))
        }
        is DeferredInstallmentIntent.OnGuaranteePicked -> flow {
            emit(PartialState.GuaranteeSelected(intent.type))
            emit(PartialState.PickerChanged(DeferredInstallmentPicker.NONE))
        }
        is DeferredInstallmentIntent.OnFirstNameChanged -> flow {
            emit(PartialState.FirstNameChanged(intent.value))
        }
        is DeferredInstallmentIntent.OnLastNameChanged -> flow {
            emit(PartialState.LastNameChanged(intent.value))
        }
        is DeferredInstallmentIntent.OnNationalIdChanged -> flow {
            emit(PartialState.NationalIdChanged(intent.value.digitsOnly().take(10)))
        }
        is DeferredInstallmentIntent.OnBirthDatePicked -> flow {
            val millis = PersianDateFormatter.toEpochMillis(intent.year, intent.month, intent.day)
            emit(
                PartialState.BirthDateSelected(
                    label = PersianDateFormatter.format(intent.year, intent.month, intent.day),
                    iso = millis.toIsoDateTime(),
                )
            )
            emit(PartialState.PickerChanged(DeferredInstallmentPicker.NONE))
        }
        DeferredInstallmentIntent.OnNextStepClicked -> flow {
            val error = uiState.value.stepOneError()
            if (error != null) {
                emit(PartialState.FieldError(error))
            } else {
                emit(PartialState.StepChanged(DeferredInstallmentStep.LoanDetails))
            }
        }
        DeferredInstallmentIntent.BackToPreviousStep -> handleBackStep()
        is DeferredInstallmentIntent.OnBankQueryChanged -> loadBanks(intent.query)
        is DeferredInstallmentIntent.OnBankPicked -> flow {
            emit(PartialState.BankSelected(intent.option))
            emit(PartialState.PickerChanged(DeferredInstallmentPicker.NONE))
        }
        is DeferredInstallmentIntent.OnBranchChanged -> flow {
            emit(PartialState.BranchChanged(intent.value))
        }
        is DeferredInstallmentIntent.OnInstallmentAmountChanged -> flow {
            emit(PartialState.InstallmentAmountChanged(intent.value.digitsOnly()))
        }
        is DeferredInstallmentIntent.OnInstallmentCountChanged -> flow {
            val digits = intent.value.digitsOnly()
            val capped = digits.toIntOrNull()?.coerceAtMost(MAX_INSTALLMENT_COUNT)?.toString() ?: digits
            emit(PartialState.InstallmentCountChanged(capped))
        }
        is DeferredInstallmentIntent.OnGuaranteeAmountChanged -> flow {
            emit(PartialState.GuaranteeAmountChanged(intent.value.digitsOnly()))
        }
        DeferredInstallmentIntent.OnSubmitClicked -> flow {
            val error = uiState.value.stepTwoError()
            if (error != null) {
                emit(PartialState.FieldError(error))
            } else {
                emit(PartialState.ConfirmDialogVisible(true))
            }
        }
        DeferredInstallmentIntent.OnConfirmDismissed -> flow {
            emit(PartialState.ConfirmDialogVisible(false))
        }
        DeferredInstallmentIntent.OnConfirmSubmit -> submitRequest()
        DeferredInstallmentIntent.OnSubmitSuccessAcknowledged -> flow {
            sendEvent(DeferredInstallmentEvent.NavigateBack)
        }
    }

    private fun handlePickerRequested(picker: DeferredInstallmentPicker): Flow<PartialState> = flow {
        emit(PartialState.PickerChanged(picker))
        if (picker == DeferredInstallmentPicker.BANK) {
            loadBanks(uiState.value.bankQuery).collect { emit(it) }
        }
    }

    private fun handleBackStep(): Flow<PartialState> = flow {
        when (uiState.value.currentStep) {
            DeferredInstallmentStep.LoanDetails -> emit(PartialState.StepChanged(DeferredInstallmentStep.CertificateRequest))
            DeferredInstallmentStep.CertificateRequest -> sendEvent(DeferredInstallmentEvent.NavigateBack)
        }
    }

    private fun loadInitialData(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val ids = getPensionerIdUseCase().first()
                .mapNotNull { it.pensionerId?.takeIf(String::isNotBlank) }
                .toPersistentList()
            emit(PartialState.PensionerLoaded(ids = ids, selectedId = ids.firstOrNull().orEmpty()))
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        }
        emit(PartialState.Loading(false))
    }

    private fun loadBanks(query: String): Flow<PartialState> = flow {
        emit(PartialState.BanksLoading(true))
        try {
            val filters = if (query.isBlank()) {
                emptyList()
            } else {
                listOf(
                    ApiFilterDN(
                        property = FilterProperty.BANK_NAME,
                        value = "*$query*",
                        operator = FilterOperator.LIKE,
                    )
                )
            }
            val options = getBeneficiaryUseCase(filters).first()
                .map { it.toPresentation() }
                .mapNotNull { bank ->
                    val code = bank.bankCode ?: return@mapNotNull null
                    DeferredInstallmentOptionUi(id = code, label = bank.bankName.orEmpty())
                }
                .toPersistentList()
            emit(PartialState.BanksLoaded(options = options, query = query))
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        }
        emit(PartialState.BanksLoading(false))
    }

    private fun submitRequest(): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.isSubmitting) return@flow
        emit(PartialState.ConfirmDialogVisible(false))
        emit(PartialState.Submitting(true))
        try {
            val result = sendRequestUseCase(state.toRequest()).first()
            emit(PartialState.SubmitSucceeded(result.request?.refCode))
        } catch (e: Exception) {
            emit(PartialState.SubmitFailed(e.toSingleLineMessage()))
        }
    }

    override fun reduceState(
        currentState: DeferredInstallmentUiState,
        partialState: PartialState,
    ): DeferredInstallmentUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Error -> currentState.copy(isLoading = false, isLoadingBanks = false, error = partialState.message)
        is PartialState.PensionerLoaded -> currentState.copy(
            pensionerIds = partialState.ids,
            pensionerId = partialState.selectedId,
        )
        is PartialState.PickerChanged -> currentState.copy(picker = partialState.picker)
        is PartialState.GuaranteeSelected -> currentState.copy(
            guaranteeType = partialState.type,
            fieldError = null,
        )
        is PartialState.FirstNameChanged -> currentState.copy(firstName = partialState.value, fieldError = null)
        is PartialState.LastNameChanged -> currentState.copy(lastName = partialState.value, fieldError = null)
        is PartialState.NationalIdChanged -> currentState.copy(nationalId = partialState.value, fieldError = null)
        is PartialState.BirthDateSelected -> currentState.copy(
            birthDateLabel = partialState.label,
            birthDateIso = partialState.iso,
            fieldError = null,
        )
        is PartialState.StepChanged -> currentState.copy(currentStep = partialState.step, fieldError = null)
        is PartialState.BanksLoading -> currentState.copy(isLoadingBanks = partialState.isLoading)
        is PartialState.BanksLoaded -> currentState.copy(
            bankOptions = partialState.options,
            bankQuery = partialState.query,
        )
        is PartialState.BankSelected -> currentState.copy(bank = partialState.bank, fieldError = null)
        is PartialState.BranchChanged -> currentState.copy(branchName = partialState.value, fieldError = null)
        is PartialState.InstallmentAmountChanged -> currentState.withRecalculatedAmounts(
            amountDigits = partialState.digits,
            countDigits = currentState.installmentCountDigits,
        )
        is PartialState.InstallmentCountChanged -> currentState.withRecalculatedAmounts(
            amountDigits = currentState.installmentAmountDigits,
            countDigits = partialState.digits,
        )
        is PartialState.GuaranteeAmountChanged -> currentState.copy(
            guaranteeAmountDigits = partialState.digits,
            fieldError = null,
        )
        is PartialState.FieldError -> currentState.copy(fieldError = partialState.error)
        is PartialState.ConfirmDialogVisible -> currentState.copy(showConfirmDialog = partialState.visible)
        is PartialState.Submitting -> currentState.copy(isSubmitting = partialState.isSubmitting, submitError = null)
        is PartialState.SubmitSucceeded -> currentState.copy(
            isSubmitting = false,
            hasSubmitted = true,
            submittedRefCode = partialState.refCode,
            submitError = null,
        )
        is PartialState.SubmitFailed -> currentState.copy(isSubmitting = false, submitError = partialState.message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

private fun DeferredInstallmentUiState.withRecalculatedAmounts(
    amountDigits: String,
    countDigits: String,
): DeferredInstallmentUiState {
    val amount = amountDigits.toLongOrNull()
    val count = countDigits.toIntOrNull()
    val repayment = if (amount != null && count != null) amount * count else null
    val minGuarantee = repayment?.let { it + (it / 5) }
    return copy(
        installmentAmountDigits = amountDigits,
        installmentCountDigits = countDigits,
        guaranteeAmountDigits = minGuarantee?.toString().orEmpty(),
        fieldError = null,
    )
}

private fun DeferredInstallmentUiState.stepOneError(): DeferredInstallmentFieldError? = when {
    pensionerId.isBlank() -> DeferredInstallmentFieldError.PENSIONER
    guaranteeType.isNullOrBlank() -> DeferredInstallmentFieldError.GUARANTEE
    isGuaranteeForOthers && firstName.isBlank() -> DeferredInstallmentFieldError.FIRST_NAME
    isGuaranteeForOthers && lastName.isBlank() -> DeferredInstallmentFieldError.LAST_NAME
    isGuaranteeForOthers && nationalId.length != 10 -> DeferredInstallmentFieldError.NATIONAL_ID
    isGuaranteeForOthers && birthDateIso.isNullOrBlank() -> DeferredInstallmentFieldError.BIRTH_DATE
    else -> null
}

private fun DeferredInstallmentUiState.stepTwoError(): DeferredInstallmentFieldError? {
    val amount = installmentAmount
    val count = installmentCount
    val guarantee = guaranteeAmount
    val minGuarantee = minimumGuaranteeAmount
    return when {
        bank == null -> DeferredInstallmentFieldError.BANK
        branchName.isBlank() -> DeferredInstallmentFieldError.BRANCH
        amount == null -> DeferredInstallmentFieldError.AMOUNT
        amount < MIN_INSTALLMENT_AMOUNT -> DeferredInstallmentFieldError.AMOUNT_MIN
        count == null -> DeferredInstallmentFieldError.COUNT
        count !in MIN_INSTALLMENT_COUNT..MAX_INSTALLMENT_COUNT -> DeferredInstallmentFieldError.COUNT_RANGE
        guarantee == null || minGuarantee == null || guarantee < minGuarantee ->
            DeferredInstallmentFieldError.GUARANTEE_AMOUNT
        else -> null
    }
}

private fun DeferredInstallmentUiState.toRequest(): DeferredInstallmentRequestDN {
    val forOthers = isGuaranteeForOthers
    return DeferredInstallmentRequestDN(
        bank = BankDN(bankCode = bank?.id),
        bankBranch = branchName,
        garanteeType = guaranteeType,
        guaranteeAmount = guaranteeAmount,
        installmentAmount = installmentAmount?.toString(),
        installmentCount = installmentCount?.toString(),
        loanAmount = repaymentAmount,
        pensionerId = pensionerId,
        birthDate = birthDateIso.takeIf { forOthers },
        firstName = firstName.takeIf { forOthers },
        lastName = lastName.takeIf { forOthers },
        nationalId = nationalId.takeIf { forOthers },
    )
}

private fun Long.toIsoDateTime(): String {
    val dateTime = Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    fun Int.pad() = toString().padStart(2, '0')
    return "${dateTime.year}-${dateTime.monthNumber.pad()}-${dateTime.dayOfMonth.pad()}" +
        "T${dateTime.hour.pad()}:${dateTime.minute.pad()}:${dateTime.second.pad()}.000"
}
