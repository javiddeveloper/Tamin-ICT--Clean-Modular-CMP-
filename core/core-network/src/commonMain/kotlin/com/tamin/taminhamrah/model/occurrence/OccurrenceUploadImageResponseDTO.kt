package com.tamin.taminhamrah.model.occurrence

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OccurrenceUploadImageResponseDTO(
    @SerialName("guid") val guid: String?,
)
