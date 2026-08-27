package com.tamin.taminhamrah.model.paging

data class PageDN<out T>(
    val items: List<T>,
    val total: Int? = null,
)
