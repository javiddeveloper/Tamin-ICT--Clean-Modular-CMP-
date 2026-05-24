package com.tamin.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("status")val status: Int? = null,
    @SerialName("error")val error: ErrorDTO? = null
)
