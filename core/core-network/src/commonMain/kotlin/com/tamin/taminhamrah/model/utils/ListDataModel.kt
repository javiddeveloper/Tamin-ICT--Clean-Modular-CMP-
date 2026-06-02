package com.tamin.taminhamrah.model.utils

import com.tamin.taminhamrah.tools.BaseResponse
import kotlinx.serialization.Serializable

@Serializable
class ListDataModel<T> {
    var data: ListData<T>? = null
}
@Serializable
class ListData<T> {
    var total: Int = 0
    var list: List<T>?=null
}
