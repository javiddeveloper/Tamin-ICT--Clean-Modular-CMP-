package com.tamin.taminhamrah.feature.historyobjection.ui.stepper

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.HistoryObjectionBottomSheetTarget
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.HistoryObjectionStepperEvent
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.HistoryObjectionStepperIntent
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.HistoryObjectionStepperState
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.HistoryObjectionStepperState.PartialState
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.STEP_BRANCH
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.STEP_RECORD
import com.tamin.taminhamrah.mapper.common.toCityPresentation
import com.tamin.taminhamrah.mapper.common.toInsuranceTypePresentation
import com.tamin.taminhamrah.mapper.common.toProvincePresentation
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.InsuranceTypePR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.historyObjection.SaveNotExistRequestDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetConfig
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetItem
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetType
import com.tamin.taminhamrah.useCases.contracts.GetBranchesUseCase
import com.tamin.taminhamrah.useCases.common.GetCitiesByProvinceUseCase
import com.tamin.taminhamrah.useCases.common.GetInsuranceTypesUseCase
import com.tamin.taminhamrah.useCases.common.GetProvincesUseCase
import com.tamin.taminhamrah.useCases.historyObjection.GetHistoryObjectionNotExistRequestsUseCase
import com.tamin.taminhamrah.useCases.historyObjection.SaveHistoryObjectionNotExistRequestUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.currentTimeMillis
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.bs_branch
import taminx.core.core_ui.bs_city
import taminx.core.core_ui.bs_insurance_type
import taminx.core.core_ui.bs_province
import taminx.core.core_ui.history_objection_date_range_invalid_error
import taminx.core.core_ui.history_objection_date_too_recent_error
import taminx.core.core_ui.history_objection_select_city_first
import taminx.core.core_ui.history_objection_select_province_first
import taminx.core.core_ui.history_objection_submit_missing_data_error
import taminx.core.core_ui.history_objection_work_days_exceeds_range_error
import taminx.core.core_ui.search_hint

/** The end date must be at least this many days before today — see [dateRangeErrorMessage]. */
private const val MIN_DAYS_BETWEEN_END_DATE_AND_TODAY = 60

class HistoryObjectionStepperViewModel(
    private val getProvincesUseCase: GetProvincesUseCase,
    private val getCitiesByProvinceUseCase: GetCitiesByProvinceUseCase,
    private val getBranchesUseCase: GetBranchesUseCase,
    private val getInsuranceTypesUseCase: GetInsuranceTypesUseCase,
    private val getHistoryObjectionNotExistRequestsUseCase: GetHistoryObjectionNotExistRequestsUseCase,
    private val saveHistoryObjectionNotExistRequestUseCase: SaveHistoryObjectionNotExistRequestUseCase,
) : BaseViewModel<HistoryObjectionStepperState, PartialState, HistoryObjectionStepperEvent, HistoryObjectionStepperIntent>(
    initialState = HistoryObjectionStepperState()
) {

    override fun handleIntent(intent: HistoryObjectionStepperIntent): Flow<PartialState> = when (intent) {
        is HistoryObjectionStepperIntent.Load -> loadInitialData(intent.editRequestNumber, intent.editRowIndex)

        HistoryObjectionStepperIntent.OnNextClicked -> flow {
            val state = uiState.value
            if (!state.canGoNextFromCurrentStep || state.currentStep == STEP_RECORD) return@flow
            emit(PartialState.StepChanged(state.currentStep + 1))
        }

        HistoryObjectionStepperIntent.OnBackClicked -> flow {
            val step = uiState.value.currentStep
            if (step == STEP_BRANCH) {
                sendEvent(HistoryObjectionStepperEvent.NavigateBack)
            } else {
                emit(PartialState.StepChanged(step - 1))
            }
        }

        HistoryObjectionStepperIntent.OnConfirmClicked -> handleConfirmClicked()

        HistoryObjectionStepperIntent.OnSubmitSuccessAcknowledged -> flow {
            sendEvent(HistoryObjectionStepperEvent.NavigateBack)
        }

        HistoryObjectionStepperIntent.OnErrorDismissed -> flow {
            emit(PartialState.ErrorDismissed)
        }

        HistoryObjectionStepperIntent.OnShowProvincePicker -> flow {
            val state = uiState.value
            emit(
                PartialState.BottomSheetStateChanged(
                    config = TaminBottomSheetConfig(
                        title = getString(Res.string.bs_province),
                        type = TaminBottomSheetType.PROVINCE,
                        items = state.provinces.mapIndexed { index, province ->
                            TaminBottomSheetItem(
                                id = index,
                                title = province.provinceName,
                                isSelected = province == state.selectedProvince,
                            )
                        },
                        singleSelection = true,
                        showSearchInput = true,
                        searchInputHint = getString(Res.string.search_hint),
                    ),
                    target = HistoryObjectionBottomSheetTarget.PROVINCE,
                )
            )
        }

        HistoryObjectionStepperIntent.OnShowCityPicker -> flow {
            val state = uiState.value
            if (state.selectedProvince == null) {
                sendEvent(HistoryObjectionStepperEvent.ShowMessage(getString(Res.string.history_objection_select_province_first)))
                return@flow
            }
            emit(
                PartialState.BottomSheetStateChanged(
                    config = TaminBottomSheetConfig(
                        title = getString(Res.string.bs_city),
                        type = TaminBottomSheetType.CITY,
                        items = state.cities.mapIndexed { index, city ->
                            TaminBottomSheetItem(
                                id = index,
                                title = city.cityName,
                                isSelected = city == state.selectedCity,
                            )
                        },
                        singleSelection = true,
                        showSearchInput = true,
                        searchInputHint = getString(Res.string.search_hint),
                    ),
                    target = HistoryObjectionBottomSheetTarget.CITY,
                )
            )
        }

        HistoryObjectionStepperIntent.OnShowBranchPicker -> flow {
            val state = uiState.value
            if (state.selectedProvince == null) {
                sendEvent(HistoryObjectionStepperEvent.ShowMessage(getString(Res.string.history_objection_select_province_first)))
                return@flow
            }
            if (state.selectedCity == null) {
                sendEvent(HistoryObjectionStepperEvent.ShowMessage(getString(Res.string.history_objection_select_city_first)))
                return@flow
            }
            emit(
                PartialState.BottomSheetStateChanged(
                    config = TaminBottomSheetConfig(
                        title = getString(Res.string.bs_branch),
                        type = TaminBottomSheetType.BRANCH,
                        items = state.branches.mapIndexed { index, branch ->
                            TaminBottomSheetItem(
                                id = index,
                                title = branch.displayName,
                                isSelected = branch == state.selectedBranch,
                            )
                        },
                        singleSelection = true,
                        showSearchInput = true,
                        searchInputHint = getString(Res.string.search_hint),
                    ),
                    target = HistoryObjectionBottomSheetTarget.BRANCH,
                )
            )
        }

        HistoryObjectionStepperIntent.OnShowInsuranceTypePicker -> flow {
            val state = uiState.value
            emit(
                PartialState.BottomSheetStateChanged(
                    config = TaminBottomSheetConfig(
                        title = getString(Res.string.bs_insurance_type),
                        type = TaminBottomSheetType.INSURANCE_TYPE,
                        items = state.insuranceTypes.mapIndexed { index, insuranceType ->
                            TaminBottomSheetItem(
                                id = index,
                                title = insuranceType.insuranceTypeDesc,
                                isSelected = insuranceType == state.selectedInsuranceType,
                            )
                        },
                        singleSelection = true,
                        showSearchInput = true,
                        searchInputHint = getString(Res.string.search_hint),
                    ),
                    target = HistoryObjectionBottomSheetTarget.INSURANCE_TYPE,
                )
            )
        }

        HistoryObjectionStepperIntent.OnDismissBottomSheet -> flow {
            emit(PartialState.BottomSheetStateChanged(config = null, target = null))
        }

        is HistoryObjectionStepperIntent.OnProvinceSelected -> handleProvinceSelected(intent.province)
        is HistoryObjectionStepperIntent.OnCitySelected -> handleCitySelected(intent.city)
        is HistoryObjectionStepperIntent.OnBranchSelected -> flow {
            emit(PartialState.BranchSelected(intent.branch))
            emit(PartialState.BottomSheetStateChanged(config = null, target = null))
        }
        is HistoryObjectionStepperIntent.OnInsuranceTypeSelected -> flow {
            emit(PartialState.InsuranceTypeSelected(intent.insuranceType))
            emit(PartialState.BottomSheetStateChanged(config = null, target = null))
        }

        is HistoryObjectionStepperIntent.OnWorkshopIdChanged -> flow {
            emit(PartialState.WorkshopIdChanged(intent.value))
        }
        is HistoryObjectionStepperIntent.OnWorkshopNameChanged -> flow {
            emit(PartialState.WorkshopNameChanged(intent.value))
        }
        is HistoryObjectionStepperIntent.OnEmployerNameChanged -> flow {
            emit(PartialState.EmployerNameChanged(intent.value))
        }
        is HistoryObjectionStepperIntent.OnWorkshopAddressChanged -> flow {
            emit(PartialState.WorkshopAddressChanged(intent.value))
        }

        is HistoryObjectionStepperIntent.OnStartDateSelected -> flow {
            emit(
                PartialState.StartDateSelected(
                    label = PersianDateFormatter.format(intent.year, intent.month, intent.day),
                    timestamp = PersianDateFormatter.toEpochMillis(intent.year, intent.month, intent.day),
                )
            )
        }
        is HistoryObjectionStepperIntent.OnEndDateSelected -> flow {
            emit(
                PartialState.EndDateSelected(
                    label = PersianDateFormatter.format(intent.year, intent.month, intent.day),
                    timestamp = PersianDateFormatter.toEpochMillis(intent.year, intent.month, intent.day),
                )
            )
        }
        is HistoryObjectionStepperIntent.OnWorkDaysChanged -> flow {
            emit(PartialState.WorkDaysChanged(intent.value))
        }
    }

    private fun handleProvinceSelected(province: ProvincePR): Flow<PartialState> = flow {
        emit(PartialState.ProvinceSelected(province))
        emit(PartialState.BottomSheetStateChanged(config = null, target = null))
        emit(PartialState.CitiesLoading(true))
        try {
            getCitiesByProvinceUseCase(province.provinceCode).collect { cities ->
                emit(PartialState.CitiesLoaded(cities.toCityPresentation().toPersistentList()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.CitiesLoading(false))
        }
    }

    private fun handleCitySelected(city: CityPR): Flow<PartialState> = flow {
        emit(PartialState.CitySelected(city))
        emit(PartialState.BottomSheetStateChanged(config = null, target = null))
        emit(PartialState.BranchesLoading(true))
        try {
            getBranchesUseCase(city.cityCode).collect { branches ->
                emit(PartialState.BranchesLoaded(branches.toPersistentList()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.BranchesLoading(false))
        }
    }

    private fun handleConfirmClicked(): Flow<PartialState> = flow {
        if (uiState.value.isSubmitting) return@flow

        val request = uiState.value.toSaveNotExistRequestDN()
        if (request == null) {
            sendEvent(HistoryObjectionStepperEvent.ShowMessage(getString(Res.string.history_objection_submit_missing_data_error)))
            return@flow
        }

        uiState.value.dateRangeErrorMessage()?.let { errorRes ->
            sendEvent(HistoryObjectionStepperEvent.ShowMessage(getString(errorRes)))
            return@flow
        }

        emit(PartialState.ErrorDismissed)
        emit(PartialState.Submitting(true))
        try {
            saveHistoryObjectionNotExistRequestUseCase(request).first()
            emit(PartialState.SubmitSucceeded)
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.Submitting(false))
        }
    }

    private fun HistoryObjectionStepperState.toSaveNotExistRequestDN(): SaveNotExistRequestDN? {
        val branch = selectedBranch ?: return null
        val branchCode = branch.code ?: return null
        val city = selectedCity ?: return null
        val province = selectedProvince ?: return null
        val insuranceType = selectedInsuranceType ?: return null
        val start = startDateTimestamp ?: return null
        val end = endDateTimestamp ?: return null
        if (workshopId.isBlank() || workshopName.isBlank() || employerName.isBlank() ||
            workshopAddress.isBlank() || workDays.isBlank()
        ) {
            return null
        }

        return SaveNotExistRequestDN(
            branchCode = branchCode,
            branchName = branch.name.orEmpty(),
            cityCode = city.cityCode,
            cityName = city.cityName,
            endDate = end,
            insuranceType = insuranceType.insuranceTypeCode,
            provinceCode = province.provinceCode,
            provinceName = province.provinceName,
            workshopId = workshopId,
            workshopName = workshopName,
            workshopManager = employerName,
            workshopAddress = workshopAddress,
            startDate = start,
            workDays = workDays,
        )
    }

    private fun HistoryObjectionStepperState.dateRangeErrorMessage(): StringResource? {
        val start = startDateTimestamp ?: return null
        val end = endDateTimestamp ?: return null

        val diffDay = PersianDateFormatter.daysBetween(start, end)
        val diffFromToday = PersianDateFormatter.daysBetween(end, currentTimeMillis())

        return when {
            diffDay <= 0 -> Res.string.history_objection_date_range_invalid_error
            diffFromToday < MIN_DAYS_BETWEEN_END_DATE_AND_TODAY -> Res.string.history_objection_date_too_recent_error
            workDays.toIntOrNull()?.let { diffDay < it } == true -> Res.string.history_objection_work_days_exceeds_range_error
            else -> null
        }
    }

    private fun loadInitialData(editRequestNumber: String?, editRowIndex: String?): Flow<PartialState> = flow {
        emit(
            PartialState.ModeInitialized(
                isEditMode = editRequestNumber != null,
                editRequestNumber = editRequestNumber,
                editRowIndex = editRowIndex,
            )
        )
        emit(PartialState.Loading(true))
        try {
            emitAll(merge(loadProvinces(), loadInsuranceTypes()))
            if (editRequestNumber != null) {
                loadEditModeData(editRequestNumber, editRowIndex)?.let { editData ->
                    emit(editData)
                    emitAll(loadEditModeCitiesAndBranches(editData.provinceCode, editData.cityCode))
                }
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        }
        emit(PartialState.Loading(false))
    }

    private fun loadProvinces(): Flow<PartialState> = flow {
        val provinces = getProvincesUseCase().first().toProvincePresentation()
        emit(PartialState.ProvincesLoaded(provinces.toPersistentList()))
    }

    private fun loadInsuranceTypes(): Flow<PartialState> = flow {
        val insuranceTypes = getInsuranceTypesUseCase().first().toInsuranceTypePresentation()
        emit(PartialState.InsuranceTypesLoaded(insuranceTypes.toPersistentList()))
    }

    private fun loadEditModeCitiesAndBranches(provinceCode: String?, cityCode: String?): Flow<PartialState> = flow {
        if (provinceCode != null) {
            emit(PartialState.CitiesLoading(true))
            try {
                getCitiesByProvinceUseCase(provinceCode).collect { cities ->
                    emit(PartialState.CitiesLoaded(cities.toCityPresentation().toPersistentList()))
                }
            } catch (e: Exception) {
                emit(PartialState.Error(e.toSingleLineMessage()))
            } finally {
                emit(PartialState.CitiesLoading(false))
            }
        }
        if (cityCode != null) {
            emit(PartialState.BranchesLoading(true))
            try {
                getBranchesUseCase(cityCode).collect { branches ->
                    emit(PartialState.BranchesLoaded(branches.toPersistentList()))
                }
            } catch (e: Exception) {
                emit(PartialState.Error(e.toSingleLineMessage()))
            } finally {
                emit(PartialState.BranchesLoading(false))
            }
        }
    }

    private suspend fun loadEditModeData(requestNumber: String, rowIndex: String?): PartialState.EditModeDataLoaded? {
        val match = getHistoryObjectionNotExistRequestsUseCase().first()
            .firstOrNull { it.requestNumber == requestNumber && (rowIndex == null || it.rowIndex == rowIndex) }
            ?: return null

        val branch = match.branchCode?.let { code ->
            BranchDN(
                code = code,
                name = match.branchName,
                branchAddress = "",
                cityCode = match.cityCode,
                minCode = null,
                maxCode = null,
            )
        }
        val insuranceType = match.insuranceType?.let { code ->
            InsuranceTypePR(insuranceTypeCode = code, insuranceTypeDesc = match.insuranceTypeDesc.orEmpty())
        }

        return PartialState.EditModeDataLoaded(
            provinceCode = match.provinceCode,
            provinceName = match.provinceName,
            cityCode = match.cityCode,
            cityName = match.cityName,
            branch = branch,
            insuranceType = insuranceType,
            workshopId = match.workshopId.orEmpty(),
            workshopName = match.workshopName.orEmpty(),
            employerName = match.workshopManager.orEmpty(),
            workshopAddress = match.workshopAddress.orEmpty(),
            startDateTimestamp = match.startDate,
            endDateTimestamp = match.endDate,
            workDays = match.workDays.orEmpty(),
        )
    }

    override fun reduceState(
        currentState: HistoryObjectionStepperState,
        partialState: PartialState,
    ): HistoryObjectionStepperState = when (partialState) {
        is PartialState.ModeInitialized -> currentState.copy(
            isEditMode = partialState.isEditMode,
            editRequestNumber = partialState.editRequestNumber,
            editRowIndex = partialState.editRowIndex,
        )
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Submitting -> currentState.copy(isSubmitting = partialState.isSubmitting)
        PartialState.SubmitSucceeded -> currentState.copy(hasSubmitted = true)
        is PartialState.Error -> currentState.copy(error = partialState.message)
        PartialState.ErrorDismissed -> currentState.copy(error = null)
        is PartialState.StepChanged -> currentState.copy(currentStep = partialState.step)

        is PartialState.ProvincesLoaded -> currentState.copy(provinces = partialState.provinces)
        is PartialState.CitiesLoading -> currentState.copy(isCitiesLoading = partialState.isLoading)
        is PartialState.CitiesLoaded -> currentState.copy(cities = partialState.cities)
        is PartialState.BranchesLoading -> currentState.copy(isBranchesLoading = partialState.isLoading)
        is PartialState.BranchesLoaded -> currentState.copy(branches = partialState.branches)
        is PartialState.InsuranceTypesLoaded -> currentState.copy(insuranceTypes = partialState.insuranceTypes)

        is PartialState.ProvinceSelected -> currentState.copy(
            selectedProvince = partialState.province,
            selectedCity = null,
            selectedBranch = null,
            cities = persistentListOf(),
            branches = persistentListOf(),
        )
        is PartialState.CitySelected -> currentState.copy(
            selectedCity = partialState.city,
            selectedBranch = null,
            branches = persistentListOf(),
        )
        is PartialState.BranchSelected -> currentState.copy(selectedBranch = partialState.branch)
        is PartialState.InsuranceTypeSelected -> currentState.copy(selectedInsuranceType = partialState.insuranceType)

        is PartialState.BottomSheetStateChanged -> currentState.copy(
            bottomSheetConfig = partialState.config,
            bottomSheetTarget = partialState.target,
        )

        is PartialState.WorkshopIdChanged -> currentState.copy(workshopId = partialState.value)
        is PartialState.WorkshopNameChanged -> currentState.copy(workshopName = partialState.value)
        is PartialState.EmployerNameChanged -> currentState.copy(employerName = partialState.value)
        is PartialState.WorkshopAddressChanged -> currentState.copy(workshopAddress = partialState.value)

        is PartialState.StartDateSelected -> currentState.copy(
            startDateLabel = partialState.label,
            startDateTimestamp = partialState.timestamp,
        )
        is PartialState.EndDateSelected -> currentState.copy(
            endDateLabel = partialState.label,
            endDateTimestamp = partialState.timestamp,
        )
        is PartialState.WorkDaysChanged -> currentState.copy(workDays = partialState.value)

        is PartialState.EditModeDataLoaded -> currentState.copy(
            selectedProvince = partialState.provinceCode?.let {
                ProvincePR(provinceCode = it, provinceName = partialState.provinceName.orEmpty())
            },
            selectedCity = partialState.cityCode?.let {
                CityPR(cityCode = it, cityName = partialState.cityName.orEmpty(), provinceCode = partialState.provinceCode)
            },
            selectedBranch = partialState.branch,
            selectedInsuranceType = partialState.insuranceType,
            workshopId = partialState.workshopId,
            workshopName = partialState.workshopName,
            employerName = partialState.employerName,
            workshopAddress = partialState.workshopAddress,
            startDateTimestamp = partialState.startDateTimestamp,
            startDateLabel = PersianDateFormatter.formatTimestamp(partialState.startDateTimestamp),
            endDateTimestamp = partialState.endDateTimestamp,
            endDateLabel = PersianDateFormatter.formatTimestamp(partialState.endDateTimestamp),
            workDays = partialState.workDays,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
