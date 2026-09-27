package com.tamin.taminhamrah.model.paging

data class PageDN<out T>(
    val items: List<T>,
    val total: Int? = null,
    /** Served from the local cache (emitted before the network page, or instead of it offline). */
    val isFromCache: Boolean = false,
)
