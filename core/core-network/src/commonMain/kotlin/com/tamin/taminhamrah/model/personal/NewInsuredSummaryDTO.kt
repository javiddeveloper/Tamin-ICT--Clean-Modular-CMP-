package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewInsuredSummaryDTO(
    @SerialName("refCode") var refCode: String?,
    @SerialName("nationalId") var nationalId: String?,
    @SerialName("firstName") var firstName: String?,
    @SerialName("lastName") var lastName: String?,
    @SerialName("relationDescription") var relationDescription: String?,
    @SerialName("jobDescription") var jobDescription: String?
)
