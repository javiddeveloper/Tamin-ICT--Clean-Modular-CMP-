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
 * separate `@Immutable` value also means a row list changing does not invalidate the surrounding fields *  — the search text, the open sheet — and vice versa.
 */
@Immutable
data class PagedListState<T>(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val items: ImmutableList<T> = persistentListOf(),
    val hasMore: Boolean = false,
    /**
     * How many rows the service has handed over, duplicates included.
     *
     * Paging counts rows *received*, not rows shown. [items] drops duplicates, so asking for the
     * next page by the shortened length would re-request a page already loaded — and if every page
     * repeats something, it would never advance at all.
     */
    val receivedCount: Int = 0,
    /**
     * The server's own grand total, as opposed to how much of it has been paged in.
     *
     * [items] only ever holds what has arrived, so a count built from it climbs as the user
     * scrolls and reads as though the first number was wrong. The services already send this —
     * `hasMoreAfter` has always used it to decide whether another page exists — it simply was
     * never carried out of the page envelope.
     */
    val total: Int = 0,
) {
    /**
     * The count to print beside the list: [total] less the repeats [items] has already dropped.
     *
     * The service counts every row it holds, including a row identical to one already shown — the
     * same workshop under two agreements — which [loaded] keeps off the list. Printing [total] put
     * ۹ over a list of ۱. Every repeat that has arrived is known and comes off here; one still on
     * a page not yet paged in comes off when that page lands. So a list that fits one page, or has
     * been scrolled to its end, prints exactly the rows it shows, and a longer one never counts a
     * row the list has hidden. Never fewer than are shown, whatever the service reports.
     */
    val distinctTotal: Int
        get() = (total - (receivedCount - items.size).coerceAtLeast(0)).coerceAtLeast(items.size)

    /** Nothing has arrived yet — the skeleton stands in for the list. */
    val isFirstLoad: Boolean get() = isLoading && items.isEmpty()

    /** The service answered, and answered with nothing. */
    val isEmpty: Boolean get() = !isLoading && error == null && items.isEmpty()

    /**
     * The request failed and there is nothing to fall back on.
     *
     * The third state, and the one that had no branch: neither [isFirstLoad] nor [isEmpty] matches
     * it, so the list used to render zero rows — a blank page that reads as "no results" rather
     * than as a failure. A failure *with* rows already on screen is deliberately not this: the
     * earlier pages stay, and only the footer stops.
     */
    val isFailed: Boolean get() = !isLoading && error != null && items.isEmpty()

    /** Which page to ask for next, derived from what the service has sent. */
    val nextPage: Int get() = receivedCount / WORKSHOP_PAGE_SIZE

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
        val received = if (isFirstPage) page.items.size else receivedCount + page.items.size
        val rows = if (isFirstPage) page.items.map(map) else items + page.items.map(map)
        return copy(
            isLoading = false,
            isLoadingMore = false,
            error = null,
            total = page.total,
            // The same workshop reaches these lists under more than one agreement, and none of
            // them carries a field unique enough to tell the copies apart — so a row that renders
            // identically to one already shown is the same row, and only the first is kept.
            // Deduplicated across pages, not just within one, because the repeat usually arrives
            // on the page after.
            items = rows.distinct().toImmutableList(),
            receivedCount = received,
            // Two conditions, because a service that misreports its total would otherwise page
            // forever: the total must be short of what was received *and* the last page must have
            // been full.
            hasMore = page.hasMoreAfter(received) && page.items.size == WORKSHOP_PAGE_SIZE,
        )
    }
}
