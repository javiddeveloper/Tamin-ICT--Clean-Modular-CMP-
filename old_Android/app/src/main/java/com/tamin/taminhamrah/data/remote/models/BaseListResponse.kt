package com.tamin.taminhamrah.data.remote.models

data class BaseListResponse<T>(
    var total: Int? = 0,
    var list: List<T>? = null
)

