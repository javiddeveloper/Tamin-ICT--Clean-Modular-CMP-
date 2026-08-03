package com.tamin.taminhamrah.feature.profile.ui.electronicFile.model

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ElectronicFilePagingSourceTest {

    private fun document(id: String) = ElectronicFileDN(id = id, name = "سند $id", thumb = "t/$id")

    private fun sourceWith(pages: List<List<ElectronicFileDN>>) =
        ElectronicFilePagingSource { page, _ -> pages.getOrElse(page) { emptyList() } }

    @Test
    fun `the first page has no previous key and points at the next`() = runTest {
        val source = sourceWith(listOf(List(10) { document("$it") }, listOf(document("x"))))

        val result = source.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
        )

        val page = result as PagingSource.LoadResult.Page
        assertEquals(10, page.data.size)
        assertEquals(null, page.prevKey)
        assertEquals(1, page.nextKey)
    }

    @Test
    fun `a short page is the last one`() = runTest {
        val source = sourceWith(listOf(List(3) { document("$it") }))

        val result = source.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
        )

        assertEquals(null, (result as PagingSource.LoadResult.Page).nextKey)
    }

    @Test
    fun `a later page keeps a previous key`() = runTest {
        val source = sourceWith(listOf(List(10) { document("a$it") }, List(10) { document("b$it") }))

        val result = source.load(
            PagingSource.LoadParams.Append(key = 1, loadSize = 10, placeholdersEnabled = false),
        )

        val page = result as PagingSource.LoadResult.Page
        assertEquals("سند b0", page.data.first().name)
        assertEquals(0, page.prevKey)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun `a failure surfaces as an error rather than an empty page`() = runTest {
        val source = ElectronicFilePagingSource { _, _ -> error("the server said no") }

        val result = source.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false),
        )

        assertTrue(result is PagingSource.LoadResult.Error)
    }

    @Test
    fun `refreshing returns to the page around the anchor`() {
        val source = sourceWith(emptyList())
        val state = PagingState<Int, com.tamin.taminhamrah.model.erecords.ElectronicFilePR>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0,
        )

        assertEquals(null, source.getRefreshKey(state))
    }
}
