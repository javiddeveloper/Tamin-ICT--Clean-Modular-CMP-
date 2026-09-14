package com.tamin.taminhamrah.model.pension.disabilityRequest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DisabilityRequestRefDTO(
    @SerialName("id") val id: Long? = null,
    @SerialName("refCode") val refCode: String? = null,
)
