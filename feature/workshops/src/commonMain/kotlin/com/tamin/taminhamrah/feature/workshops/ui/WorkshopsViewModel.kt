package com.tamin.taminhamrah.feature.workshops.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopSearch
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsEvent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsIntent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsUiState
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsUiState.PartialState
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.Article16DebtQuery
import com.tamin.taminhamrah.model.workshop.WORKSHOP_PAGE_SIZE
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.model.workshop.WorkshopListQuery
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.GetArticle16DebtsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetEmployerAgreementsUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
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
 * Two things here are deliberately unlike the screen this replaces. Search and status filter are
 * one query, applied together, instead of each call carrying only what it was just handed. And the
 * action menu is gated on the workshop actually having both identity halves, decided once, rather
 * than on the card happening to be expanded.
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
        WorkshopsIntent.LoadMore -> loadNextPage()
        is WorkshopsIntent.WorkshopIdChanged ->
            flow { emit(PartialState.SearchInputChanged(workshopId = intent.value)) }

        is WorkshopsIntent.BranchCodeChanged ->
            flow { emit(PartialState.SearchInputChanged(branchCode = intent.value)) }

        WorkshopsIntent.ApplySearch -> applySearch()
        WorkshopsIntent.ClearSearch -> clearSearch()
        is WorkshopsIntent.StatusFilterChanged -> applyStatusFilter(intent.status)
        is WorkshopsIntent.ActionsRequested -> openActions(intent.workshop)
        WorkshopsIntent.ActionsDismissed -> flow { emit(PartialState.ActionsForChanged(null)) }
        is WorkshopsIntent.ActionSelected -> selectAction(intent.action, intent.workshop)
    }

    /**
     * Loads one page.
     *
     * Page 0 replaces what is on screen; every later page appends, so scrolling to the end never
     * makes the list flicker back to a skeleton.
     */
    private fun loadPage(
        page: Int,
        search: WorkshopSearch = uiState.value.appliedSearch,
        status: WorkshopActivityStatus? = uiState.value.statusFilter,
        existing: ImmutableList<WorkshopPR> = persistentListOf(),
    ): Flow<PartialState> = flow {
        emit(if (page == 0) PartialState.Loading(true) else PartialState.LoadingMore)

        val result = getEmployerAgreements(
            WorkshopListQuery(
                workshopId = search.workshopId.takeIf { it.isNotBlank() },
                branchCode = search.branchCode.takeIf { it.isNotBlank() },
                status = status,
                page = page,
            )
        )

        val workshops = (existing + result.items.map { it.toPresentation() }).toImmutableList()
        emit(
            PartialState.Loaded(
                workshops = workshops,
                // The service reports a grand total, so "is there another page" is answered
                // without asking for one that comes back empty.
                hasMore = result.hasMoreAfter(workshops.size) && result.items.size == WORKSHOP_PAGE_SIZE,
            )
        )
    }.catch { emit(PartialState.Error(it.toSingleLineMessage())) }

    private fun loadNextPage(): Flow<PartialState> {
        val state = uiState.value
        if (!state.hasMore || state.isLoading || state.isLoadingMore) return flow { }
        return loadPage(
            page = state.workshops.size / WORKSHOP_PAGE_SIZE,
            existing = state.workshops,
        )
    }

    private fun applySearch(): Flow<PartialState> = flow {
        val state = uiState.value
        val search = WorkshopSearch(
            workshopId = state.workshopIdInput.trim(),
            branchCode = state.branchCodeInput.trim(),
        )
        emit(PartialState.QueryApplied(search, state.statusFilter))
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
     * ماده ۱۶ asks the service for the workshop's debts before navigating, because a workshop with
     * none must be told so rather than shown an empty screen. Every other action opens directly.
     */
    private fun selectAction(action: WorkshopAction, workshop: WorkshopPR): Flow<PartialState> = flow {
        emit(PartialState.ActionsForChanged(null))
        if (action != WorkshopAction.ARTICLE16) {
            sendEvent(workshop.navigationEvent(action))
            return@flow
        }

        emit(PartialState.Loading(true))
        val debts = getArticle16Debts(
            Article16DebtQuery(workshopId = workshop.workshopId, branchCode = workshop.branchCode)
        )
        emit(PartialState.Loading(false))
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
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            isLoadingMore = false,
            error = null,
        )

        PartialState.LoadingMore -> currentState.copy(isLoadingMore = true, error = null)

        is PartialState.Error -> currentState.copy(
            isLoading = false,
            isLoadingMore = false,
            error = partialState.message,
        )

        is PartialState.Loaded -> currentState.copy(
            isLoading = false,
            isLoadingMore = false,
            error = null,
            workshops = partialState.workshops,
            hasMore = partialState.hasMore,
        )

        is PartialState.SearchInputChanged -> currentState.copy(
            workshopIdInput = partialState.workshopId ?: currentState.workshopIdInput,
            branchCodeInput = partialState.branchCode ?: currentState.branchCodeInput,
        )

        is PartialState.QueryApplied -> currentState.copy(
            appliedSearch = partialState.search,
            statusFilter = partialState.status,
        )

        is PartialState.ActionsForChanged -> currentState.copy(actionsFor = partialState.workshop)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
