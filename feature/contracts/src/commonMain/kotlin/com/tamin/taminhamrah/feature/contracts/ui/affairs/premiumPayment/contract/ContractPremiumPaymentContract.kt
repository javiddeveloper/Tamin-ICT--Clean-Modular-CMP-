package com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.contracts.ContractDebitPR
import com.tamin.taminhamrah.model.contracts.ContractLastPaymentPR

/** دورهٔ پرداخت stepper bounds — 1..12 months, matching `old_android`'s `InsurancePaymentFragment`. */
internal const val MIN_PAYMENT_MONTHS = 1
internal const val MAX_PAYMENT_MONTHS = 12

/**
 * پرداخت حق بیمه (محاسبهٔ حق بیمه) — reached from «پرداخت حق بیمه» in امور قرارداد (card button or
 * sheet row) for an active حرف و مشاغل آزاد / بیمهٔ اختیاری contract.
 *
 * [contractNumber] / [premiumTypeCode] / [insuranceType] arrive as route arguments. [lastPayment]
 * seeds the «تا تاریخ … پرداخت شده است» banner; [debit] is the محاسبهٔ حق بیمه result card and is
 * cleared whenever [months] changes. The bottom «پرداخت حق بیمه» CTA is enabled once [debit] exists
 * but its action is intentionally not wired yet (SEP online-payment deferred).
 */
@Immutable
data class ContractPremiumPaymentUiState(
    val contractNumber: String = "",
    val premiumTypeCode: String = "",
    val insuranceType: String = "",
    val isFreelance: Boolean = true,
    val isInitLoading: Boolean = false,
    val lastPayment: ContractLastPaymentPR? = null,
    val months: Int = MIN_PAYMENT_MONTHS,
    val isCalculating: Boolean = false,
    val debit: ContractDebitPR? = null,
    val error: String? = null,
) {
    val canDecrement: Boolean get() = months > MIN_PAYMENT_MONTHS
    val canIncrement: Boolean get() = months < MAX_PAYMENT_MONTHS
    val canPay: Boolean get() = debit != null

    sealed interface PartialState {
        data class HeaderSeeded(
            val contractNumber: String,
            val premiumTypeCode: String,
            val insuranceType: String,
            val isFreelance: Boolean,
        ) : PartialState

        data class InitLoading(val loading: Boolean) : PartialState
        data class LastPaymentLoaded(val lastPayment: ContractLastPaymentPR) : PartialState
        data class MonthsChanged(val months: Int) : PartialState
        data class Calculating(val calculating: Boolean) : PartialState
        data class DebitCalculated(val debit: ContractDebitPR) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface ContractPremiumPaymentIntent {
    /** Sent once from the Route with the values carried by the route. */
    data class Load(
        val contractNumber: String,
        val premiumTypeCode: String,
        val insuranceType: String,
    ) : ContractPremiumPaymentIntent

    data object IncrementMonths : ContractPremiumPaymentIntent
    data object DecrementMonths : ContractPremiumPaymentIntent
    data object Calculate : ContractPremiumPaymentIntent
    data object OpenPaymentDetails : ContractPremiumPaymentIntent

    /** پرداخت حق بیمه — deliberately a no-op for now (SEP online-payment not in scope). */
    data object Pay : ContractPremiumPaymentIntent
    data object Retry : ContractPremiumPaymentIntent
    data object OnBackClicked : ContractPremiumPaymentIntent
}

sealed interface ContractPremiumPaymentEvent {
    data object NavigateBack : ContractPremiumPaymentEvent
    data class ShowError(val message: String) : ContractPremiumPaymentEvent
    data class ShowWarning(val message: String) : ContractPremiumPaymentEvent

    /** جزئیات برگ پرداخت — open the ریز محاسبه screen for the calculated period. */
    data class NavigateToPaymentDetails(
        val premiumTypeCode: String,
        val startDate: Long,
        val endDate: Long,
    ) : ContractPremiumPaymentEvent
}
