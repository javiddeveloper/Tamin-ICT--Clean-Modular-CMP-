package com.tamin.taminhamrah.feature.workshops.ui.model

import com.tamin.taminhamrah.model.util.PagedListDN
import kotlinx.collections.immutable.toImmutableList
import com.tamin.taminhamrah.model.workshop.WORKSHOP_PAGE_SIZE
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The paging rules every کارگاه screen shares.
 *
 * All eight list screens fold their pages through this one type, so a mistake here is a mistake
 * eight times over — and the two failures it is written to prevent (a list that pages forever, a
 * loaded list that flickers back to a skeleton) are invisible until a service misreports itself.
 */
class PagedListStateTest {

    private fun page(items: Int, total: Int) =
        PagedListDN(items = List(items) { "row-$it" }, total = total)

    @Test
    fun `first page replaces whatever was there`() {
        val state = PagedListState(items = listOf("stale").toImmutable())
            .loaded(page(items = 2, total = 2), isFirstPage = true) { it }

        assertEquals(listOf("row-0", "row-1"), state.items)
    }

    @Test
    fun `later pages append rather than replace`() {
        val first = PagedListState<String>()
            .loaded(page(items = WORKSHOP_PAGE_SIZE, total = 12), isFirstPage = true) { it }
        val second = first.loaded(page(items = 2, total = 12), isFirstPage = false) { it }

        assertEquals(WORKSHOP_PAGE_SIZE + 2, second.items.size)
        assertEquals("row-0", second.items.first())
    }

    @Test
    fun `a short page ends the list even when the total claims more`() {
        // The service reporting total=99 while handing back a part-full page is exactly how the
        // old client paged forever.
        val state = PagedListState<String>()
            .loaded(page(items = 3, total = 99), isFirstPage = true) { it }

        assertFalse(state.hasMore)
        assertFalse(state.canLoadMore)
    }

    @Test
    fun `a full page with more to come can load more`() {
        val state = PagedListState<String>()
            .loaded(page(items = WORKSHOP_PAGE_SIZE, total = 40), isFirstPage = true) { it }

        assertTrue(state.hasMore)
        assertTrue(state.canLoadMore)
    }

    @Test
    fun `a full page that completes the total does not ask again`() {
        val state = PagedListState<String>()
            .loaded(page(items = WORKSHOP_PAGE_SIZE, total = WORKSHOP_PAGE_SIZE), isFirstPage = true) { it }

        assertFalse(state.hasMore)
    }

    @Test
    fun `next page is derived from what is already loaded`() {
        val empty = PagedListState<String>()
        assertEquals(0, empty.nextPage)

        val onePage = empty.loaded(page(items = WORKSHOP_PAGE_SIZE, total = 40), isFirstPage = true) { it }
        assertEquals(1, onePage.nextPage)

        val twoPages = onePage.loaded(page(items = WORKSHOP_PAGE_SIZE, total = 40), isFirstPage = false) { it }
        assertEquals(2, twoPages.nextPage)
    }

    @Test
    fun `no second request while one is already in flight`() {
        val loaded = PagedListState<String>()
            .loaded(page(items = WORKSHOP_PAGE_SIZE, total = 40), isFirstPage = true) { it }

        assertFalse(loaded.loading().canLoadMore)
        assertFalse(loaded.loadingMore().canLoadMore)
    }

    @Test
    fun `loading clears a previous failure`() {
        val failed = PagedListState<String>().failed("قطع ارتباط")
        assertEquals("قطع ارتباط", failed.error)

        assertNull(failed.loading().error)
        assertNull(failed.loadingMore().error)
    }

    @Test
    fun `failing stops both spinners`() {
        val failed = PagedListState<String>().loading().failed("خطا")

        assertFalse(failed.isLoading)
        assertFalse(failed.isLoadingMore)
        assertEquals("خطا", failed.error)
    }

    @Test
    fun `the skeleton stands in only for the very first load`() {
        val firstLoad = PagedListState<String>().loading()
        assertTrue(firstLoad.isFirstLoad)

        val reloadingWithRows = PagedListState<String>()
            .loaded(page(items = 2, total = 2), isFirstPage = true) { it }
            .loading()
        assertFalse(reloadingWithRows.isFirstLoad)
    }

    @Test
    fun `empty means answered, not pending or broken`() {
        assertFalse(PagedListState<String>().loading().isEmpty)
        assertFalse(PagedListState<String>().failed("خطا").isEmpty)
        assertTrue(
            PagedListState<String>().loaded(page(items = 0, total = 0), isFirstPage = true) { it }.isEmpty,
        )
    }

    @Test
    fun `a reload that comes back empty clears the rows it had`() {
        val state = PagedListState<String>()
            .loaded(page(items = 3, total = 3), isFirstPage = true) { it }
            .loaded(page(items = 0, total = 0), isFirstPage = true) { it }

        assertTrue(state.items.isEmpty())
        assertTrue(state.isEmpty)
    }
}

private fun <T> List<T>.toImmutable() = toImmutableList()
