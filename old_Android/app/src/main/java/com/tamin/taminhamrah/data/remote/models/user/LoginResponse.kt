package com.tamin.taminhamrah.data.remote.models.user

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class LoginResponse(
    @SerializedName("access_token")
    var accessToken: String = "",

    @SerializedName("expires_in")
    var expiresIn: Long = 0,

    @SerializedName("refresh_token")
    val refreshToken: String = "",

    @SerializedName("token_type")
    val tokenType: String = ""

) : BaseResponseNew()
