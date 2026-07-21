package com.tamin.taminhamrah.data.remote.models.appUpdate

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class AppUpdateStatusResponse(
    var data: LoginModel? = null
) : BaseResponseNew() {

    inner class LoginModel(
        @SerializedName("access_token")
        var accessToken: String = "",

        @SerializedName("expires_in")
        var expiresIn: Int = 0
    )
}

//fun LoginResponse.asDomainModel(): LoginResponse {
//    return LoginResponse(
//        accessToken = this.data?.accessToken,
//        expiresIn = this.data?.expiresIn
//    )
//}
