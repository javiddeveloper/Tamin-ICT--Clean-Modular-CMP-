package com.tamin.taminhamrah.data.remote.models.profile

data class VerifyMobileCodeReq(
    var userName: String="",
    var mobileNumber: String ,
    var ticket: String
)
