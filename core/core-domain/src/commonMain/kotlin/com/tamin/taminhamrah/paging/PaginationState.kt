package com.tamin.taminhamrah.paging

data class PaginationState<out T>(
    val items: List<T> = emptyList(),
    val isLoadingFirstPage: Boolean = false,
    val isLoadingNextPage: Boolean = false,
    val endReached: Boolean = false,
    val error: Throwable? = null,
) {
    /** True once a load has settled and the collection is genuinely empty. */
    val isEmpty: Boolean
        get() = items.isEmpty() && !isLoadingFirstPage && error == null
}
