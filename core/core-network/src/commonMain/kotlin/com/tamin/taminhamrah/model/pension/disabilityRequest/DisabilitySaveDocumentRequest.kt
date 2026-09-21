package com.tamin.taminhamrah.model.pension.disabilityRequest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DisabilitySaveDocumentRequest(
    @SerialName("pensionRequestDocList") val pensionRequestDocList: List<DisabilityDocumentDTO>? = null,
    @SerialName("status") val status: String? = null,
)
