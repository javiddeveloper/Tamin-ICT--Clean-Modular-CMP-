package com.tamin.taminhamrah.paging

import app.cash.turbine.test
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class PaginatorTest {

    private val config = PaginationConfig(pageSize = PAGE_SIZE, firstPage = 1)

    @Test
    fun `loadNext requests the first page and exposes its items`() = runTest {
        val loader = FakePageLoader(pages = mapOf(1 to page("a", "b")))
        val paginator = paginatorOf(loader)

        paginator.loadNext()

        assertEquals(listOf("a", "b"), paginator.state.value.items)
        assertEquals(1, loader.requestedQueries.single().page)
        assertEquals(0, loader.requestedQueries.single().start)
        assertEquals(PAGE_SIZE, loader.requestedQueries.single().limit)
    }

    @Test
    fun `loadNext appends the following page and advances the offset`() = runTest {
        val loader = FakePageLoader(pages = mapOf(1 to page("a", "b"), 2 to page("c", "d")))
        val paginator = paginatorOf(loader)

        paginator.loadNext()
        paginator.loadNext()

        assertEquals(listOf("a", "b", "c", "d"), paginator.state.value.items)
        assertEquals(listOf(1, 2), loader.requestedQueries.map { it.page })
        assertEquals(listOf(0, PAGE_SIZE), loader.requestedQueries.map { it.start })
    }

    @Test
    fun `start offset follows a one-based firstPage`() = runTest {
        val loader = FakePageLoader(pages = mapOf(1 to page("a", "b"), 2 to page("c", "d")))
        val paginator = Paginator(
            config = PaginationConfig(pageSize = PAGE_SIZE, firstPage = 1),
            loadPage = loader::load,
        )

        paginator.loadNext()
        paginator.loadNext()

        assertEquals(listOf(1, 2), loader.requestedQueries.map { it.page })
        assertEquals(listOf(0, PAGE_SIZE), loader.requestedQueries.map { it.start })
    }

    @Test
    fun `default config reproduces the live endpoint paging sequence`() = runTest {
        val defaultPageSize = PaginationConfig().pageSize
        val fullPage = PageDN(List(defaultPageSize) { "item$it" }, total = defaultPageSize * 5)
        val loader = FakePageLoader(pages = mapOf(0 to fullPage, 1 to fullPage, 2 to fullPage))
        val paginator = Paginator(loadPage = loader::load)

        paginator.loadNext()
        paginator.loadNext()
        paginator.loadNext()

        assertEquals(listOf(0, 1, 2), loader.requestedQueries.map { it.page })
        assertEquals(
            listOf(0, defaultPageSize, defaultPageSize * 2),
            loader.requestedQueries.map { it.start },
        )
        assertEquals(List(3) { defaultPageSize }, loader.requestedQueries.map { it.limit })
    }

    @Test
    fun `filters on the base query survive paging`() = runTest {
        val filter = ApiFilterDN(FilterProperty.STATUS_CODE, "1", FilterOperator.EQUAL)
        val loader = FakePageLoader(pages = mapOf(1 to page("a", "b"), 2 to page("c", "d")))
        val paginator = Paginator(
            baseQuery = ApiQueryParamDN(filters = listOf(filter)),
            config = config,
            loadPage = loader::load,
        )

        paginator.loadNext()
        paginator.loadNext()

        assertTrue(loader.requestedQueries.all { it.filters == listOf(filter) })
    }

    @Test
    fun `end is reached once the reported total is covered`() = runTest {
        val loader = FakePageLoader(
            pages = mapOf(
                1 to PageDN(listOf("a", "b"), total = 4),
                2 to PageDN(listOf("c", "d"), total = 4),
            ),
        )
        val paginator = paginatorOf(loader)

        paginator.loadNext()
        assertFalse(paginator.state.value.endReached)

        paginator.loadNext()
        assertTrue(paginator.state.value.endReached)
    }

    @Test
    fun `end is reached on a short page when no total is reported`() = runTest {
        val loader = FakePageLoader(pages = mapOf(1 to page("a")))
        val paginator = paginatorOf(loader)

        paginator.loadNext()

        assertTrue(paginator.state.value.endReached)
    }

    @Test
    fun `loadNext is ignored once the end is reached`() = runTest {
        val loader = FakePageLoader(pages = mapOf(1 to page("a")))
        val paginator = paginatorOf(loader)

        paginator.loadNext()
        paginator.loadNext()
        paginator.loadNext()

        assertEquals(1, loader.requestedQueries.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `concurrent loadNext calls issue a single request`() = runTest {
        val gate = CompletableDeferred<Unit>()
        val loader = FakePageLoader(pages = mapOf(1 to page("a", "b")), gate = gate)
        val paginator = paginatorOf(loader)

        repeat(5) { launch { paginator.loadNext() } }
        runCurrent()

        assertEquals(1, loader.requestedQueries.size)
        assertTrue(paginator.state.value.isLoadingFirstPage)

        gate.complete(Unit)
        advanceUntilIdle()

        assertEquals(listOf("a", "b"), paginator.state.value.items)
        assertEquals(1, loader.requestedQueries.size)
    }

    @Test
    fun `a failing page surfaces the error and does not advance`() = runTest {
        val failure = IllegalStateException("boom")
        val loader = FakePageLoader(pages = mapOf(1 to page("a", "b")), failWith = failure)
        val paginator = paginatorOf(loader)

        paginator.loadNext()

        assertSame(failure, paginator.state.value.error)
        assertTrue(paginator.state.value.items.isEmpty())
        assertFalse(paginator.state.value.isLoadingFirstPage)
        assertFalse(paginator.state.value.isLoadingNextPage)
    }

    @Test
    fun `loadNext is ignored while an error is pending`() = runTest {
        val loader = FakePageLoader(
            pages = mapOf(1 to page("a", "b")),
            failWith = RuntimeException("boom"),
        )
        val paginator = paginatorOf(loader)

        paginator.loadNext()
        paginator.loadNext()

        assertEquals(1, loader.requestedQueries.size)
    }

    @Test
    fun `retry re-attempts the page that failed`() = runTest {
        val loader = FakePageLoader(
            pages = mapOf(1 to page("a", "b")),
            failWith = RuntimeException("boom"),
        )
        val paginator = paginatorOf(loader)

        paginator.loadNext()
        loader.failWith = null
        paginator.retry()

        assertNull(paginator.state.value.error)
        assertEquals(listOf("a", "b"), paginator.state.value.items)
        assertEquals(listOf(1, 1), loader.requestedQueries.map { it.page })
    }

    @Test
    fun `retry after a failed second page keeps the pages already loaded`() = runTest {
        val loader = FakePageLoader(pages = mapOf(1 to page("a", "b"), 2 to page("c", "d")))
        val paginator = paginatorOf(loader)

        paginator.loadNext()
        loader.failWith = RuntimeException("boom")
        paginator.loadNext()

        assertEquals(listOf("a", "b"), paginator.state.value.items)

        loader.failWith = null
        paginator.retry()

        assertEquals(listOf("a", "b", "c", "d"), paginator.state.value.items)
        assertEquals(listOf(1, 2, 2), loader.requestedQueries.map { it.page })
    }

    @Test
    fun `retry is a no-op when nothing failed`() = runTest {
        val loader = FakePageLoader(pages = mapOf(1 to page("a", "b")))
        val paginator = paginatorOf(loader)

        paginator.loadNext()
        paginator.retry()

        assertEquals(1, loader.requestedQueries.size)
    }

    @Test
    fun `refresh restarts from the first page and replaces the items`() = runTest {
        val loader = FakePageLoader(pages = mapOf(1 to page("a", "b"), 2 to page("c", "d")))
        val paginator = paginatorOf(loader)

        paginator.loadNext()
        paginator.loadNext()
        loader.pages = mapOf(1 to page("x", "y"))
        paginator.refresh()

        assertEquals(listOf("x", "y"), paginator.state.value.items)
        assertEquals(listOf(1, 2, 1), loader.requestedQueries.map { it.page })
    }

    @Test
    fun `refresh clears a reached end`() = runTest {
        val loader = FakePageLoader(pages = mapOf(1 to page("a")))
        val paginator = paginatorOf(loader)

        paginator.loadNext()
        assertTrue(paginator.state.value.endReached)

        loader.pages = mapOf(1 to page("a", "b"))
        paginator.refresh()

        assertFalse(paginator.state.value.endReached)
        assertNull(paginator.state.value.error)
    }

    @Test
    fun `refresh recovers from a failed first page`() = runTest {
        val loader = FakePageLoader(
            pages = mapOf(1 to page("a", "b")),
            failWith = RuntimeException("boom"),
        )
        val paginator = paginatorOf(loader)

        paginator.loadNext()
        assertTrue(paginator.state.value.error != null)

        loader.failWith = null
        paginator.refresh()

        assertNull(paginator.state.value.error)
        assertEquals(listOf("a", "b"), paginator.state.value.items)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `refresh supersedes a page that is still in flight`() = runTest {
        val gate = CompletableDeferred<Unit>()
        val loader = FakePageLoader(pages = mapOf(1 to page("stale", "stale2")), gate = gate)
        val paginator = paginatorOf(loader)

        launch { paginator.loadNext() }
        runCurrent()
        assertEquals(1, loader.requestedQueries.size)

        loader.gate = null
        loader.pages = mapOf(1 to page("fresh", "fresh2"))
        paginator.refresh()

        // The superseded request only completes after the refresh has already settled.
        gate.complete(Unit)
        advanceUntilIdle()

        assertEquals(listOf("fresh", "fresh2"), paginator.state.value.items)
        assertFalse(paginator.state.value.isLoadingFirstPage)
    }

    @Test
    fun `refresh can swap the base query`() = runTest {
        val filter = ApiFilterDN(FilterProperty.STATUS_CODE, "9", FilterOperator.EQUAL)
        val loader = FakePageLoader(pages = mapOf(1 to page("a", "b")))
        val paginator = paginatorOf(loader)

        paginator.loadNext()
        paginator.refresh(ApiQueryParamDN(filters = listOf(filter)))

        assertEquals(emptyList(), loader.requestedQueries.first().filters)
        assertEquals(listOf(filter), loader.requestedQueries.last().filters)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `state stream reports loading before the loaded page`() = runTest {
        val gate = CompletableDeferred<Unit>()
        val loader = FakePageLoader(pages = mapOf(1 to page("a", "b")), gate = gate)
        val paginator = paginatorOf(loader)

        paginator.state.test {
            assertEquals(PaginationState(), awaitItem())

            launch { paginator.loadNext() }
            runCurrent()

            val loading = awaitItem()
            assertTrue(loading.isLoadingFirstPage)
            assertFalse(loading.isLoadingNextPage)

            gate.complete(Unit)
            advanceUntilIdle()

            val loaded = awaitItem()
            assertEquals(listOf("a", "b"), loaded.items)
            assertFalse(loaded.isLoadingFirstPage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `appending a page reports the footer flag not the full screen one`() = runTest {
        val gate = CompletableDeferred<Unit>()
        val loader = FakePageLoader(pages = mapOf(1 to page("a", "b"), 2 to page("c", "d")))
        val paginator = paginatorOf(loader)

        paginator.loadNext()
        loader.gate = gate

        launch { paginator.loadNext() }
        runCurrent()

        assertTrue(paginator.state.value.isLoadingNextPage)
        assertFalse(paginator.state.value.isLoadingFirstPage)

        gate.complete(Unit)
        advanceUntilIdle()
    }

    @Test
    fun `an empty first page reports an empty exhausted list`() = runTest {
        val loader = FakePageLoader(pages = mapOf(1 to PageDN(emptyList(), total = 0)))
        val paginator = paginatorOf(loader)

        paginator.loadNext()

        assertTrue(paginator.state.value.isEmpty)
        assertTrue(paginator.state.value.endReached)
    }

    private fun paginatorOf(loader: FakePageLoader) =
        Paginator(config = config, loadPage = loader::load)

    private fun page(vararg items: String) = PageDN(items.toList())

    private class FakePageLoader(
        var pages: Map<Int, PageDN<String>>,
        var gate: CompletableDeferred<Unit>? = null,
        var failWith: Throwable? = null,
    ) {
        val requestedQueries = mutableListOf<ApiQueryParamDN>()

        suspend fun load(query: ApiQueryParamDN): PageDN<String> {
            requestedQueries += query
            gate?.await()
            failWith?.let { throw it }
            return pages[query.page] ?: PageDN(emptyList())
        }
    }

    private companion object {
        const val PAGE_SIZE = 2
    }
}
