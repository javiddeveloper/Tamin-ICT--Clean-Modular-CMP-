package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** One row of `workshop-services/payment-sheets` — a برگ پرداخت. Dates arrive as epoch millis. */
@Serializable
data class PaymentSheetDTO(
    @SerialName("orderNo") val orderNo: String? = null,
    @SerialName("orderRow") val orderRow: String? = null,
    @SerialName("payId") val payId: String? = null,
    @SerialName("mastCustomerCode") val customerCode: String? = null,
    @SerialName("mastCustomerName") val customerName: String? = null,
    @SerialName("rcntrow") val agreementRow: String? = null,
    @SerialName("debitNo") val debitNumber: String? = null,
    @SerialName("debitCreateReasonCode") val debitCreateReasonCode: String? = null,
    @SerialName("debitCreateReasonDesc") val debitCreateReasonDesc: String? = null,
    @SerialName("docDate") val docDate: Long? = null,
    @SerialName("cardDate") val cardDate: Long? = null,
    @SerialName("paySeqAmount") val amount: Long? = null,
    /** `1` باطل / `2` وصول / `3` موثر. */
    @SerialName("orpStatusCode") val statusCode: String? = null,
    @SerialName("orpStatusDesc") val statusDesc: String? = null,
    @SerialName("payKindCode") val payKindCode: String? = null,
    @SerialName("payKindDesc") val payKindDesc: String? = null,
    @SerialName("ouragGno") val documentNumber: String? = null,
    @SerialName("ouragSDate") val documentDate: String? = null,
)
