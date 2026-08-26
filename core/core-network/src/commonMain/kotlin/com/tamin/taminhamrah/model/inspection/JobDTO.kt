package com.tamin.taminhamrah.model.inspection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JobDTO(
    @SerialName("operation") val operation: String? = null,
    @SerialName("jobCode") val jobCode: String? = null,
    @SerialName("jobDescription") val jobDescription: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("statusDate") val statusDate: String? = null
)
