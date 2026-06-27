package com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CityOfIssueDTO(
    @SerialName("code") val code: String? = null,
    @SerialName("description") val description: String? = null,
)
