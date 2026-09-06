package com.tamin.taminhamrah.model.contracts

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ContractPR(
    val contractNumber: String,
    val statusDesc: String,
    val isActive: Boolean,
    val requestDate: String,
    val insuranceType: String,
    val monthlyPremiumLabel: String,
    val monthlyIncome: String,
    val treatmentSupportText: String,
    val hasTreatmentSupport: Boolean,
    val jobTitle: String,
    /** Raw `premiumTypeCode` ("01"/"02"/"38"); drives which operation endpoints a card can call. */
    val premiumTypeCode: String,
    /** `contractStatusObject.selfIsuContStatDode`; `1` means the contract is currently active. */
    val statusCode: Int?,
    /** `cntFreeJobCode` — used to gate پرداخت حق بیمه / غیرفعال کردن for special job codes. */
    val freeJobCode: String,
)

/** One selectable علت خاتمه قرارداد row. [code] is sent as the cancel request path segment. */
@Immutable
data class ContractStatePR(
    val code: Int,
    val title: String,
)

/** One مشاهده پرداخت‌ها row. */
@Immutable
data class ContractPaymentHistoryItemPR(
    val debtNumber: String,
    val termRange: String,
    val totalDebt: String,
    val amountPayment: String,
    val paymentDeadline: String,
    val datePayment: String,
    val statusContract: String,
)
