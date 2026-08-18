package com.tamin.taminhamrah.feature.workshops.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.contract.WORKSHOP_STATS_PAGE_SIZE
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopSearch
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopStats
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsEvent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsIntent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsUiState
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsUiState.PartialState
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.Article16DebtQuery
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.model.workshop.WorkshopListQuery
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.GetArticle16DebtsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetEmployerAgreementsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import taminx.core.core_ui.Res
import taminx.core.core_ui.workshop_error_receive_data
import taminx.core.core_ui.workshop_no_debt_found

/**
 * The کارگاه‌های کارفرما list.
 *
 * Three things here are deliberately unlike the screen this replaces. Search and status filter are
 * one query applied together, instead of each call carrying only what it was just handed. The
 * action sheet is gated on the workshop actually having both identity halves, decided once, rather
 * than on a card happening to be expanded. And ماده ۱۶ asks whether the workshop has any debts
 * before navigating, so a workshop with none is told so instead of shown an empty screen.
 */
class WorkshopsViewModel(
    private val getEmployerAgreements: GetEmployerAgreementsUseCase,
    private val getArticle16Debts: GetArticle16DebtsUseCase,
) : BaseViewModel<WorkshopsUiState, PartialState, WorkshopsEvent, WorkshopsIntent>(
    initialState = WorkshopsUiState()
) {

    init {
        sendIntent(WorkshopsIntent.Load)
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
        is WorkshopsIntent.ActionsRequested -> openActions(intent.workshop)
        WorkshopsIntent.ActionsDismissed -> flow { emit(PartialState.ActionsForChanged(null)) }
        is WorkshopsIntent.ActionSelected -> selectAction(intent.action, intent.workshop)
    }

    /** Page 0 replaces what is on screen; later pages append, so the end never flickers back. */
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

        // The header figures describe every workshop the user has, not the filtered view, so they
        // are counted once, on the first unfiltered page, and left alone afterwards.
        if (page == 0 && !search.isNotEmpty && status == null && uiState.value.stats == null) {
            emitAll(countStats(result.total))
        }
    }.catch { emit(PartialState.Error(it.toSingleLineMessage())) }

    /**
     * One extra call, for one figure.
     *
     * The unfiltered total came with the page just loaded; only the active count needs asking for,
     * and asking for a single row is enough — it is the envelope's `total` that is read, not the
     * row. Everything not active is the third figure, by subtraction. A failure here leaves the
     * card with the total it already has rather than taking the list down with it.
     */
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

    /** همه موارد empties the fields as well as the query — the old screen left the text behind. */
    private fun clearSearch(): Flow<PartialState> = flow {
        val status = uiState.value.statusFilter
        emit(PartialState.SearchInputChanged(workshopId = "", branchCode = ""))
        emit(PartialState.QueryApplied(WorkshopSearch(), status))
        emitAll(loadPage(page = 0, search = WorkshopSearch(), status = status))
    }

    private fun applyStatusFilter(status: WorkshopActivityStatus?): Flow<PartialState> = flow {
        val search = uiState.value.appliedSearch
        emit(PartialState.QueryApplied(search, status))
        emit(PartialState.FilterSheetOpenChanged(false))
        emitAll(loadPage(page = 0, search = search, status = status))
    }

    /** A workshop missing either identity half cannot be acted on, so the sheet never opens. */
    private fun openActions(workshop: WorkshopPR): Flow<PartialState> = flow {
        if (!workshop.hasIdentity) {
            sendEvent(WorkshopsEvent.ShowMessage(Res.string.workshop_error_receive_data))
            return@flow
        }
        emit(PartialState.ActionsForChanged(workshop))
    }

    /**
     * ماده ۱۶ asks the service for this workshop's debts before navigating; every other action
     * opens directly.
     */
    private fun selectAction(
        action: WorkshopAction,
        workshop: WorkshopPR,
    ): Flow<PartialState> = flow {
        emit(PartialState.ActionsForChanged(null))
        if (action != WorkshopAction.ARTICLE16) {
            sendEvent(workshop.navigationEvent(action))
            return@flow
        }

        emit(PartialState.Loading)
        val debts = getArticle16Debts(
            Article16DebtQuery(workshopId = workshop.workshopId, branchCode = workshop.branchCode)
        )
        emit(PartialState.Loaded(uiState.value.list))
        if (debts.items.isEmpty()) {
            sendEvent(WorkshopsEvent.ShowMessage(Res.string.workshop_no_debt_found))
        } else {
            sendEvent(workshop.navigationEvent(action))
        }
    }.catch { emit(PartialState.Error(it.toSingleLineMessage())) }

    private fun WorkshopPR.navigationEvent(action: WorkshopAction) = WorkshopsEvent.Navigate(
        action = action,
        workshopId = workshopId,
        branchCode = branchCode,
        workshopName = name,
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
        is PartialState.ActionsForChanged -> currentState.copy(actionsFor = partialState.workshop)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
