package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkshopDebtInquiryDTO(
    @SerialName("status") val status: String?,
    @SerialName("workshopId") val workshopId: String?,
    @SerialName("branchCode") val branchCode: String?,
    @SerialName("workshopName") val workshopName: String?,
    @SerialName("result") val result: String?,
    @SerialName("amount1") val amount1: String?,
    @SerialName("sDate") val sDate: String?,
    @SerialName("amount2") val amount2: String?,
    @SerialName("amount3") val amount3: String?
)
