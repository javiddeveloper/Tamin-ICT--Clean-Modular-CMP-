package com.tamin.taminhamrah.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyMobileReq(
    @SerialName("mobileNumber") var mobile: String = "",
    @SerialName("otp") var otp: String = "",
    @SerialName("hash") var otpHashCode: String = ""
)
