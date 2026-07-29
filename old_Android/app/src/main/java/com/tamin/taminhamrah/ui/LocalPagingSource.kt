package com.tamin.taminhamrah.ui

import android.annotation.SuppressLint
import androidx.paging.PagingSource
import androidx.paging.PagingState
import timber.log.Timber

private const val INITIAL_LOAD_SIZE = 1

class LocalPagingSource<item : Any>(
    val list: List<item>
) : PagingSource<Int, item>() {
    @SuppressLint("TimberArgCount")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, item> {
        val position = params.key ?: INITIAL_LOAD_SIZE

        return try {
            LoadResult.Page(
                data = list,
                prevKey = if (position == INITIAL_LOAD_SIZE) null else position - 1,
                nextKey = null,
            )

        } catch (e: Exception) {
            Timber.tag("Paging3TestDebug").i(e, "Exception %s")
            LoadResult.Error(e)
        }
    }

    @androidx.paging.ExperimentalPagingApi
    override fun getRefreshKey(state: PagingState<Int, item>): Int {
        return 0
    }


}