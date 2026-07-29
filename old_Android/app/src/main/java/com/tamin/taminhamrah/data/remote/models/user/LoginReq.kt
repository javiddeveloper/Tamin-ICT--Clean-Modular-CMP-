package com.tamin.taminhamrah.data.remote.models.user

data class LoginReq(
    var username: String? = null,
    var password: String? = null,
    var key: String? = null,
    var appId: String? = null,
    var code: String? = null

)
