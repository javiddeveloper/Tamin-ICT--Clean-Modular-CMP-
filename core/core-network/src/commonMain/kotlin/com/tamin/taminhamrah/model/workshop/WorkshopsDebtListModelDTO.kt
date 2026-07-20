package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkshopsDebtListModelDTO(
    @SerialName("bikariAmount") val indebtednessAmount: Int?,
    @SerialName("bimehAmount") val insuranceAmount: Int?,
    @SerialName("debitAmount") val debitAmount: Int?,
    @SerialName("debitCreateReasonCode") val debitCreateReasonCode: String?,
    @SerialName("debitEndDate") val debitEndDate: String?,
    @SerialName("debitNumber") val debitNumber: String?,
    @SerialName("debitRemain") val debitRemain: Int?,
    @SerialName("debitStartDate") val debitStartDate: String?,
    @SerialName("status") val status: String?
)
