package com.tamin.taminhamrah.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditMobileResponseDto(
    @SerialName("traceId") val traceId: String? = null,
    @SerialName("data") val data: EditMobileDto? = null
)

@Serializable
data class EditMobileDto(
    @SerialName("hash") val hash: String? = null,
    @SerialName("expirationTime") val expirationTime: Long? = null
)
