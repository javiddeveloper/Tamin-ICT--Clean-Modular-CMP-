package com.tamin.taminhamrah.ui

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.Constants.PAGE
import com.tamin.taminhamrah.Constants.QUERY_PAGE_SIZE
import com.tamin.taminhamrah.Constants.START
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.MultipleLiveData
import timber.log.Timber

private var nextKey: Int? = 0
private var retryCount = 0

class GeneralPagingSource<item : Any, list : ListDataModel<item>>(
    val block: suspend (paramsMap: MutableMap<String, String>?) -> list,
    val queryPageSize: Int,
    val mldErrorState: MultipleLiveData<BaseResponseNew>,
    private var paramsMap: MutableMap<String, String>?
) : PagingSource<Int, item>() {

    override val keyReuseSupported: Boolean
        get() = true

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, item> {
        Timber.tag("pagingErrorHandeling").i("Load Called")

        var position = 0
        var offset = 0
        if (params.key != null) {
            position = params.key!!
            offset = (position * Constants.DEFAULT_QUERY_PAGE_SIZE.toInt())
        }

        Timber.tag("Pagin3TestDebug").i("position: $position")
        Timber.tag("Pagin3TestDebug").i("offset: $offset")

        return try {
            if (paramsMap == null) {
                paramsMap = mutableMapOf()
            }
            paramsMap?.apply {
                put(PAGE, position.toString())
                put(QUERY_PAGE_SIZE, queryPageSize.toString())
                put(START, offset.toString())
            }
            Timber.tag("pagingErrorHandeling").i("before call block")

            val response = block(paramsMap)
            Timber.tag("pagingErrorHandeling") .i("get Response : isSuccess=${response.isSuccess}\nstatus:${response.baseStatus?.serviceStatus}\nmessage:${response.getMessage()}\ncode:${response.getCode()}")

            val result = response.data?.list ?: emptyList()
            Timber.tag("Pagin3TestDebug").i("RESULT SIZE: ${result.size}")

            val totalSize = response.data?.total ?: -1 // -1;;fetch data has error
            Timber.tag("Pagin3TestDebug").i("TOTAL SIZE: $totalSize")

            if (totalSize == -1) {
                retryCount++
                nextKey = if (retryCount > 3)
                    null
                else
                    position

            } else {
                retryCount = 0
                nextKey = if (totalSize < (offset + queryPageSize) || totalSize <= result.size)
                    null
                else
                    position + 1

            }

            Timber.tag("Pagin3TestDebug").i("nxt key: $nextKey")

            if (!response.isSuccess) {
                Timber.tag("pagingErrorHandeling").i("send Error:mldErrorState")
                mldErrorState.postValue(response)
            }
            Timber.tag("Pagin3TestDebug").i("end**********************")

            LoadResult.Page(
                data = result,
                prevKey = if (position == 0) null else position - 1,
                nextKey = nextKey,
            )


        } catch (e: Exception) {
            Timber.tag("pagingErrorHandeling").i("Exception :: e=$e")
            Timber.tag("Pagin3TestDebug").i("Exception %s", e)
            LoadResult.Error(e)
        }
    }

    @androidx.paging.ExperimentalPagingApi
    override fun getRefreshKey(state: PagingState<Int, item>): Int {
        return 0
    }

}
