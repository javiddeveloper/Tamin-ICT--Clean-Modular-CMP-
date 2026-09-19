package com.tamin.taminhamrah.model.contractAffair

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Body of `special-insured-services/update-self-contract-state/{stateCode}` and its
 * `freelance-` variant — غیرفعال کردن قرارداد.
 */
@Serializable
data class CancelContractRequestDTO(
    @SerialName("canceldesc") val canceldesc: String? = null,
    @SerialName("contractStatus") val contractStatus: Int? = null,
)

/**
 * One row of `special-insured-services/list-self-contract-state` — a selectable
 * contract-termination reason (علت خاتمه قرارداد).
 */
@Serializable
data class ContractStateDTO(
    @SerialName("selfIsuContStatDesc") val selfIsuContStatDesc: String? = null,
    @SerialName("selfIsuContStatDode") val selfIsuContStatCode: Int? = null,
)

/**
 * One مشاهده پرداخت‌ها row, already lifted out of the positional array the
 * `freelance-payment-history-head-with-contractNumber` endpoint returns (see
 * `ContractAffairRemoteDataSourceImpl.getContractPaymentHistory`).
 */
@Serializable
data class ContractPaymentHistoryItemDTO(
    @SerialName("nationalId")
    val nationalId: String? = null,
    @SerialName("insuranceId")
    val insuranceId: String? = null,
    @SerialName("debtNumber")
    val debtNumber: String? = null,
    @SerialName("startTermPayment")
    val startTermPayment: String? = null,
    @SerialName("endTermPayment")
    val endTermPayment: String? = null,
    @SerialName("totalDebt")
    val totalDebt: Double? = null,
    @SerialName("paymentDeadline")
    val paymentDeadline: String? = null,
    @SerialName("amountPayment")
    val amountPayment: Double? = null,
    @SerialName("datePayment")
    val datePayment: String? = null,
    @SerialName("statusContract")
    val statusContract: String? = null,
    @SerialName("statusRecipient")
    val statusRecipient: String? = null,
)
