package com.tamin.taminhamrah.feature.workshops.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsEvent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsIntent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsUiState
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsUiState.PartialState
import com.tamin.taminhamrah.mapper.common.toCityPresentation
import com.tamin.taminhamrah.mapper.common.toProvincePresentation
import com.tamin.taminhamrah.mapper.contracts.toBranchPresentation
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.useCases.common.GetCitiesUseCase
import com.tamin.taminhamrah.useCases.common.GetProvincesUseCase
import com.tamin.taminhamrah.useCases.contracts.GetBranchesUseCase
import com.tamin.taminhamrah.useCases.workshops.GetAllEmployerAgreementByNationalIdUseCase
import com.tamin.taminhamrah.useCases.common.GetRegistrationDeclarationFormUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WorkshopsViewModel(
    private val getAllEmployerAgreementUseCase: GetAllEmployerAgreementByNationalIdUseCase,
    private val getRegistrationDeclarationFormUseCase: GetRegistrationDeclarationFormUseCase,
    private val getProvincesUseCase: GetProvincesUseCase,
    private val getCitiesUseCase: GetCitiesUseCase,
    private val getBranchesUseCase: GetBranchesUseCase,
) : BaseViewModel<WorkshopsUiState, PartialState, WorkshopsEvent, WorkshopsIntent>(
    initialState = WorkshopsUiState()
) {

    init {
        sendIntent(WorkshopsIntent.LoadWorkshops())
        sendIntent(WorkshopsIntent.LoadProvinces)
    }

    override fun handleIntent(intent: WorkshopsIntent): Flow<PartialState> {
        return when (intent) {
            is WorkshopsIntent.LoadWorkshops -> handleLoadWorkshops(
                intent.workshopId,
                intent.branchCode,
                intent.workshopStatus
            )
            WorkshopsIntent.LoadProvinces -> loadProvinces()
            is WorkshopsIntent.SelectProvince -> handleSelectProvince(intent.province)
            is WorkshopsIntent.SelectCity -> handleSelectCity(intent.city)
            is WorkshopsIntent.SelectBranch -> handleSelectBranch(intent.branch)
            WorkshopsIntent.TestDownloadPdf -> handleTestDownloadPdf()
        }
    }

    private fun handleTestDownloadPdf(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getRegistrationDeclarationFormUseCase().collect { response ->
                sendEvent(WorkshopsEvent.ShowToast(" ${response.size}"))
            }

            emit(PartialState.Loading(false))
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun loadProvinces(): Flow<PartialState> = flow {
        emit(PartialState.ProvincesLoading(true))
        try {
            getProvincesUseCase().collect { provinces ->
                emit(PartialState.ProvincesLoaded(provinces.toProvincePresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        } finally {
            emit(PartialState.ProvincesLoading(false))
        }
    }

    /**
     * Picking a province clears the city and branch below it: keeping a stale branchCode
     * from the previous province would silently filter the list by a branch the user can
     * no longer see selected.
     */
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
        emit(PartialState.CitiesLoading(true))
        try {
            getCitiesUseCase(provinceCode = province.provinceCode).collect { cities ->
                emit(PartialState.CitiesLoaded(cities.toCityPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        } finally {
            emit(PartialState.CitiesLoading(false))
        }
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
        emit(PartialState.BranchesLoading(true))
        try {
            getBranchesUseCase(city.cityCode).collect { branches ->
                emit(PartialState.BranchesLoaded(branches.toBranchPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        } finally {
            emit(PartialState.BranchesLoading(false))
        }
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

    private fun handleLoadWorkshops(
        workshopId: String?,
        branchCode: String?,
        workshopStatus: String?
    ): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val filters = mutableListOf<ApiFilterDN>()

            workshopId?.takeIf { it.isNotEmpty() }?.let {
                filters.add(ApiFilterDN(FilterProperty.WORKSHOP_ID, it, FilterOperator.EQ))
            }

            branchCode?.takeIf { it.isNotEmpty() }?.let {
                filters.add(ApiFilterDN(FilterProperty.WORKSHOP_BRANCH_CODE, it, FilterOperator.EQ))
            }

            workshopStatus?.takeIf { it.isNotEmpty() }?.let {
                filters.add(ApiFilterDN(FilterProperty.WORKSHOP_STATUS_CODE, it, FilterOperator.EQ))
            }

            val response = getAllEmployerAgreementUseCase(filters = filters)
            val list = response?.list?.map { it.toPresentation() } ?: emptyList()
            emit(PartialState.WorkshopsLoaded(list))

        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    override fun reduceState(
        currentState: WorkshopsUiState,
        partialState: PartialState
    ): WorkshopsUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.WorkshopsLoaded -> currentState.copy(
            isLoading = false,
            agreements = partialState.list
        )

        is PartialState.ProvincesLoading ->
            currentState.copy(isProvincesLoading = partialState.isLoading)
        is PartialState.ProvincesLoaded -> currentState.copy(provinces = partialState.list)
        is PartialState.CitiesLoading ->
            currentState.copy(isCitiesLoading = partialState.isLoading)
        is PartialState.CitiesLoaded -> currentState.copy(cities = partialState.list)
        is PartialState.BranchesLoading ->
            currentState.copy(isBranchesLoading = partialState.isLoading)
        is PartialState.BranchesLoaded -> currentState.copy(branches = partialState.list)
        is PartialState.BranchSelectionChanged ->
            currentState.copy(branchSelection = partialState.selection)
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
