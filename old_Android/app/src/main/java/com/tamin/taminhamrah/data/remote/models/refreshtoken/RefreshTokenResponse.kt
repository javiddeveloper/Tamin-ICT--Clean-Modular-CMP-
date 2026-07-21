package com.tamin.taminhamrah.data.remote.models.refreshtoken

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(

    @SerializedName("access_token")
    val accessToken: String = "",

    @SerializedName("refresh_token")
    val refreshToken: String = "",

    @SerializedName("token_type")
    val tokenType: String = "",

    @SerializedName("expires_in")
    val expiresIn: Long = 0
)