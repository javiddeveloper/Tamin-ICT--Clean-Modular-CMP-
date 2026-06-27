package com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JobDTO(
    @SerialName("jobCode") val jobCode: String? = null,
    @SerialName("jobDescription") val jobDescription: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("statusDate") val statusDate: String? = null
)
