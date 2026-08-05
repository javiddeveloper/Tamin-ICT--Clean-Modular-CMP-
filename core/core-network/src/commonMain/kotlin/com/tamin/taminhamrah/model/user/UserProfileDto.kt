package com.tamin.taminhamrah.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    @SerialName("entityId") val entityId: String? = null,
    @SerialName("login") val login: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("mobile") val mobile: String? = null
)

@Serializable
data class UserProfileResponseDto(
    @SerialName("data") val data: UserProfileDto?
)
