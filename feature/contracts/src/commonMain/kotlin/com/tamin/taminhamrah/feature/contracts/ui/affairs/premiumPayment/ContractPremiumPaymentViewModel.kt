package com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment.contract.ContractPremiumPaymentEvent
import com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment.contract.ContractPremiumPaymentIntent
import com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment.contract.ContractPremiumPaymentUiState
import com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment.contract.ContractPremiumPaymentUiState.PartialState
import com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment.contract.MAX_PAYMENT_MONTHS
import com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment.contract.MIN_PAYMENT_MONTHS
import com.tamin.taminhamrah.mapper.contracts.toPresentation
import com.tamin.taminhamrah.model.contracts.ContractPremiumType
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.contracts.GetContractDebitUseCase
import com.tamin.taminhamrah.useCases.contracts.GetContractLastPaymentUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

/**
 * پرداخت حق بیمه (محاسبهٔ حق بیمه). Ported from `old_android`'s `InsurancePaymentViewModel`
 * (`getInitData` / `getInitDataOptional` + `calculateDebitByMonth`), trimmed to what the new
 * design needs — the SEP online-payment call is intentionally left out.
 */
class ContractPremiumPaymentViewModel(
    private val getContractLastPaymentUseCase: GetContractLastPaymentUseCase,
    private val getContractDebitUseCase: GetContractDebitUseCase,
) : BaseViewModel<ContractPremiumPaymentUiState, PartialState, ContractPremiumPaymentEvent, ContractPremiumPaymentIntent>(
    initialState = ContractPremiumPaymentUiState(),
) {

    private var premiumType: ContractPremiumType = ContractPremiumType.FREELANCE

    override fun handleIntent(intent: ContractPremiumPaymentIntent): Flow<PartialState> =
        when (intent) {
            is ContractPremiumPaymentIntent.Load -> load(intent)
            ContractPremiumPaymentIntent.Retry -> loadLastPayment()

            ContractPremiumPaymentIntent.IncrementMonths ->
                changeMonths(uiState.value.months + 1)

            ContractPremiumPaymentIntent.DecrementMonths ->
                changeMonths(uiState.value.months - 1)

            ContractPremiumPaymentIntent.Calculate -> calculate()
            ContractPremiumPaymentIntent.OpenPaymentDetails -> openPaymentDetails()

            ContractPremiumPaymentIntent.Pay -> {
                // TODO: SEP online-payment — intentionally deferred, no logic wired here.
                emptyFlow()
            }

            ContractPremiumPaymentIntent.OnBackClicked -> {
                sendEvent(ContractPremiumPaymentEvent.NavigateBack)
                emptyFlow()
            }
        }

    private fun load(intent: ContractPremiumPaymentIntent.Load): Flow<PartialState> {
        premiumType = ContractPremiumType.fromCode(intent.premiumTypeCode)
            ?: ContractPremiumType.FREELANCE
        val isFreelance = premiumType != ContractPremiumType.OPTIONAL
        return flow {
            emit(
                PartialState.HeaderSeeded(
                    contractNumber = intent.contractNumber,
                    premiumTypeCode = intent.premiumTypeCode,
                    insuranceType = intent.insuranceType,
                    isFreelance = isFreelance,
                ),
            )
            loadLastPayment().collect { emit(it) }
        }
    }

    private fun loadLastPayment(): Flow<PartialState> = flow {
        emit(PartialState.InitLoading(true))
        try {
            val lastPayment = getContractLastPaymentUseCase(premiumType).first().toPresentation()
            emit(PartialState.LastPaymentLoaded(lastPayment))
            lastPayment.warningMessage?.let {
                sendEvent(ContractPremiumPaymentEvent.ShowWarning(it))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.InitLoading(false))
        }
    }

    private fun changeMonths(target: Int): Flow<PartialState> {
        val clamped = target.coerceIn(MIN_PAYMENT_MONTHS, MAX_PAYMENT_MONTHS)
        if (clamped == uiState.value.months) return emptyFlow()
        return flow { emit(PartialState.MonthsChanged(clamped)) }
    }

    private fun calculate(): Flow<PartialState> = flow {
        emit(PartialState.Calculating(true))
        try {
            val debit = getContractDebitUseCase(premiumType, uiState.value.months)
                .first()
                .toPresentation()
            emit(PartialState.DebitCalculated(debit))
            debit.infoMessage?.let {
                sendEvent(ContractPremiumPaymentEvent.ShowWarning(it))
            }
        } catch (e: Exception) {
            sendEvent(ContractPremiumPaymentEvent.ShowError(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.Calculating(false))
        }
    }

    private fun openPaymentDetails(): Flow<PartialState> {
        val debit = uiState.value.debit ?: return emptyFlow()
        sendEvent(
            ContractPremiumPaymentEvent.NavigateToPaymentDetails(
                premiumTypeCode = uiState.value.premiumTypeCode,
                startDate = debit.startDate,
                endDate = debit.endDate,
            ),
        )
        return emptyFlow()
    }

    override fun reduceState(
        currentState: ContractPremiumPaymentUiState,
        partialState: PartialState,
    ): ContractPremiumPaymentUiState = when (partialState) {
        is PartialState.HeaderSeeded -> currentState.copy(
            contractNumber = partialState.contractNumber,
            premiumTypeCode = partialState.premiumTypeCode,
            insuranceType = partialState.insuranceType,
            isFreelance = partialState.isFreelance,
        )

        is PartialState.InitLoading -> currentState.copy(isInitLoading = partialState.loading)

        is PartialState.LastPaymentLoaded -> currentState.copy(
            lastPayment = partialState.lastPayment,
            error = null,
        )

        is PartialState.MonthsChanged -> currentState.copy(
            months = partialState.months,
            debit = null,
        )

        is PartialState.Calculating -> currentState.copy(isCalculating = partialState.calculating)

        is PartialState.DebitCalculated -> currentState.copy(debit = partialState.debit)

        is PartialState.Error -> currentState.copy(error = partialState.message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
