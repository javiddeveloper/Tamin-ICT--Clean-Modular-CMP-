package com.tamin.taminhamrah.feature.workshops.ui.model

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.WORKSHOP_PAGE_SIZE
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * The list half of every screen under کارگاه‌های کارفرما.
 *
 * All eight of them page the same way and show the same four states, so the shape is declared once
 * and each screen's own state holds one of these plus whatever is particular to it. Being a
 * separate `@Immutable` value also means a row list changing does not invalidate the fields around
 * it — the search text, the open sheet — and vice versa.
 */
@Immutable
data class PagedListState<T>(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val items: ImmutableList<T> = persistentListOf(),
    val hasMore: Boolean = false,
) {
    /** Nothing has arrived yet — the skeleton stands in for the list. */
    val isFirstLoad: Boolean get() = isLoading && items.isEmpty()

    /** The service answered, and answered with nothing. */
    val isEmpty: Boolean get() = !isLoading && error == null && items.isEmpty()

    /** Which page to ask for next, derived from what is already loaded. */
    val nextPage: Int get() = items.size / WORKSHOP_PAGE_SIZE

    /** True only when another page is worth asking for and none is already in flight. */
    val canLoadMore: Boolean get() = hasMore && !isLoading && !isLoadingMore

    fun loading(): PagedListState<T> = copy(isLoading = true, isLoadingMore = false, error = null)

    fun loadingMore(): PagedListState<T> = copy(isLoadingMore = true, error = null)

    fun failed(message: String?): PagedListState<T> =
        copy(isLoading = false, isLoadingMore = false, error = message)

    /**
     * Folds one page in: page 0 replaces, later pages append, so reaching the end of the list never
     * flickers back to a skeleton.
     */
    fun <D> loaded(page: PagedListDN<D>, isFirstPage: Boolean, map: (D) -> T): PagedListState<T> {
        val merged = if (isFirstPage) {
            page.items.map(map).toImmutableList()
        } else {
            (items + page.items.map(map)).toImmutableList()
        }
        return copy(
            isLoading = false,
            isLoadingMore = false,
            error = null,
            items = merged,
            // Two conditions, because a service that misreports its total would otherwise page
            // forever: the total must be short of what is loaded *and* the last page must have
            // been full.
            hasMore = page.hasMoreAfter(merged.size),
        )
    }
}
