package com.tamin.taminhamrah.feature.workshops.ui.contractRows

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.contract.ContractRowFilter
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.contract.ContractRowTab
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.contract.ContractRowsEvent
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.contract.ContractRowsIntent
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.contract.ContractRowsUiState
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.contract.ContractRowsUiState.PartialState
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.mapper.workshop.toContractRow
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.ContractRowQuery
import com.tamin.taminhamrah.model.workshop.WorkshopListQuery
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.GetContractRowsWithAgreementUseCase
import com.tamin.taminhamrah.useCases.workshops.GetContractRowsWithoutAgreementUseCase
import com.tamin.taminhamrah.useCases.workshops.GetEmployerAgreementsUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * ردیف‌های پیمان — the contract rows of one workshop, from either of two services.
 *
 * The two backend calls the old app gave two whole screens to are one screen and one tab here.
 * They return different models, so each has its own use case; the mapper folds both into the same
 * card model, which is why the tab only decides *which* call runs and nothing below it branches.
 */
class ContractRowsViewModel(
    private val getWithAgreement: GetContractRowsWithAgreementUseCase,
    private val getWithoutAgreement: GetContractRowsWithoutAgreementUseCase,
    private val getMyWorkshops: GetEmployerAgreementsUseCase,
) : BaseViewModel<ContractRowsUiState, PartialState, ContractRowsEvent, ContractRowsIntent>(
    initialState = ContractRowsUiState()
) {

    override fun handleIntent(intent: ContractRowsIntent): Flow<PartialState> = when (intent) {
        is ContractRowsIntent.Open -> open(intent)
        is ContractRowsIntent.TabSelected -> selectTab(intent.tab)
        ContractRowsIntent.LoadMore -> loadMore()
        // The same first-page load the filter originally triggered, fallback included.
        ContractRowsIntent.Retry -> loadPage(page = 0, allowFallback = true)
        is ContractRowsIntent.PickerOpenChanged -> setPickerOpen(intent.isOpen)
        is ContractRowsIntent.DraftWorkshopIdChanged -> flow {
            emit(PartialState.DraftChanged(workshopId = intent.value))
            // The message clears the moment the field it names is edited.
            emit(PartialState.WorkshopIdErrorChanged(isVisible = false))
        }

        is ContractRowsIntent.DraftBranchCodeChanged -> flow {
            emit(PartialState.DraftChanged(branchCode = intent.value))
            emit(PartialState.BranchCodeErrorChanged(isVisible = false))
        }

        is ContractRowsIntent.QuickPicked -> flow {
            emit(
                PartialState.DraftChanged(
                    workshopId = intent.workshopId,
                    branchCode = intent.branchCode,
                )
            )
            emit(PartialState.WorkshopIdErrorChanged(isVisible = false))
            emit(PartialState.BranchCodeErrorChanged(isVisible = false))
        }

        ContractRowsIntent.ApplyPicker -> applyPicker()
        ContractRowsIntent.ClearPicker -> clearPicker()
    }

    /**
     * Opened with an identity — the drill-down — loads that workshop straight away. Opened without
     * one — the services grid — has nothing to fetch, so it raises the picker instead of showing an
     * empty list the user has no way to read as "choose a workshop".
     *
     * Re-opening on an identity already in state does not refetch: returning to the screen keeps
     * the page and the tab the user left behind.
     */
    private fun open(intent: ContractRowsIntent.Open): Flow<PartialState> = flow {
        val state = uiState.value
        // Either half missing is the same situation as arriving from the grid: there is no route
        // to request. A drill-down from a workshop that lost one of its codes lands here too, and
        // must ask rather than send `…/{workshopId}/` and take the 404.
        if (intent.workshopId.isBlank() || intent.branchCode.isBlank()) {
            // Through the same path the search button takes, not a bare open: that is what also
            // fetches کارگاه‌های شما, and raising the sheet without it leaves the grid entry with
            // two empty fields and no shortcut.
            if (state.applied == null) emitAll(setPickerOpen(isOpen = true))
            return@flow
        }
        val filter = ContractRowFilter(intent.workshopId, intent.branchCode)
        if (state.applied == filter) return@flow

        emit(PartialState.DraftChanged(intent.workshopId, intent.branchCode))
        emit(PartialState.Applied(filter))
        emitAll(loadPage(page = 0, filter = filter, allowFallback = true))
    }

    /**
     * Switching tab re-reads from the other service, because the two hold different rows for the
     * same workshop. With no workshop chosen there is nothing to read — only the empty state's
     * wording changes.
     */
    private fun selectTab(tab: ContractRowTab): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.tab == tab) return@flow
        emit(PartialState.TabChanged(tab))
        val filter = state.applied ?: return@flow
        // A deliberate switch is final — no falling back out of the tab the user just chose.
        emitAll(loadPage(page = 0, filter = filter, tab = tab, allowFallback = false))
    }

    /** The service that is *not* the one given. A workshop is in exactly one of the two. */
    private fun ContractRowTab.other(): ContractRowTab = when (this) {
        ContractRowTab.WITH_AGREEMENT -> ContractRowTab.WITHOUT_AGREEMENT
        ContractRowTab.WITHOUT_AGREEMENT -> ContractRowTab.WITH_AGREEMENT
    }

    /**
     * One page of whichever service [tab] names.
     *
     * [allowFallback] covers the guess the screen would otherwise push onto the user: a workshop
     * either has a تعهدنامه or it does not, so one of the two tabs is always empty, and the design's
     * own empty state tells the user to go and try the other one. When a *first* page arrives empty
     * and the tab was not chosen by hand, the other service is read instead and the tab follows.
     * Only ever once per filter — the fallback load passes `false`, so two empty services settle on
     * the empty state rather than ping-ponging.
     */
    private fun loadPage(
        page: Int,
        filter: ContractRowFilter? = uiState.value.applied,
        tab: ContractRowTab = uiState.value.tab,
        allowFallback: Boolean = false,
    ): Flow<PartialState> = flow {
        if (filter == null) return@flow
        emit(if (page == 0) PartialState.Loading else PartialState.LoadingMore)

        val query = ContractRowQuery(
            workshopId = filter.workshopId,
            branchCode = filter.branchCode,
            page = page,
        )
        // Both branches fold into the same card model, so the list below this point is one type.
        val list = when (tab) {
            ContractRowTab.WITH_AGREEMENT ->
                uiState.value.list.loaded(getWithAgreement(query), isFirstPage = page == 0) {
                    it.toContractRow()
                }

            ContractRowTab.WITHOUT_AGREEMENT ->
                uiState.value.list.loaded(getWithoutAgreement(query), isFirstPage = page == 0) {
                    it.toContractRow()
                }
        }
        if (allowFallback && page == 0 && list.items.isEmpty()) {
            // The move goes first: the fallback's own rows are tagged with the tab they were
            // fetched for, and the reducer drops a result whose tab is not the one on screen.
            emit(PartialState.AutoSwitchedTab(tab.other()))
            emitAll(loadPage(page = 0, filter = filter, tab = tab.other(), allowFallback = false))
            return@flow
        }
        emit(PartialState.Loaded(list, tab))
    }.catch { emit(PartialState.Error(it.toSingleLineMessage(), tab)) }

    private fun loadMore(): Flow<PartialState> {
        val list = uiState.value.list
        if (!list.canLoadMore) return flow { }
        return loadPage(page = list.nextPage)
    }

    /**
     * Opening the sheet seeds it from what is already applied, so «تغییر» edits the live filter
     * rather than starting from blank, and fetches کارگاه‌های شما the first time only.
     */
    private fun setPickerOpen(isOpen: Boolean): Flow<PartialState> = flow {
        emit(PartialState.PickerOpenChanged(isOpen))
        if (!isOpen) return@flow

        val state = uiState.value
        state.applied?.let {
            emit(PartialState.DraftChanged(it.workshopId, it.branchCode))
        }
        emit(PartialState.WorkshopIdErrorChanged(isVisible = false))
        emit(PartialState.BranchCodeErrorChanged(isVisible = false))
        if (state.myWorkshops.isNotEmpty()) return@flow

        val workshops = getMyWorkshops(WorkshopListQuery(page = 0))
        emit(
            PartialState.MyWorkshopsLoaded(
                workshops.items
                    .map { it.toPresentation() }
                    // One workshop reaches this list once per agreement it holds, so the same
                    // کارگاه arrives two or three times — seen on device, where «دبستان غیر دولتي
                    // کارن» was offered twice. The paged list this comes from dedupes rows for the
                    // same reason; here the rows are mapped straight through, so it is done here.
                    // Keyed on the identity the pick actually uses, not on the whole card.
                    .distinctBy { it.workshopId to it.branchCode }
                    .toImmutableList(),
                total = workshops.total,
            )
        )
    }.catch {
        // کارگاه‌های شما is a convenience above two fields that already work. Failing to fetch it
        // must not put an error on the list the user has not asked for yet, so it is swallowed and
        // the section simply does not appear.
        emit(PartialState.MyWorkshopsLoaded(uiState.value.myWorkshops, uiState.value.myWorkshopsTotal))
    }

    /**
     * کد کارگاه is the one required field — it is a path segment, so a blank one would request
     * a different route entirely. Submitting without it surfaces the message at the field rather
     * than doing nothing.
     */
    private fun applyPicker(): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.list.isLoading) return@flow

        // Both codes are required, because both are path segments. A blank کد شعبه does not widen
        // the search — it addresses `…/{workshopId}/`, which the live service answers with 404, and
        // the list then renders empty rather than as an error. The old app refused to fetch until
        // both were present, on both of its equivalent screens.
        val workshopId = state.draftWorkshopId.trim()
        val branchCode = state.draftBranchCode.trim()
        emit(PartialState.WorkshopIdErrorChanged(isVisible = workshopId.isBlank()))
        emit(PartialState.BranchCodeErrorChanged(isVisible = branchCode.isBlank()))
        if (workshopId.isBlank() || branchCode.isBlank()) return@flow

        val filter = ContractRowFilter(workshopId, branchCode)
        emit(PartialState.Applied(filter))
        emit(PartialState.PickerOpenChanged(isOpen = false))
        emitAll(loadPage(page = 0, filter = filter, allowFallback = true))
    }

    /** «حذف» drops the filter and the rows with it — the list has no meaning without a workshop. */
    private fun clearPicker(): Flow<PartialState> = flow {
        emit(PartialState.Cleared)
        emit(PartialState.PickerOpenChanged(isOpen = false))
    }

    override fun reduceState(
        currentState: ContractRowsUiState,
        partialState: PartialState,
    ): ContractRowsUiState = when (partialState) {
        is PartialState.TabChanged -> currentState.copy(
            tab = partialState.tab,
            didAutoSwitchTab = false,
            // The other service's rows are not this tab's rows; keeping them would show the
            // previous tab's list under the new tab's heading until the request lands.
            list = PagedListState(),
        )

        PartialState.Loading -> currentState.copy(list = currentState.list.loading())
        PartialState.LoadingMore -> currentState.copy(list = currentState.list.loadingMore())
        // A result for a tab the user has already left is discarded rather than painted under the
        // new tab's heading — the request it came from was never canceled, only superseded.
        is PartialState.Loaded ->
            if (partialState.tab == currentState.tab) {
                currentState.copy(list = partialState.list)
            } else {
                currentState
            }

        // Same rule for a failure: a tab the user has left must not put its error on the one they
        // are looking at. A null tab is the pipeline's own catch-all and always applies.
        is PartialState.Error ->
            if (partialState.tab == null || partialState.tab == currentState.tab) {
                currentState.copy(list = currentState.list.failed(partialState.message))
            } else {
                currentState
            }

        is PartialState.DraftChanged -> currentState.copy(
            draftWorkshopId = partialState.workshopId ?: currentState.draftWorkshopId,
            draftBranchCode = partialState.branchCode ?: currentState.draftBranchCode,
        )

        is PartialState.Applied ->
            currentState.copy(applied = partialState.filter, didAutoSwitchTab = false)
        PartialState.Cleared -> currentState.copy(
            applied = null,
            didAutoSwitchTab = false,
            draftWorkshopId = "",
            draftBranchCode = "",
            list = PagedListState(),
        )

        is PartialState.PickerOpenChanged -> currentState.copy(isPickerOpen = partialState.isOpen)
        is PartialState.WorkshopIdErrorChanged ->
            currentState.copy(showWorkshopIdError = partialState.isVisible)

        is PartialState.BranchCodeErrorChanged ->
            currentState.copy(showBranchCodeError = partialState.isVisible)

        // The rows for the new tab are already in state — the fallback load emitted them before
        // this — so only the tab and its notice move.
        is PartialState.AutoSwitchedTab ->
            currentState.copy(tab = partialState.tab, didAutoSwitchTab = true)

        is PartialState.MyWorkshopsLoaded -> currentState.copy(
            myWorkshops = partialState.workshops,
            myWorkshopsTotal = partialState.total,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
