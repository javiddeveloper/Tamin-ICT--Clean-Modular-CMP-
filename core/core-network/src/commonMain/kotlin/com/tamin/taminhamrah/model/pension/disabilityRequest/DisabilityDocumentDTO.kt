package com.tamin.taminhamrah.model.pension.disabilityRequest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DisabilityDocumentDTO(
    @SerialName("documentType") val documentType: String? = null,
    @SerialName("guid") val guid: String? = null,
)
