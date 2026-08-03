package com.tamin.taminhamrah.feature.profile.ui.electronicFile.model

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.tamin.taminhamrah.mapper.erecords.toPresentation
import com.tamin.taminhamrah.model.erecords.ElectronicFilePR
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN

/** How many documents one request asks for. */
const val ELECTRONIC_FILE_PAGE_SIZE = 10

/**
 * Pages the person's registered documents.
 *
 * The endpoint reports no total, so the end of the list is inferred from a short page — the only
 * signal available. Mapping to [ElectronicFilePR] happens here so nothing above this ever holds a
 * domain model.
 *
 * [getPage] is a function rather than the use case itself: fetching one page is all this needs,
 * and a lambda is something a test can supply without faking a fourteen-method repository.
 */
class ElectronicFilePagingSource(
    private val getPage: suspend (page: Int, limit: Int) -> List<ElectronicFileDN>,
) : PagingSource<Int, ElectronicFilePR>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ElectronicFilePR> {
        val page = params.key ?: 0
        return try {
            val documents = getPage(page, params.loadSize).toPresentation()
            LoadResult.Page(
                data = documents,
                prevKey = if (page == 0) null else page - 1,
                // A page shorter than asked for means the server had no more to give.
                nextKey = if (documents.size < params.loadSize) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    /**
     * On refresh, come back to the page the user was looking at rather than the top — losing their
     * place after a transient failure is worse than a slightly stale page.
     */
    override fun getRefreshKey(state: PagingState<Int, ElectronicFilePR>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
}
