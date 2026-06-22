package com.tamin.taminhamrah.model.personal.saveSurvivorInfo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PensionDocRequest(
    @SerialName("documentType") val documentType: String? = null,
    @SerialName("guid") val guid: String? = null,
)
