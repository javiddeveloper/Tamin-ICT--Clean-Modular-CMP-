package com.tamin.taminhamrah.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorDTO(
    @SerialName("status") val status: Int? = null,
    @SerialName("error") val error: ErrorDataDTO? = null
)
