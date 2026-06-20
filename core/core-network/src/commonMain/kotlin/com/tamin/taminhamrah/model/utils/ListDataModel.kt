package com.tamin.taminhamrah.model.utils

import kotlinx.serialization.Serializable

@Serializable
data class ListDataModel<T>(
    val data: ListData<T>? = null
)

@Serializable
data class ListData<T>(
    val total: Int = 0,
    val list: List<T>? = null
)
