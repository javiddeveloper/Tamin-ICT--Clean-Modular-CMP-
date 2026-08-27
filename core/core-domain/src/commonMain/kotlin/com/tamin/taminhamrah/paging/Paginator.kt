package com.tamin.taminhamrah.paging

import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class Paginator<T>(
    baseQuery: ApiQueryParamDN = ApiQueryParamDN(),
    private val config: PaginationConfig = PaginationConfig(),
    private val loadPage: suspend (query: ApiQueryParamDN) -> PageDN<T>,
) {

    private val _state = MutableStateFlow(PaginationState<T>())
    val state: StateFlow<PaginationState<T>> = _state.asStateFlow()

    private val mutex = Mutex()

    private var query: ApiQueryParamDN = baseQuery
    private var nextPage: Int = config.firstPage
    private var isLoading: Boolean = false
    private var generation: Int = 0

    suspend fun loadNext() = load(reset = false)

    suspend fun refresh(query: ApiQueryParamDN? = null) = load(reset = true, newQuery = query)

    suspend fun retry() {
        mutex.withLock {
            if (_state.value.error == null) return
            _state.value = _state.value.copy(error = null)
        }
        load(reset = false)
    }

    private suspend fun load(reset: Boolean, newQuery: ApiQueryParamDN? = null) {
        val request: LoadRequest = mutex.withLock {
            val current = _state.value
            if (!reset && (isLoading || current.endReached || current.error != null)) return

            if (reset) {
                generation++
                nextPage = config.firstPage
            }
            newQuery?.let { query = it }
            isLoading = true

            val isFullScreenLoad = reset || current.items.isEmpty()
            _state.value = current.copy(
                isLoadingFirstPage = isFullScreenLoad,
                isLoadingNextPage = !isFullScreenLoad,
                endReached = if (reset) false else current.endReached,
                error = null,
            )

            LoadRequest(generation = generation, page = nextPage, query = query)
        }

        val page: PageDN<T> = try {
            loadPage(request.query.forPage(request.page, config))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            mutex.withLock {
                if (request.generation != generation) return
                isLoading = false
                _state.value = _state.value.copy(
                    isLoadingFirstPage = false,
                    isLoadingNextPage = false,
                    error = e,
                )
            }
            return
        }

        mutex.withLock {
            if (request.generation != generation) return

            isLoading = false
            val isFirstPage = request.page == config.firstPage
            val items = if (isFirstPage) page.items else _state.value.items + page.items
            nextPage = request.page + 1

            _state.value = _state.value.copy(
                items = items,
                isLoadingFirstPage = false,
                isLoadingNextPage = false,
                endReached = isEndReached(page, items.size),
                error = null,
            )
        }
    }

    private data class LoadRequest(
        val generation: Int,
        val page: Int,
        val query: ApiQueryParamDN,
    )

    private fun isEndReached(page: PageDN<T>, loadedCount: Int): Boolean {
        val total = page.total
        return page.items.isEmpty() ||
            page.items.size < config.pageSize ||
            (total != null && loadedCount >= total)
    }
}
