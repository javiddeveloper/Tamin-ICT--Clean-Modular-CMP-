package com.tamin.taminhamrah.model.personal.deceasedInfo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeceasedInfoDTO (
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("deadDate") val deadDate: String? = null,
    @SerialName("insuranceId") val insuranceId: String? = null,
    @SerialName("pensionerId") val pensionerId: String? = null,
    @SerialName("personal") val personal: PersonalDTO? = null,
    @SerialName("yearsAge") val yearsAge: String? = null,
    @SerialName("monthsAge") val monthsAge: String? = null,
    @SerialName("daysAge") val daysAge: String? = null,
    @SerialName("related") val related : String? = null
)
