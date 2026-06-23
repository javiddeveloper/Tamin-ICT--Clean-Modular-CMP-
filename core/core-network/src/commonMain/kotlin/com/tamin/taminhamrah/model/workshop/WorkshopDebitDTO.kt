package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkshopDebitDTO(
    @SerialName("debitNumber") val debitNumber: String?,
    @SerialName("debitCreateReasonCode") val debitCreateReasonCode: String?,
    @SerialName("debitCreateReasonDesc") val debitCreateReasonDesc: String?,
    @SerialName("debitStartDate") val debitStartDate: String?,
    @SerialName("debitEndDate") val debitEndDate: String?,
    @SerialName("debitAmount") val debitAmount: Long?,
    @SerialName("debitRemain") val debitRemain: Long?,
    @SerialName("withoutPentaltyAmount") val withoutPentaltyAmount: Long?,
    @SerialName("penaltyList") val penaltyList: Long?,
    @SerialName("penaltyPay") val penaltyPay: Long?,
    @SerialName("sum") val sum: Long?,
    @SerialName("nimOshr") val nimOshr: Long?,
    @SerialName("debitStepDesc") val debitStepDesc: String?,
    @SerialName("debitStatDesc") val debitStatDesc: String?,
    @SerialName("debitStepCode") val debitStepCode: String?,
    @SerialName("debitStatCode") val debitStatCode: String?,
    @SerialName("mastCustomerTypeCode") val mastCustomerTypeCode: String?,
    @SerialName("mastCustomerCode") val mastCustomerCode: String?,
    @SerialName("peymanSequence") val peymanSequence: String?,
    @SerialName("debitCreateDate") val debitCreateDate: String?,
    @SerialName("cludatCode") val cludatCode: String?,
    @SerialName("cludatDesc") val cludatDesc: String?,
    @SerialName("nimOshrKol") val nimOshrKol: Long?,
    @SerialName("docDate") val docDate: String?,
    @SerialName("stepCat") val stepCat: String?,
)
