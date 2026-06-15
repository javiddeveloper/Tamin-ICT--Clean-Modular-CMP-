package com.tamin.taminhamrah.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorDataDTO(
    @SerialName("uri") val uri: String? = null,
    @SerialName("code") val code: Int? = null,
    @SerialName("description") val description: String? = null
)
