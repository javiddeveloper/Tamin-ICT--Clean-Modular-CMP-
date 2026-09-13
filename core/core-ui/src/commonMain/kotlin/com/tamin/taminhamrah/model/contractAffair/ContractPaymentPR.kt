package com.tamin.taminhamrah.model.contractAffair

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

/**
 * محاسبهٔ حق بیمه result card.
 *
 * Money fields ([payableAmount] / [periodPremiumAmount] / [pastDebtAmount]) are raw digit strings —
 * format with `toRialAmount()` at the call site, exactly like [ContractPaymentHistoryItemPR]. Date
 * fields already carry Persian digits. [startDate] / [endDate] are the raw timestamps handed to the
 * جزئیات برگ پرداخت route.
 */
@Immutable
data class ContractDebitPR(
    val payableAmount: String,
    val periodPremiumAmount: String,
    val pastDebtAmount: String,
    val periodStartLabel: String,
    val periodEndLabel: String,
    val deadlineLabel: String,
    val hasPastDebt: Boolean,
    val infoMessage: String?,
    val startDate: Long,
    val endDate: Long,
)

/**
 * آخرین پرداخت حق بیمه banner state. [paidUntilLabel] is "" when nothing has been paid yet;
 * [warningMessage] is a backend `chekReloLap` message worth surfacing (freelance only).
 */
@Immutable
data class ContractLastPaymentPR(
    val paidUntilLabel: String,
    val hasHistory: Boolean,
    val warningMessage: String?,
)

/**
 * One line of a month's ریز محاسبه — e.g. «حق بیمه» with a positive [amountRaw], or «کمک دولت»
 * with a negative one ([isDeduction] `true`). [amountRaw] is a signed integer; format with
 * `toPriceFormat()` at the call site.
 */
@Immutable
data class PaymentCalcLinePR(
    val label: String,
    val amountRaw: Long,
    val isDeduction: Boolean,
)

/**
 * One جزئیات برگ پرداخت card — a single month, holding every [lines] entry the `payment-details`
 * endpoint returned for that month (both حق بیمه and کمک دولت).
 *
 * [monthTitle] / [monthNumberLabel] / [daysLabel] already carry Persian digits. [baseWageRaw] is
 * `دستمزد مبنا` (`wage × days`, from the primary حق بیمه line) and [ratePercent] is `نرخ حق بیمه`
 * (`amount ÷ base × 100`); both are `0` / `null` when they cannot be computed. [netAmountRaw] is the
 * signed sum of every line. Money values are plain integers — format with `toPriceFormat()`.
 */
@Immutable
data class PaymentCalcMonthPR(
    val monthTitle: String,
    val monthNumberLabel: String,
    val daysLabel: String,
    val baseWageRaw: Long,
    val ratePercent: Int?,
    val lines: ImmutableList<PaymentCalcLinePR>,
    val netAmountRaw: Long,
)
