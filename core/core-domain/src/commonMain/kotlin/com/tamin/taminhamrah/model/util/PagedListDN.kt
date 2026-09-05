package com.tamin.taminhamrah.model.util

/**
 * One page of a list endpoint, with the server's grand total so a caller can tell whether more
 * pages exist without asking for one that turns out empty.
 *
 * Replaces the per-type `XListDN` wrappers: they all carried exactly these two fields.
 */
data class PagedListDN<T>(
    val items: List<T> = emptyList(),
    val total: Int = 0,
) {
    /** True while [items] gathered so far is short of [total] — i.e. another page is worth asking for. */
    fun hasMoreAfter(loaded: Int): Boolean = loaded < total
}
