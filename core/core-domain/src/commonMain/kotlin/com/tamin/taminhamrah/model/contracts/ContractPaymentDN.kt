package com.tamin.taminhamrah.model.contracts

/**
 * محاسبهٔ حق بیمه result — the payable amount and period for a chosen number of months,
 * from `freelance-calc-debit/{month}` / `calc-debit/{month}`.
 */
data class ContractDebitDN(
    val total: Long?,
    val insurancePremiums: Long?,
    val previousDebit: Long?,
    val startDate: Long?,
    val endDate: Long?,
    val payPremiumDate: String?,
    val infoMessage: String?,
)

/**
 * آخرین پرداخت حق بیمه for a contract. [lastPaymentTimestamp] is `null`/`0` when nothing has been
 * paid yet. [checkReloLap] carries a backend warning message for freelance contracts (shown when it
 * is neither `"1"` nor empty, mirroring `old_android`'s `showResultLastPayment`).
 */
data class ContractLastPaymentDN(
    val lastPaymentTimestamp: Long?,
    val checkReloLap: String?,
    val medicalResultResend: String?,
)

/** One جزئیات برگ پرداخت row. Negative [amount] is a کمک دولت deduction line. */
data class PaymentCalculationRowDN(
    val year: String?,
    val month: String?,
    val day: String?,
    val description: String?,
    val wage: Double?,
    val amount: Double?,
)
