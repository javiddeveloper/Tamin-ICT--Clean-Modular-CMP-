package com.tamin.taminhamrah.model.pension.retirement

import com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal.WorkDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RetirementPersonalDTO(
    @SerialName("branch") val branch: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("insuranceId") val insuranceId: String? = null,
    @SerialName("mobileNumber") val mobileNumber: String? = null,
    @SerialName("organizationId") val organizationId: String? = null,
    @SerialName("personal") val personal: PersonalDTO? = null,
    @SerialName("provinceName") val provinceName: String? = null,
    @SerialName("work") val work: WorkDTO? = null,
    @SerialName("strAge") val strAge: String? = null,
    @SerialName("verificationResult") val verificationResult :String? = null
)
