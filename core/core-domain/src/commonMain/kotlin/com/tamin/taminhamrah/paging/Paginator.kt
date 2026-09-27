package com.tamin.taminhamrah.paging

import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Collects every page a repository emits for one request, so an offline-first repository
 * (cache first, then network — the same shape as the app's other cached flows) works as is:
 *
 * - a page with [PageDN.isFromCache] is shown immediately, with [PaginationState.isRefreshing];
 * - the last emission wins — normally the network page, which replaces the cached one;
 * - if the last emission is still the cache (the repository fell back offline), it is used
 *   like any page and marked [PaginationState.isFromCache]. End-of-list comes from its size or
 *   [PageDN.total] as usual, so a repository serving cached slices per offset keeps paging
 *   offline, while one serving its whole cache at once sets `total` to end the list there.
 *
 * Network-only callers keep passing a suspend `loadPage` (secondary constructor).
 */
class Paginator<T>(
    baseQuery: ApiQueryParamDN = ApiQueryParamDN(),
    private val config: PaginationConfig = PaginationConfig(),
    private val loadPages: (query: ApiQueryParamDN) -> Flow<PageDN<T>>,
) {

    constructor(
        baseQuery: ApiQueryParamDN = ApiQueryParamDN(),
        config: PaginationConfig = PaginationConfig(),
        loadPage: suspend (query: ApiQueryParamDN) -> PageDN<T>,
    ) : this(
        baseQuery = baseQuery,
        config = config,
        loadPages = { query: ApiQueryParamDN -> flow { emit(loadPage(query)) } },
    )

    private val _state = MutableStateFlow(PaginationState<T>())
    val state: StateFlow<PaginationState<T>> = _state.asStateFlow()

    private val mutex = Mutex()

    private var query: ApiQueryParamDN = baseQuery
    private var nextPage: Int = config.firstPage
    private var isLoading: Boolean = false
    private var generation: Int = 0

    /**
     * A [loadNext] that arrived while a load was in flight. It is replayed once that load
     * succeeds, because scroll triggers (`OnLoadMore`) fire only on the false→true edge of
     * "near the end": if the call were simply dropped — e.g. while a cached first page is on
     * screen and the network page is still loading — the trigger would never fire again.
     */
    private var pendingLoadNext: Boolean = false

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
            if (!reset) {
                if (isLoading) {
                    pendingLoadNext = true
                    return
                }
                if (current.endReached || current.error != null) return
            }

            if (reset) {
                generation++
                nextPage = config.firstPage
                pendingLoadNext = false
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

        var lastPage: PageDN<T>? = null
        try {
            loadPages(request.query.forPage(request.page, config)).collect { page ->
                lastPage = page
                if (page.isFromCache) showCachedPage(request, page)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            mutex.withLock {
                if (request.generation != generation) return
                isLoading = false
                // Never auto-load after a failure; the user retries explicitly.
                pendingLoadNext = false
                _state.value = _state.value.copy(
                    isLoadingFirstPage = false,
                    isLoadingNextPage = false,
                    isRefreshing = false,
                    error = e,
                )
            }
            return
        }
        val page: PageDN<T> = lastPage ?: PageDN(emptyList())

        val replayLoadNext = mutex.withLock {
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
                isRefreshing = false,
                isFromCache = page.isFromCache,
            )
            pendingLoadNext.also { pendingLoadNext = false }
        }
        // load() re-checks endReached, so a replay after the last page is a no-op.
        if (replayLoadNext) load(reset = false)
    }

    /** Shows a cached first page while the network page is still in flight. */
    private suspend fun showCachedPage(request: LoadRequest, page: PageDN<T>) {
        if (request.page != config.firstPage || page.items.isEmpty()) return
        mutex.withLock {
            if (request.generation != generation) return@withLock
            _state.value = _state.value.copy(
                items = page.items,
                isLoadingFirstPage = false,
                isLoadingNextPage = false,
                isRefreshing = true,
                isFromCache = true,
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
