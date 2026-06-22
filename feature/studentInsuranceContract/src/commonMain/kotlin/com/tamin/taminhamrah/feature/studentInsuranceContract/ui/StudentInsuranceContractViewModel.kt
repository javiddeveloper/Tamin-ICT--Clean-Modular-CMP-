package com.tamin.taminhamrah.feature.studentInsuranceContract.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract.StudentInsuranceContractEvent
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract.StudentInsuranceContractIntent
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract.StudentInsuranceContractUiState
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract.StudentInsuranceContractUiState.PartialState
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper.toSpcPremiumRateOptions
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper.resolveEligibility
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper.filterByProvinceCode
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper.toBranchOptions
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper.toCityOptions
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper.toProvinceOptions
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.CityOptionPR
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.ContractApplicantType
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.SpcPremiumRateOptionPR
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.StudentInsuranceContractStep
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.UserInfoFormPR
import com.tamin.taminhamrah.mapper.contracts.toPresentation
import com.tamin.taminhamrah.useCases.common.GetCitiesUseCase
import com.tamin.taminhamrah.useCases.common.GetProvincesUseCase
import com.tamin.taminhamrah.useCases.contracts.GetBranchesUseCase
import com.tamin.taminhamrah.useCases.contracts.GetContractsUseCase
import com.tamin.taminhamrah.useCases.contracts.GetRegistrationInfoUseCase
import com.tamin.taminhamrah.useCases.contracts.GetSpcPremiumRatesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

class StudentInsuranceContractViewModel(
    private val getRegistrationInfoUseCase: GetRegistrationInfoUseCase,
    private val getContractsUseCase: GetContractsUseCase,
    private val getCitiesUseCase: GetCitiesUseCase,
    private val getProvincesUseCase: GetProvincesUseCase,
    private val getBranchesUseCase: GetBranchesUseCase,
    private val getSpcPremiumRatesUseCase: GetSpcPremiumRatesUseCase,
) : BaseViewModel<
    StudentInsuranceContractUiState,
    PartialState,
    StudentInsuranceContractEvent,
    StudentInsuranceContractIntent,
    >(
    initialState = StudentInsuranceContractUiState(),
) {
    override fun handleIntent(intent: StudentInsuranceContractIntent): Flow<PartialState> {
        return when (intent) {
            StudentInsuranceContractIntent.LoadInitialData -> handleLoadInitialData()
            StudentInsuranceContractIntent.GoToNextStep -> handleGoToNextStep()
            StudentInsuranceContractIntent.GoToPreviousStep -> handleGoToPreviousStep()
            is StudentInsuranceContractIntent.SetRulesConfirmed -> handleSetRulesConfirmed(intent.confirmed)
            is StudentInsuranceContractIntent.UpdateUserInfo -> handleUpdateUserInfo(intent.userInfo)
            is StudentInsuranceContractIntent.SetContractApplicantType -> handleSetContractApplicantType(intent.type)
            is StudentInsuranceContractIntent.SelectBranchProvince -> handleSelectBranchProvince(intent.province)
            is StudentInsuranceContractIntent.SelectBranchCity -> handleSelectBranchCity(intent.city)
            is StudentInsuranceContractIntent.SelectBranch -> handleSelectBranch(intent.branch)
            is StudentInsuranceContractIntent.SelectPremiumRate -> handleSelectPremiumRate(intent.rate)
        }
    }

    private fun handleLoadInitialData(): Flow<PartialState> = merge(
        loadRegistrationInfo(),
        loadContracts(),
        loadCities(),
        loadProvinces(),
        loadPremiumRates(),
    )

    private fun loadRegistrationInfo(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getRegistrationInfoUseCase().collect { info ->
                val presentation = info.toPresentation()
                emit(PartialState.RegistrationInfoLoaded(presentation))
                emit(PartialState.UserInfoChanged(UserInfoFormPR.fromRegistration(presentation)))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun loadContracts(): Flow<PartialState> = flow {
        try {
            getContractsUseCase.studentInsuranceContracts().collect { contracts ->
                emit(PartialState.EligibilityLoaded(contracts.resolveEligibility()))
                emit(PartialState.ContractsLoaded(contracts.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun loadCities(): Flow<PartialState> = flow {
        emit(PartialState.CitiesLoading(true))
        try {
            getCitiesUseCase().collect { cities ->
                emit(PartialState.CitiesLoaded(cities.toCityOptions()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        } finally {
            emit(PartialState.CitiesLoading(false))
        }
    }

    private fun loadProvinces(): Flow<PartialState> = flow {
        emit(PartialState.ProvincesLoading(true))
        try {
            getProvincesUseCase().collect { provinces ->
                emit(PartialState.ProvincesLoaded(provinces.toProvinceOptions()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        } finally {
            emit(PartialState.ProvincesLoading(false))
        }
    }

    private fun loadPremiumRates(): Flow<PartialState> = flow {
        emit(PartialState.PremiumRatesLoading(true))
        try {
            getSpcPremiumRatesUseCase().collect { rates ->
                emit(PartialState.PremiumRatesLoaded(rates.toSpcPremiumRateOptions()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        } finally {
            emit(PartialState.PremiumRatesLoading(false))
        }
    }

    private fun handleSelectBranchProvince(province: CityOptionPR): Flow<PartialState> = flow {
        emit(
            PartialState.BranchSelectionChanged(
                uiState.value.branchSelection.copy(
                    provinceCode = province.code,
                    provinceName = province.name,
                    cityCode = "",
                    cityName = "",
                    branchCode = "",
                    branchName = "",
                ),
            ),
        )
        emit(
            PartialState.BranchCitiesLoaded(
                uiState.value.cities.filterByProvinceCode(province.code),
            ),
        )
        emit(PartialState.BranchesLoaded(emptyList()))
    }

    private fun loadBranches(cityCode: String): Flow<PartialState> = flow {
        emit(PartialState.BranchesLoading(true))
        try {
            getBranchesUseCase(cityCode).collect { branches ->
                emit(PartialState.BranchesLoaded(branches.toBranchOptions()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        } finally {
            emit(PartialState.BranchesLoading(false))
        }
    }

    private fun handleGoToNextStep(): Flow<PartialState> = flow {
        val nextStep = uiState.value.currentStep.stepIndex
            .let { StudentInsuranceContractStep.entries.firstOrNull { step -> step.stepIndex == it + 1 } }
            ?: return@flow
        if (!uiState.value.canGoNext) return@flow
        emit(PartialState.StepChanged(nextStep))
    }

    private fun handleGoToPreviousStep(): Flow<PartialState> = flow {
        val previousStep = uiState.value.currentStep.stepIndex
            .let { StudentInsuranceContractStep.entries.firstOrNull { step -> step.stepIndex == it - 1 } }
            ?: return@flow
        emit(PartialState.StepChanged(previousStep))
    }

    private fun handleSetRulesConfirmed(confirmed: Boolean): Flow<PartialState> = flow {
        emit(PartialState.RulesConfirmedChanged(confirmed))
    }

    private fun handleUpdateUserInfo(userInfo: UserInfoFormPR): Flow<PartialState> = flow {
        emit(PartialState.UserInfoChanged(userInfo))
    }

    private fun handleSetContractApplicantType(type: ContractApplicantType): Flow<PartialState> = flow {
        emit(PartialState.ContractApplicantTypeChanged(type))
    }

    private fun handleSelectBranchCity(city: CityOptionPR): Flow<PartialState> = merge(
        flow {
            emit(
                PartialState.BranchSelectionChanged(
                    uiState.value.branchSelection.copy(
                        cityCode = city.code,
                        cityName = city.name,
                        branchCode = "",
                        branchName = "",
                    ),
                ),
            )
            emit(PartialState.BranchesLoaded(emptyList()))
        },
        loadBranches(city.code),
    )

    private fun handleSelectBranch(branch: CityOptionPR): Flow<PartialState> = flow {
        emit(
            PartialState.BranchSelectionChanged(
                uiState.value.branchSelection.copy(
                    branchCode = branch.code,
                    branchName = branch.name,
                ),
            ),
        )
    }

    private fun handleSelectPremiumRate(rate: SpcPremiumRateOptionPR): Flow<PartialState> = flow {
        emit(PartialState.PremiumRateSelected(rate.code))
    }

    override fun reduceState(
        currentState: StudentInsuranceContractUiState,
        partialState: PartialState,
    ): StudentInsuranceContractUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.RegistrationInfoLoaded -> currentState.copy(
            isLoading = false,
            registrationInfo = partialState.info,
        )
        is PartialState.ContractsLoaded -> currentState.copy(
            isLoading = false,
            existingContracts = partialState.contracts,
        )
        is PartialState.EligibilityLoaded -> currentState.copy(
            eligibility = partialState.eligibility,
        )
        is PartialState.RulesConfirmedChanged -> currentState.copy(
            isRulesConfirmed = partialState.confirmed,
        )
        is PartialState.UserInfoChanged -> currentState.copy(
            userInfo = partialState.userInfo,
        )
        is PartialState.CitiesLoading -> currentState.copy(
            isCitiesLoading = partialState.isLoading,
        )
        is PartialState.CitiesLoaded -> {
            val cities = partialState.cities
            currentState.copy(
                isCitiesLoading = false,
                cities = cities,
                branchCities = if (currentState.branchSelection.provinceCode.isNotBlank()) {
                    cities.filterByProvinceCode(currentState.branchSelection.provinceCode)
                } else {
                    currentState.branchCities
                },
            )
        }
        is PartialState.ProvincesLoading -> currentState.copy(
            isProvincesLoading = partialState.isLoading,
        )
        is PartialState.ProvincesLoaded -> currentState.copy(
            isProvincesLoading = false,
            provinces = partialState.provinces,
        )
        is PartialState.BranchCitiesLoading -> currentState.copy(
            isBranchCitiesLoading = partialState.isLoading,
        )
        is PartialState.BranchCitiesLoaded -> currentState.copy(
            isBranchCitiesLoading = false,
            branchCities = partialState.cities,
        )
        is PartialState.BranchesLoading -> currentState.copy(
            isBranchesLoading = partialState.isLoading,
        )
        is PartialState.BranchesLoaded -> currentState.copy(
            isBranchesLoading = false,
            branches = partialState.branches,
        )
        is PartialState.ContractApplicantTypeChanged -> currentState.copy(
            contractApplicantType = partialState.type,
        )
        is PartialState.BranchSelectionChanged -> currentState.copy(
            branchSelection = partialState.branchSelection,
        )
        is PartialState.PremiumRatesLoading -> currentState.copy(
            isPremiumRatesLoading = partialState.isLoading,
        )
        is PartialState.PremiumRatesLoaded -> currentState.copy(
            isPremiumRatesLoading = false,
            premiumRates = partialState.premiumRates,
        )
        is PartialState.PremiumRateSelected -> currentState.copy(
            selectedPremiumRateCode = partialState.code,
        )
        is PartialState.StepChanged -> currentState.copy(
            currentStep = partialState.step,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
