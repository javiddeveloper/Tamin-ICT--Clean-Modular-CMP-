package com.tamin.taminhamrah.model.pension

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PensionInquiryDTO (
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("insuranceNumber") val insuranceNumber: String? = null,
    @SerialName("pensionerRisuid") val pensionerRisUid: String? = null,
    @SerialName("pensionerType") val pensionerType: String? = null,
    @SerialName("paymentDate") val paymentDate: String? = null,
    @SerialName("pensionerBaseDate") val pensionerBaseDate: String? = null,
    @SerialName("fullName") val fullName: String? = null,
    @SerialName("statusDesc") val statusDesc: String? = null,
    @SerialName("sexDesc") val sexDesc: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("pensionEndDate") val pensionEndDate: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("paymentAmount") val paymentAmount: Int? = null
)
