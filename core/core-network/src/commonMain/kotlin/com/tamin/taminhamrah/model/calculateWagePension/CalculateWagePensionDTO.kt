package com.tamin.taminhamrah.model.calculateWagePension

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MultipleWorkshopPersonalInfoDTO(
    @SerialName("organizationId") val organizationId: String? = null,
    @SerialName("insuranceId") val insuranceId: String? = null,
    @SerialName("branch") val branch: String? = null
)

@Serializable
data class MultipleWorkshopResultDTO(
    @SerialName("result") val result: Int? = null
)
