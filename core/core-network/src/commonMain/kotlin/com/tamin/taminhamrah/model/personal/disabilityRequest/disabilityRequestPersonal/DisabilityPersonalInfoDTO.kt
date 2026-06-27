package com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DisabilityPersonalInfoDTO(
    @SerialName("branch") val branch: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("confirmed") val confirmed: Boolean? = false,
    @SerialName("insuranceId") val insuranceId: String? = null,
    @SerialName("mobileNumber") val mobileNumber: String? = null,
    @SerialName("personal") val personal: PersonalDTO? = null,
    @SerialName("provinceName") val provinceName: String? = null,
    @SerialName("work") val work: WorkDTO? = null,
    @SerialName("yearsAge") val yearsAge: String? = null,
    @SerialName("monthsAge") val monthsAge: String? = null,
    @SerialName("daysAge") val daysAge: String? = null,
    @SerialName("strAge") val strAge: String? = null,
)
