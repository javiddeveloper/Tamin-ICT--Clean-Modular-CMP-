package com.tamin.taminhamrah.data.remote.models

abstract class ListDataModel<T> : BaseResponseNew() {
    var data: ListData<T>? = null
}

 class ListData<T> {
    var total: Int = 0
    var list: List<T>?=null
}

