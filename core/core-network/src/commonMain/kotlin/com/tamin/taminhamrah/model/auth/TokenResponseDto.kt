package com.tamin.taminhamrah.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponseDto(
    @SerialName("access_token") val accessToken: String? = null,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("token_type") val tokenType: String? = null,
    @SerialName("expires_in") val expiresIn: Long? = null,
    // Standard OAuth2 error response shape (e.g. {"error":"invalid_client","error_description":
    // "..."}). expectSuccess = false on this client means a non-2xx body is still decoded into
    // this same DTO rather than throwing, so these are what let the debug login screen show
    // *why* a login failed instead of just a null accessToken.
    @SerialName("error") val error: String? = null,
    @SerialName("error_description") val errorDescription: String? = null,
)

