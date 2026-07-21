package com.tamin.taminhamrah.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JobTitleDTO(
    @SerialName("jobCode") val jobCode: String?,
    @SerialName("jobDescription") val jobDescription: String?,
    @SerialName("status") val status: String?,
    @SerialName("statusDate") val statusDate: String?,
)
