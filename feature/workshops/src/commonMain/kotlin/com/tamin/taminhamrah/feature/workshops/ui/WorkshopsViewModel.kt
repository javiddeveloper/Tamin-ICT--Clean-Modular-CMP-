package com.tamin.taminhamrah.feature.workshops.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.feature.workshops.ui.contract.WORKSHOP_STATS_PAGE_SIZE
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopSearch
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopStats
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsEvent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsIntent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsUiState
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsUiState.PartialState
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.model.workshop.WorkshopListQuery
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.GetEmployerAgreementsUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import taminx.core.core_ui.Res
import taminx.core.core_ui.workshop_action_missing_identity
import taminx.core.core_ui.workshop_error_receive_data

class WorkshopsViewModel(
    private val getEmployerAgreements: GetEmployerAgreementsUseCase,
    private val featureManager: FeatureManager,
) : BaseViewModel<WorkshopsUiState, PartialState, WorkshopsEvent, WorkshopsIntent>(
    initialState = WorkshopsUiState()
) {

    init {
        sendIntent(WorkshopsIntent.Load)
        resolveAvailableActions()
    }

    /**
     * Drops the services the server has switched off.
     *
     * Read once when the screen is created rather than per action row: `isFeatureEnabled` suspends,
     * and a composable cannot wait on it without drawing the row first and removing it after.
     */
    private fun resolveAvailableActions() = doAsyncTask {
        val actions = WorkshopAction.entries.filter { action ->
            val flag = action.featureFlag ?: return@filter true
            runCatching { featureManager.isFeatureEnabled(flag) }
                // A flag that cannot be read is not a flag that is off — the menu the user came
                // through already let them in here.
                .getOrDefault(true)
        }
        sendIntent(WorkshopsIntent.AvailableActionsResolved(actions.toImmutableList()))
    }

    override fun handleIntent(intent: WorkshopsIntent): Flow<PartialState> = when (intent) {
        WorkshopsIntent.Load -> loadPage(page = 0)
        WorkshopsIntent.LoadMore -> loadMore()
        is WorkshopsIntent.WorkshopIdChanged ->
            flow { emit(PartialState.SearchInputChanged(workshopId = intent.value)) }

        is WorkshopsIntent.BranchCodeChanged ->
            flow { emit(PartialState.SearchInputChanged(branchCode = intent.value)) }

        is WorkshopsIntent.SearchOpenChanged ->
            flow { emit(PartialState.SearchOpenChanged(intent.isOpen)) }

        WorkshopsIntent.ApplySearch -> applySearch()
        WorkshopsIntent.ClearSearch -> clearSearch()
        is WorkshopsIntent.FilterSheetOpenChanged ->
            flow { emit(PartialState.FilterSheetOpenChanged(intent.isOpen)) }

        is WorkshopsIntent.StatusFilterChanged -> applyStatusFilter(intent.status)
        is WorkshopsIntent.DetailRequested -> openDetail(intent.workshop)
        WorkshopsIntent.DetailDismissed -> flow { emit(PartialState.DetailForChanged(null)) }
        is WorkshopsIntent.ActionSelected -> selectAction(intent.action, intent.workshop)
        is WorkshopsIntent.AvailableActionsResolved ->
            flow { emit(PartialState.ActionsResolved(intent.actions)) }
    }

    private fun loadPage(
        page: Int,
        search: WorkshopSearch = uiState.value.appliedSearch,
        status: WorkshopActivityStatus? = uiState.value.statusFilter,
    ): Flow<PartialState> = flow {
        emit(if (page == 0) PartialState.Loading else PartialState.LoadingMore)

        val result = getEmployerAgreements(
            WorkshopListQuery(
                workshopId = search.workshopId.takeIf { it.isNotBlank() },
                branchCode = search.branchCode.takeIf { it.isNotBlank() },
                status = status,
                page = page,
            )
        )
        emit(
            PartialState.Loaded(
                uiState.value.list.loaded(result, isFirstPage = page == 0) { it.toPresentation() }
            )
        )

        if (page == 0 && !search.isNotEmpty && status == null && uiState.value.stats == null) {
            emitAll(countStats(result.total))
        }
    }.catch { emit(PartialState.Error(it.toSingleLineMessage())) }

    private fun countStats(total: Int): Flow<PartialState> = flow {
        val active = getEmployerAgreements(
            WorkshopListQuery(
                status = WorkshopActivityStatus.ACTIVE,
                pageSize = WORKSHOP_STATS_PAGE_SIZE,
            )
        ).total
        emit(PartialState.StatsLoaded(WorkshopStats(total = total, active = active)))
    }.catch { emit(PartialState.StatsLoaded(WorkshopStats(total = total))) }

    private fun loadMore(): Flow<PartialState> {
        val list = uiState.value.list
        if (!list.canLoadMore) return flow { }
        return loadPage(page = list.nextPage)
    }

    private fun applySearch(): Flow<PartialState> = flow {
        val state = uiState.value
        val search = WorkshopSearch(
            workshopId = state.workshopIdInput.trim(),
            branchCode = state.branchCodeInput.trim(),
        )
        emit(PartialState.QueryApplied(search, state.statusFilter))
        emit(PartialState.SearchOpenChanged(false))
        emitAll(loadPage(page = 0, search = search, status = state.statusFilter))
    }

    private fun clearSearch(): Flow<PartialState> = flow {
        val status = uiState.value.statusFilter
        emit(PartialState.SearchInputChanged(workshopId = "", branchCode = ""))
        emit(PartialState.SearchOpenChanged(false))
        emit(PartialState.QueryApplied(WorkshopSearch(), status))
        emitAll(loadPage(page = 0, search = WorkshopSearch(), status = status))
    }

    private fun applyStatusFilter(status: WorkshopActivityStatus?): Flow<PartialState> = flow {
        val search = uiState.value.appliedSearch
        emit(PartialState.QueryApplied(search, status))
        emit(PartialState.FilterSheetOpenChanged(false))
        emitAll(loadPage(page = 0, search = search, status = status))
    }

    private fun openDetail(workshop: WorkshopPR): Flow<PartialState> = flow {
        if (!workshop.hasIdentity) {
            sendEvent(WorkshopsEvent.ShowMessage(Res.string.workshop_error_receive_data))
            return@flow
        }
        emit(PartialState.DetailForChanged(workshop))
    }

    /**
     * Opens the service the menu picked.
     *
     * Every action navigates, including رسیدگی به بدهی ماده ۱۶. That one used to fetch its debts
     * first and refuse with a message when there were none — which cost a request on every tap and
     * made it the one row in the list that answers with a toast instead of a screen. Its own list
     * shows the same «نتیجه‌ای یافت نشد» empty state every other workshop screen does.
     */
    private fun selectAction(
        action: WorkshopAction,
        workshop: WorkshopPR,
    ): Flow<PartialState> = flow {
        // Every one of these services takes workshopId/branchCode as *path segments*. Half an
        // identity does not narrow the request, it addresses a route that does not exist — the
        // service answers 404 and the destination shows an empty list it cannot explain. Say so
        // here instead, where the missing half is still visible.
        if (!workshop.hasIdentity) {
            sendEvent(WorkshopsEvent.ShowMessage(Res.string.workshop_action_missing_identity))
            return@flow
        }
        sendEvent(workshop.navigationEvent(action))
    }

    private fun WorkshopPR.navigationEvent(action: WorkshopAction) = WorkshopsEvent.Navigate(
        action = action,
        workshopId = workshopId,
        branchCode = branchCode,
        workshopName = name,
        characterCode = characterCode,
        legalNationalId = legalNationalId,
    )

    override fun reduceState(
        currentState: WorkshopsUiState,
        partialState: PartialState,
    ): WorkshopsUiState = when (partialState) {
        PartialState.Loading -> currentState.copy(list = currentState.list.loading())
        PartialState.LoadingMore -> currentState.copy(list = currentState.list.loadingMore())
        is PartialState.Error -> currentState.copy(list = currentState.list.failed(partialState.message))
        is PartialState.Loaded -> currentState.copy(list = partialState.list)
        is PartialState.SearchInputChanged -> currentState.copy(
            workshopIdInput = partialState.workshopId ?: currentState.workshopIdInput,
            branchCodeInput = partialState.branchCode ?: currentState.branchCodeInput,
        )

        is PartialState.QueryApplied -> currentState.copy(
            appliedSearch = partialState.search,
            statusFilter = partialState.status,
        )

        is PartialState.SearchOpenChanged -> currentState.copy(isSearchOpen = partialState.isOpen)
        is PartialState.FilterSheetOpenChanged ->
            currentState.copy(isFilterSheetOpen = partialState.isOpen)

        is PartialState.StatsLoaded -> currentState.copy(stats = partialState.stats)
        is PartialState.ActionsResolved ->
            currentState.copy(availableActions = partialState.actions)

        is PartialState.DetailForChanged -> currentState.copy(detailFor = partialState.workshop)

    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
