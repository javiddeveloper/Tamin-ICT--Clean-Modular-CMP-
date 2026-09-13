package com.tamin.taminhamrah.model.contractAffair

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Body of `special-insured-services/freelance-calc-debit/{month}` and its اختیاری twin
 * `special-insured-services/calc-debit/{month}` — محاسبهٔ حق بیمه for a chosen number of months.
 *
 * Mirrors `old_android`'s `CalculateFreelanceDebit`.
 */
@Serializable
data class ContractDebitDTO(
    @SerialName("total") val total: Long? = null,
    @SerialName("insurancePremiums") val insurancePremiums: Long? = null,
    @SerialName("previousDebit") val previousDebit: Long? = null,
    @SerialName("startDate") val startDate: Long? = null,
    @SerialName("endDate") val endDate: Long? = null,
    @SerialName("PayPremiumDate") val payPremiumDate: String? = null,
    @SerialName("messageInformation") val messageInformation: String? = null,
)

/**
 * Body of `special-insured-services/freelance-get-last-payment` — the last date حق بیمه was paid
 * for a حرف و مشاغل آزاد contract. Mirrors `old_android`'s `FreelanceLastPayment`.
 *
 * The اختیاری endpoint `special-insured-services/get-last-payment` instead answers with a bare
 * timestamp in `data`, lifted into [ContractLastPaymentDTO] by the remote data source.
 */
@Serializable
data class FreelanceLastPaymentDTO(
    @SerialName("chekReloLap") val chekReloLap: String? = null,
    @SerialName("lastPaymentDate") val lastPaymentDate: String? = null,
    @SerialName("medicalRsltResend") val medicalRsltResend: String? = null,
)

/**
 * Unified last-payment shape the remote data source produces for both contract kinds:
 * the object for freelance, `{ lastPaymentTimestamp }` only for اختیاری.
 */
data class ContractLastPaymentDTO(
    val lastPaymentTimestamp: String? = null,
    val chekReloLap: String? = null,
    val medicalRsltResend: String? = null,
)

/**
 * One row of جزئیات برگ پرداخت, already lifted out of the positional array
 * `special-insured-services/freelance-payment-details` / `payment-details` return
 * (`[year, month, day, description, wage, amount]` — see
 * `ContractAffairRemoteDataSourceImpl.getPaymentCalculationDetails`). Mirrors `old_android`'s
 * `CalculationModel`.
 */
data class PaymentCalculationRowDTO(
    val year: String? = null,
    val month: String? = null,
    val day: String? = null,
    val description: String? = null,
    val wage: Double? = null,
    val amount: Double? = null,
)
