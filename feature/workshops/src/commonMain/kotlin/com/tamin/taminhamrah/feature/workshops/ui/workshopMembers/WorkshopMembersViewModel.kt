package com.tamin.taminhamrah.feature.workshops.ui.workshopMembers

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.model.PersonSearch
import com.tamin.taminhamrah.feature.workshops.ui.workshopMembers.WorkshopMembersUiState.PartialState
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.WorkshopMemberQuery
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopMembersUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/** کارکنان of one workshop. */
class WorkshopMembersViewModel(
    private val getWorkshopMembers: GetWorkshopMembersUseCase,
) : BaseViewModel<
    WorkshopMembersUiState,
    PartialState,
    WorkshopMembersEvent,
    WorkshopMembersIntent,
    >(initialState = WorkshopMembersUiState()) {

    override fun handleIntent(intent: WorkshopMembersIntent): Flow<PartialState> = when (intent) {
        is WorkshopMembersIntent.Open -> open(intent)
        WorkshopMembersIntent.LoadMore -> loadMore()
        WorkshopMembersIntent.Retry -> loadPage(page = 0)
        is WorkshopMembersIntent.SearchOpenChanged ->
            flow { emit(PartialState.SearchOpenChanged(intent.isOpen)) }

        is WorkshopMembersIntent.DraftChanged -> flow { emit(PartialState.DraftChanged(intent.draft)) }
        WorkshopMembersIntent.ApplySearch -> applySearch(uiState.value.draft)
        WorkshopMembersIntent.ClearSearch -> applySearch(PersonSearch())
        is WorkshopMembersIntent.ReplaceSearch -> applySearch(intent.search)
    }

    private fun open(intent: WorkshopMembersIntent.Open): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.workshopId == intent.workshopId && state.branchCode == intent.branchCode) return@flow
        emit(PartialState.Opened(intent.workshopId, intent.branchCode))
        emitAll(loadPage(page = 0, identity = intent.workshopId to intent.branchCode))
    }

    private fun loadPage(
        page: Int,
        search: PersonSearch = uiState.value.applied,
        identity: Pair<String, String> = uiState.value.workshopId to uiState.value.branchCode,
    ): Flow<PartialState> = flow {
        val (workshopId, branchCode) = identity
        if (workshopId.isBlank() || branchCode.isBlank()) {
            emit(PartialState.Error(null))
            return@flow
        }
        emit(if (page == 0) PartialState.Loading else PartialState.LoadingMore)
        val result = getWorkshopMembers(
            WorkshopMemberQuery(
                workshopId = workshopId,
                branchCode = branchCode,
                insuranceNumber = search.insuranceNumber.takeIf { it.isNotBlank() },
                nationalId = search.nationalId.takeIf { it.isNotBlank() },
                page = page,
            )
        )
        if (hasMovedOn(search, identity)) return@flow
        emit(
            PartialState.Loaded(
                uiState.value.list.loaded(result, isFirstPage = page == 0) { it.toPresentation() }
            )
        )
    }.catch { if (!hasMovedOn(search, identity)) emit(PartialState.Error(it.toSingleLineMessage())) }

    /**
     * Whether the answer now in hand belongs to a list the screen has already left behind.
     *
     * `BaseViewModel` merges intent flows rather than switching between them, so applying a search
     * does not cancel the page already in flight. A slow page-N answer for the previous search can
     * land after the new search's page 0, and `loaded(isFirstPage = false)` would append the old
     * search's people to the new search's results — rows that match nothing the user asked for.
     * The request carries the search and the workshop it was made for; anything else is dropped.
     */
    private fun hasMovedOn(search: PersonSearch, identity: Pair<String, String>): Boolean {
        val state = uiState.value
        return state.applied != search ||
            state.workshopId != identity.first ||
            state.branchCode != identity.second
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
        currentState: WorkshopMembersUiState,
        partialState: PartialState,
    ): WorkshopMembersUiState = when (partialState) {
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
