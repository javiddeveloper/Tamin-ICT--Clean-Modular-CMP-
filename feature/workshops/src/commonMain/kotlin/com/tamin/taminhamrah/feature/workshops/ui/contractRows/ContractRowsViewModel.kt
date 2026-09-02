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
        is ContractRowsIntent.PickerOpenChanged -> setPickerOpen(intent.isOpen)
        is ContractRowsIntent.DraftWorkshopIdChanged -> flow {
            emit(PartialState.DraftChanged(workshopId = intent.value))
            // The message clears the moment the field it names is edited.
            emit(PartialState.WorkshopIdErrorChanged(isVisible = false))
        }

        is ContractRowsIntent.DraftBranchCodeChanged ->
            flow { emit(PartialState.DraftChanged(branchCode = intent.value)) }

        is ContractRowsIntent.QuickPicked -> flow {
            emit(
                PartialState.DraftChanged(
                    workshopId = intent.workshopId,
                    branchCode = intent.branchCode,
                )
            )
            emit(PartialState.WorkshopIdErrorChanged(isVisible = false))
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
        if (intent.workshopId.isBlank()) {
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
        emitAll(loadPage(page = 0, filter = filter))
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
        emitAll(loadPage(page = 0, filter = filter, tab = tab))
    }

    private fun loadPage(
        page: Int,
        filter: ContractRowFilter? = uiState.value.applied,
        tab: ContractRowTab = uiState.value.tab,
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
        emit(PartialState.Loaded(list))
    }.catch { emit(PartialState.Error(it.toSingleLineMessage())) }

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
        if (state.myWorkshops.isNotEmpty()) return@flow

        val workshops = getMyWorkshops(WorkshopListQuery(page = 0))
        emit(
            PartialState.MyWorkshopsLoaded(
                workshops.items.map { it.toPresentation() }.toImmutableList()
            )
        )
    }.catch {
        // کارگاه‌های شما is a convenience above two fields that already work. Failing to fetch it
        // must not put an error on the list the user has not asked for yet, so it is swallowed and
        // the section simply does not appear.
        emit(PartialState.MyWorkshopsLoaded(uiState.value.myWorkshops))
    }

    /**
     * کد کارگاه is the one required field — it is a path segment, so a blank one would request
     * a different route entirely. Submitting without it surfaces the message at the field rather
     * than doing nothing.
     */
    private fun applyPicker(): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.list.isLoading) return@flow

        val workshopId = state.draftWorkshopId.trim()
        if (workshopId.isBlank()) {
            emit(PartialState.WorkshopIdErrorChanged(isVisible = true))
            return@flow
        }

        val filter = ContractRowFilter(workshopId, state.draftBranchCode.trim())
        emit(PartialState.WorkshopIdErrorChanged(isVisible = false))
        emit(PartialState.Applied(filter))
        emit(PartialState.PickerOpenChanged(isOpen = false))
        emitAll(loadPage(page = 0, filter = filter))
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
            // The other service's rows are not this tab's rows; keeping them would show the
            // previous tab's list under the new tab's heading until the request lands.
            list = PagedListState(),
        )

        PartialState.Loading -> currentState.copy(list = currentState.list.loading())
        PartialState.LoadingMore -> currentState.copy(list = currentState.list.loadingMore())
        is PartialState.Error ->
            currentState.copy(list = currentState.list.failed(partialState.message))

        is PartialState.Loaded -> currentState.copy(list = partialState.list)
        is PartialState.DraftChanged -> currentState.copy(
            draftWorkshopId = partialState.workshopId ?: currentState.draftWorkshopId,
            draftBranchCode = partialState.branchCode ?: currentState.draftBranchCode,
        )

        is PartialState.Applied -> currentState.copy(applied = partialState.filter)
        PartialState.Cleared -> currentState.copy(
            applied = null,
            draftWorkshopId = "",
            draftBranchCode = "",
            list = PagedListState(),
        )

        is PartialState.PickerOpenChanged -> currentState.copy(isPickerOpen = partialState.isOpen)
        is PartialState.WorkshopIdErrorChanged ->
            currentState.copy(showWorkshopIdError = partialState.isVisible)

        is PartialState.MyWorkshopsLoaded ->
            currentState.copy(myWorkshops = partialState.workshops)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
