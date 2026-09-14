package com.tamin.taminhamrah.model.pension.disabilityRequest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DisabilitySaveInfoResponseDTO(
    @SerialName("request") val request: DisabilityRequestRefDTO? = null,
)
