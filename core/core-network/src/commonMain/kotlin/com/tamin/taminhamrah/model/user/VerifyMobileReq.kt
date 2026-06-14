package com.tamin.taminhamrah.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyMobileReq(
    @SerialName("mobile") var mobile: String = "",
    @SerialName("otp") var otp: String = "",
    @SerialName("otpHashCode") var otpHashCode: String = ""
)
