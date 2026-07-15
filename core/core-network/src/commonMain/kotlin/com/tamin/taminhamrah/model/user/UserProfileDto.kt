package com.tamin.taminhamrah.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    @SerialName("entityId") val entityId: String?,
    @SerialName("login") val login: String?,
    @SerialName("firstName") val firstName: String?,
    @SerialName("lastName") val lastName: String?,
    @SerialName("email") val email: String?,
    @SerialName("nationalCode") val nationalCode: String?,
    @SerialName("mobile") val mobile: String?
)

@Serializable
data class UserProfileResponseDto(
    @SerialName("data") val data: UserProfileDto?
)
