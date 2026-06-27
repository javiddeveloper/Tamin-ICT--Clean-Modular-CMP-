package com.tamin.taminhamrah.model.upload

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadImageResponseDTO(
    @SerialName("guid") val guid: String?,
)
