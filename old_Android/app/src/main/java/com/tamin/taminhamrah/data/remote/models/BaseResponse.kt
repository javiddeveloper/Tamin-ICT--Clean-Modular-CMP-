package com.tamin.taminhamrah.data.remote.models

data class BaseResponse<T>(
    var status: Int? = 0,
    var family: String? = "",
    var reason: String? = "",
    var data: T
)
