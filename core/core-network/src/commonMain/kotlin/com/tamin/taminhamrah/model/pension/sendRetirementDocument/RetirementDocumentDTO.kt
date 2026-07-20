package com.tamin.taminhamrah.model.pension.sendRetirementDocument

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RetirementDocumentDTO(
    @SerialName("documentType") val documentType: String? = null,
    @SerialName("guid") val guid: String? = null,
)
