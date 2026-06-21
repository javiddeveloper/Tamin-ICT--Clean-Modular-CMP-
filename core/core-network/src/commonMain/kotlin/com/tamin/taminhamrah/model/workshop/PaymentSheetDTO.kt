package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentSheetDTO(
    @SerialName("orderNo") val orderNo: String?,
    @SerialName("orderRow") val orderRow: String?,
    @SerialName("payId") val payId: String?,
    @SerialName("mastCustomerCode") val mastCustomerCode: String?,
    @SerialName("rcntrow") val rcntrow: String?,
    @SerialName("mastCustomerName") val mastCustomerName: String?,
    @SerialName("debitCreateReasonCode") val debitCreateReasonCode: String?,
    @SerialName("debitCreateReasonDesc") val debitCreateReasonDesc: String?,
    @SerialName("debitNo") val debitNo: String?,
    @SerialName("docDate") val docDate: Long?,
    @SerialName("paySeqAmount") val paySeqAmount: Long?,
    @SerialName("orpStatusCode") val orpStatusCode: String?,
    @SerialName("orpStatusDesc") val orpStatusDesc: String?,
    @SerialName("cardDate") val cardDate: Long?,
    @SerialName("payKindCode") val payKindCode: String?,
    @SerialName("payKindDesc") val payKindDesc: String?,
    @SerialName("ouragGno") val ouragGno: String?,
    @SerialName("ouragSDate") val ouragSDate: String?
)
