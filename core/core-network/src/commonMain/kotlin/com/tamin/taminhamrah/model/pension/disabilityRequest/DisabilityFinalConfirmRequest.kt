package com.tamin.taminhamrah.model.pension.disabilityRequest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DisabilityFinalConfirmRequest(
    @SerialName("id") val id: Long? = null,
    @SerialName("status") val status: String? = null,
)
