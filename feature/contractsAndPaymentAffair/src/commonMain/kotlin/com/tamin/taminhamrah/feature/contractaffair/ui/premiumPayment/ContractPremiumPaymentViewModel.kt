package com.tamin.taminhamrah.feature.contractaffair.ui.premiumPayment

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.contractaffair.ui.premiumPayment.contract.ContractPremiumPaymentEvent
import com.tamin.taminhamrah.feature.contractaffair.ui.premiumPayment.contract.ContractPremiumPaymentIntent
import com.tamin.taminhamrah.feature.contractaffair.ui.premiumPayment.contract.ContractPremiumPaymentUiState
import com.tamin.taminhamrah.feature.contractaffair.ui.premiumPayment.contract.ContractPremiumPaymentUiState.PartialState
import com.tamin.taminhamrah.feature.contractaffair.ui.premiumPayment.contract.MAX_PAYMENT_MONTHS
import com.tamin.taminhamrah.feature.contractaffair.ui.premiumPayment.contract.MIN_PAYMENT_MONTHS
import com.tamin.taminhamrah.mapper.contractAffair.toPresentation
import com.tamin.taminhamrah.model.contractAffair.ContractPremiumType
import com.tamin.taminhamrah.model.contracts.InsurancePaymentParamsDN
import com.tamin.taminhamrah.model.payment.PaymentRequestDN
import com.tamin.taminhamrah.model.payment.PaymentVerifierKey
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.contractAffair.GetContractDebitUseCase
import com.tamin.taminhamrah.useCases.contractAffair.GetContractLastPaymentUseCase
import com.tamin.taminhamrah.useCases.contracts.GetInsurancePaymentUseCase
import com.tamin.taminhamrah.util.NetworkConstants
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_premium_payment_ticket_error

/**
 * پرداخت حق بیمه (محاسبهٔ حق بیمه). Ported from `old_android`'s `InsurancePaymentViewModel`
 * (`getInitData` / `getInitDataOptional` + `calculateDebitByMonth`), integrated with shared
 * `:feature:payment` via [GetInsurancePaymentUseCase].
 */
class ContractPremiumPaymentViewModel(
    private val getContractLastPaymentUseCase: GetContractLastPaymentUseCase,
    private val getContractDebitUseCase: GetContractDebitUseCase,
    private val getInsurancePaymentUseCase: GetInsurancePaymentUseCase,
) : BaseViewModel<ContractPremiumPaymentUiState, PartialState, ContractPremiumPaymentEvent, ContractPremiumPaymentIntent>(
    initialState = ContractPremiumPaymentUiState(),
) {

    private var premiumType: ContractPremiumType = ContractPremiumType.FREELANCE
    private var hasLoaded = false

    override fun handleIntent(intent: ContractPremiumPaymentIntent): Flow<PartialState> =
        when (intent) {
            is ContractPremiumPaymentIntent.Load ->
                if (hasLoaded) {
                    emptyFlow()
                } else {
                    hasLoaded = true
                    load(intent)
                }
            ContractPremiumPaymentIntent.Retry -> loadLastPayment()

            ContractPremiumPaymentIntent.IncrementMonths ->
                changeMonths(uiState.value.months + 1)

            ContractPremiumPaymentIntent.DecrementMonths ->
                changeMonths(uiState.value.months - 1)

            ContractPremiumPaymentIntent.Calculate -> calculate()
            ContractPremiumPaymentIntent.OpenPaymentDetails -> openPaymentDetails()

            ContractPremiumPaymentIntent.Pay -> pay()

            ContractPremiumPaymentIntent.OnResumed -> reloadLastPaymentOnResume()

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
            if (e is CancellationException) throw e
            emit(PartialState.Error(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.InitLoading(false))
        }
    }

    private fun reloadLastPaymentOnResume(): Flow<PartialState> {
        if (!hasLoaded) return emptyFlow()
        return loadLastPayment()
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
            if (e is CancellationException) throw e
            sendEvent(ContractPremiumPaymentEvent.ShowError(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.Calculating(false))
        }
    }

    private fun pay(): Flow<PartialState> {
        val debit = uiState.value.debit ?: return emptyFlow()
        val systemType = when (premiumType) {
            ContractPremiumType.OPTIONAL -> "01"
            ContractPremiumType.FRACTION -> "04"
            else -> "03"
        }
        return flow {
            emit(PartialState.Paying(true))
            try {
                val params = InsurancePaymentParamsDN(
                    systemType = systemType,
                    redirectUrl = NetworkConstants.TFH_BASE_URL,
                    startDate = debit.startDate,
                    endDate = debit.endDate,
                    amount = debit.payableAmount.toLongOrNull() ?: 0L,
                    redirectUri = "mytamin://payment_callback",
                    paramPage = "0",
                    month = uiState.value.months,
                )
                val payment = getInsurancePaymentUseCase(params).first()
                val ticket = payment.paymentTicket
                if (payment.succeed == true && !ticket.isNullOrBlank()) {
                    emit(PartialState.DebitCalculated(null))
                    sendEvent(
                        ContractPremiumPaymentEvent.NavigateToPayment(
                            PaymentRequestDN(
                                ticket = ticket,
                                verifierKey = PaymentVerifierKey.SPECIAL_INSURED,
                                verifierReference = systemType,
                            ),
                        ),
                    )
                } else {
                    val message = payment.responseMessage ?: getString(Res.string.contract_premium_payment_ticket_error)
                    sendEvent(ContractPremiumPaymentEvent.ShowError(message))
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                sendEvent(ContractPremiumPaymentEvent.ShowError(e.toSingleLineMessage()))
            } finally {
                emit(PartialState.Paying(false))
            }
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

        is PartialState.Paying -> currentState.copy(isPaying = partialState.isPaying)

        is PartialState.DebitCalculated -> currentState.copy(debit = partialState.debit)

        is PartialState.Error -> currentState.copy(error = partialState.message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
