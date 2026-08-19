package com.tamin.taminhamrah.model.occurrence

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OccurrenceDocTypeDTO(
    @SerialName("docTypeId") val docTypeId: String? = null,
    @SerialName("docDesc") val docDesc: String? = null,
)
