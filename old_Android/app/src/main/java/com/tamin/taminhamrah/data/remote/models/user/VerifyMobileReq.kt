package com.tamin.taminhamrah.data.remote.models.user

import com.google.gson.annotations.SerializedName

data class VerifyMobileReq(
    @SerializedName("mobileNumber")
    var mobile: String = "",
    @SerializedName("otp")
    var otp: String = "",
    @SerializedName("hash")
    var otpHashCode: String = ""
)
