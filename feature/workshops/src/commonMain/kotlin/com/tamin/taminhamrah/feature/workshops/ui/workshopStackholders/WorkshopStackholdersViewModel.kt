package com.tamin.taminhamrah.feature.workshops.ui.workshopStackholders

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.model.PersonSearch
import com.tamin.taminhamrah.feature.workshops.ui.workshopStackholders.WorkshopStackholdersUiState.PartialState
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderQuery
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopStackHoldersUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/** ذینفعان of one workshop. */
class WorkshopStackholdersViewModel(
    private val getWorkshopStackHolders: GetWorkshopStackHoldersUseCase,
) : BaseViewModel<
    WorkshopStackholdersUiState,
    PartialState,
    WorkshopStackholdersEvent,
    WorkshopStackholdersIntent,
    >(initialState = WorkshopStackholdersUiState()) {

    /**
     * Which load the screen is waiting for; an answer from an earlier one is dropped.
     *
     * `BaseViewModel` merges intent flows rather than switching between them, so applying a search
     * does not cancel the page already in flight. Without this, a slow page-N answer for the
     * previous search arriving after the new search's page 0 would be *appended* to it — people
     * who match nothing the user asked for, and a `receivedCount` advanced by them. The reduced
     * state cannot be consulted for this: `flatMapMerge` buffers, so it still lags the emission
     * that this very flow just made.
     */
    private var latestLoad = 0

    override fun handleIntent(intent: WorkshopStackholdersIntent): Flow<PartialState> = when (intent) {
        is WorkshopStackholdersIntent.Open -> open(intent)
        WorkshopStackholdersIntent.LoadMore -> loadMore()
        WorkshopStackholdersIntent.Retry -> loadPage(page = 0)
        is WorkshopStackholdersIntent.SearchOpenChanged ->
            flow { emit(PartialState.SearchOpenChanged(intent.isOpen)) }

        is WorkshopStackholdersIntent.DraftChanged ->
            flow { emit(PartialState.DraftChanged(intent.draft)) }

        WorkshopStackholdersIntent.ApplySearch -> applySearch(uiState.value.draft)
        WorkshopStackholdersIntent.ClearSearch -> applySearch(PersonSearch())
        is WorkshopStackholdersIntent.ReplaceSearch -> applySearch(intent.search)
    }

    private fun open(intent: WorkshopStackholdersIntent.Open): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.workshopId == intent.workshopId && state.branchCode == intent.branchCode) return@flow
        emit(PartialState.Opened(intent.workshopId, intent.branchCode))
        emitAll(loadPage(page = 0, identity = intent.workshopId to intent.branchCode))
    }

    private fun loadPage(
        page: Int,
        search: PersonSearch = uiState.value.applied,
        identity: Pair<String, String> = uiState.value.workshopId to uiState.value.branchCode,
    ): Flow<PartialState> {
        // Claimed before the request goes out, so an answer can tell whether the list it was asked
        // for is still the list on screen.
        val load = ++latestLoad
        return flow {
            val (workshopId, branchCode) = identity
            if (workshopId.isBlank() || branchCode.isBlank()) {
                emit(PartialState.Error(null))
                return@flow
            }
            emit(if (page == 0) PartialState.Loading else PartialState.LoadingMore)
            val result = getWorkshopStackHolders(
                WorkshopStackHolderQuery(
                    workshopId = workshopId,
                    branchCode = branchCode,
                    nationalId = search.nationalId.takeIf { it.isNotBlank() },
                    page = page,
                )
            )
            if (load != latestLoad) return@flow
            emit(
                PartialState.Loaded(
                    uiState.value.list.loaded(result, isFirstPage = page == 0) { it.toPresentation() }
                )
            )
        }.catch { if (load == latestLoad) emit(PartialState.Error(it.toSingleLineMessage())) }
    }

    private fun loadMore(): Flow<PartialState> {
        val list = uiState.value.list
        if (!list.canLoadMore) return flow { }
        return loadPage(page = list.nextPage)
    }

    private fun applySearch(search: PersonSearch): Flow<PartialState> = flow {
        emit(PartialState.DraftChanged(search))
        emit(PartialState.Applied(search))
        emit(PartialState.SearchOpenChanged(false))
        emitAll(loadPage(page = 0, search = search))
    }

    override fun reduceState(
        currentState: WorkshopStackholdersUiState,
        partialState: PartialState,
    ): WorkshopStackholdersUiState = when (partialState) {
        is PartialState.Opened -> currentState.copy(
            workshopId = partialState.workshopId,
            branchCode = partialState.branchCode,
        )

        PartialState.Loading -> currentState.copy(list = currentState.list.loading())
        PartialState.LoadingMore -> currentState.copy(list = currentState.list.loadingMore())
        is PartialState.Error -> currentState.copy(list = currentState.list.failed(partialState.message))
        is PartialState.Loaded -> currentState.copy(list = partialState.list)
        is PartialState.DraftChanged -> currentState.copy(draft = partialState.draft)
        is PartialState.Applied -> currentState.copy(applied = partialState.search)
        is PartialState.SearchOpenChanged -> currentState.copy(isSearchOpen = partialState.isOpen)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
