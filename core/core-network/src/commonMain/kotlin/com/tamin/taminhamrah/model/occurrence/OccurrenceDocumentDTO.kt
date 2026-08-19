package com.tamin.taminhamrah.model.occurrence

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OccurrenceDocumentDTO(
    @SerialName("docTypeId") val docTypeId: Int,
    @SerialName("guid") val guid: String,
)
