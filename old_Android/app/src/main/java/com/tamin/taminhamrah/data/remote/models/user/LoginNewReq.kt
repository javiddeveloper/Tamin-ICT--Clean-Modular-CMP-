package com.tamin.taminhamrah.data.remote.models.user

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.Constants

data class LoginNewReq(
    @SerializedName("redirect_uri")
    var redirectUrl: String="mytamin://login",
    @SerializedName("client_id")
    var clientId: String=Constants.CLIENT_ID,
    @SerializedName("grant_type")
    var grantType: String="authorization_code",
    @SerializedName("code")
    var codeFromServer: String="",
    @SerializedName("code_verifier")
    var codeVerifier: String=""
)
