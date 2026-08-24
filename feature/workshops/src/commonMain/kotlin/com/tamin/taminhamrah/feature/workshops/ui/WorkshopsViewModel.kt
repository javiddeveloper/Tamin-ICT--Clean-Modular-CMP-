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
import com.tamin.taminhamrah.mapper.common.toCityPresentation
import com.tamin.taminhamrah.mapper.common.toProvincePresentation
import com.tamin.taminhamrah.mapper.contracts.toBranchPresentation
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.model.workshop.Article16DebtQuery
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.model.workshop.WorkshopListQuery
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.common.GetCitiesUseCase
import com.tamin.taminhamrah.useCases.common.GetProvincesUseCase
import com.tamin.taminhamrah.useCases.contracts.GetBranchesUseCase
import com.tamin.taminhamrah.useCases.workshops.GetArticle16DebtsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetEmployerAgreementsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import taminx.core.core_ui.Res
import taminx.core.core_ui.workshop_error_receive_data
import taminx.core.core_ui.workshop_no_debt_found

class WorkshopsViewModel(
    private val getEmployerAgreements: GetEmployerAgreementsUseCase,
    private val getArticle16Debts: GetArticle16DebtsUseCase,
    private val getProvincesUseCase: GetProvincesUseCase,
    private val getCitiesUseCase: GetCitiesUseCase,
    private val getBranchesUseCase: GetBranchesUseCase,
) : BaseViewModel<WorkshopsUiState, PartialState, WorkshopsEvent, WorkshopsIntent>(
    initialState = WorkshopsUiState()
) {

    init {
        sendIntent(WorkshopsIntent.Load)
        sendIntent(WorkshopsIntent.LoadProvinces)
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

        WorkshopsIntent.LoadProvinces -> loadProvinces()
        WorkshopsIntent.RetryCities -> loadCities(uiState.value.branchSelection.provinceCode)
        WorkshopsIntent.RetryBranches -> loadBranches(uiState.value.branchSelection.cityCode)
        is WorkshopsIntent.SelectProvince -> handleSelectProvince(intent.province)
        is WorkshopsIntent.SelectCity -> handleSelectCity(intent.city)
        is WorkshopsIntent.SelectBranch -> handleSelectBranch(intent.branch)
    }

    private fun loadPage(
        page: Int,
        search: WorkshopSearch = uiState.value.appliedSearch,
        status: WorkshopActivityStatus? = uiState.value.statusFilter,
    ): Flow<PartialState> = flow {
        emit(if (page == 0) PartialState.Loading else PartialState.LoadingMore)

        val resolvedBranchCode = search.branchCode.ifBlank { uiState.value.branchSelection.branchCode }
        val result = getEmployerAgreements(
            WorkshopListQuery(
                workshopId = search.workshopId.takeIf { it.isNotBlank() },
                branchCode = resolvedBranchCode.takeIf { it.isNotBlank() },
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
        emit(PartialState.QueryApplied(WorkshopSearch(), status))
        emitAll(loadPage(page = 0, search = WorkshopSearch(), status = status))
    }

    private fun applyStatusFilter(status: WorkshopActivityStatus?): Flow<PartialState> = flow {
        val search = uiState.value.appliedSearch
        emit(PartialState.QueryApplied(search, status))
        emit(PartialState.FilterSheetOpenChanged(false))
        emitAll(loadPage(page = 0, search = search, status = status))
    }

    private fun loadProvinces(): Flow<PartialState> = flow {
        emit(PartialState.ProvincesLoading(true))
        emit(PartialState.ProvincesError(null))
        try {
            getProvincesUseCase().collect { provinces ->
                emit(PartialState.ProvincesLoaded(provinces.toProvincePresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.ProvincesError(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.ProvincesLoading(false))
        }
    }

    private fun loadCities(provinceCode: String): Flow<PartialState> = flow {
        if (provinceCode.isBlank()) return@flow
        emit(PartialState.CitiesLoading(true))
        emit(PartialState.CitiesError(null))
        try {
            getCitiesUseCase(provinceCode = provinceCode).collect { cities ->
                emit(PartialState.CitiesLoaded(cities.toCityPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.CitiesError(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.CitiesLoading(false))
        }
    }

    private fun loadBranches(cityCode: String): Flow<PartialState> = flow {
        if (cityCode.isBlank()) return@flow
        emit(PartialState.BranchesLoading(true))
        emit(PartialState.BranchesError(null))
        try {
            getBranchesUseCase(cityCode).collect { branches ->
                emit(PartialState.BranchesLoaded(branches.toBranchPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.BranchesError(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.BranchesLoading(false))
        }
    }

    private fun handleSelectProvince(province: ProvincePR): Flow<PartialState> = flow {
        emit(
            PartialState.BranchSelectionChanged(
                uiState.value.branchSelection.copy(
                    provinceCode = province.provinceCode,
                    provinceName = province.provinceName,
                    cityCode = "",
                    cityName = "",
                    branchCode = "",
                    branchName = "",
                ),
            ),
        )
        emit(PartialState.BranchesLoaded(emptyList()))
        emit(PartialState.BranchesError(null))
        emitAll(loadCities(province.provinceCode))
    }

    private fun handleSelectCity(city: CityPR): Flow<PartialState> = flow {
        emit(
            PartialState.BranchSelectionChanged(
                uiState.value.branchSelection.copy(
                    cityCode = city.cityCode,
                    cityName = city.cityName,
                    branchCode = "",
                    branchName = "",
                ),
            ),
        )
        emitAll(loadBranches(city.cityCode))
    }

    private fun handleSelectBranch(branch: BranchPR): Flow<PartialState> = flow {
        emit(
            PartialState.BranchSelectionChanged(
                uiState.value.branchSelection.copy(
                    branchCode = branch.code,
                    branchName = branch.name,
                ),
            ),
        )
    }

    private fun openActions(workshop: WorkshopPR): Flow<PartialState> = flow {
        if (!workshop.hasIdentity) {
            sendEvent(WorkshopsEvent.ShowMessage(Res.string.workshop_error_receive_data))
            return@flow
        }
        emit(PartialState.ActionsForChanged(workshop))
    }

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

        is PartialState.ProvincesLoading -> currentState.copy(isProvincesLoading = partialState.isLoading)
        is PartialState.ProvincesLoaded -> currentState.copy(provinces = partialState.list)
        is PartialState.CitiesLoading -> currentState.copy(isCitiesLoading = partialState.isLoading)
        is PartialState.CitiesLoaded -> currentState.copy(cities = partialState.list)
        is PartialState.BranchesLoading -> currentState.copy(isBranchesLoading = partialState.isLoading)
        is PartialState.BranchesLoaded -> currentState.copy(branches = partialState.list)
        is PartialState.BranchSelectionChanged -> currentState.copy(branchSelection = partialState.selection)
        is PartialState.ProvincesError -> currentState.copy(provincesError = partialState.message)
        is PartialState.CitiesError -> currentState.copy(citiesError = partialState.message)
        is PartialState.BranchesError -> currentState.copy(branchesError = partialState.message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
