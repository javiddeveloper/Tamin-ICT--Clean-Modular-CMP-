package com.tamin.taminhamrah.feature.studentInsuranceContract.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract.StudentInsuranceContractEvent
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract.StudentInsuranceContractIntent
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract.StudentInsuranceContractUiState
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract.StudentInsuranceContractUiState.PartialState
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper.toPresentation as toContractResultPresentation
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper.toPresentation as toPremiumRangePresentation
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper.toSpcPremiumRateOptions
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper.resolveEligibility
import com.tamin.taminhamrah.mapper.common.filterByProvinceCode
import com.tamin.taminhamrah.mapper.common.toCityPresentation
import com.tamin.taminhamrah.mapper.common.toProvincePresentation
import com.tamin.taminhamrah.mapper.contracts.toBranchPresentation
import com.tamin.taminhamrah.mapper.contracts.toPresentation
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.useCases.contracts.GetBranchesUseCase
import com.tamin.taminhamrah.useCases.contracts.GetContractsUseCase
import com.tamin.taminhamrah.useCases.contracts.GetRegistrationInfoUseCase
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.ContractApplicantType
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.SpcPremiumRateOptionPR
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.StudentInsuranceContractStep
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.UserInfoFormPR
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractParams
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractRequestDN
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.useCases.contracts.CalculateFreelanceSalaryUseCase
import com.tamin.taminhamrah.useCases.contracts.GetFreelancePremiumRangeUseCase
import com.tamin.taminhamrah.useCases.contracts.GetSpcPremiumRatesUseCase
import com.tamin.taminhamrah.useCases.contracts.MakeFreelanceContractUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

class StudentInsuranceContractViewModel(
    private val getRegistrationInfoUseCase: GetRegistrationInfoUseCase,
    private val getContractsUseCase: GetContractsUseCase,
    private val identityInfoUseCase: IdentityInfoUseCase,
    private val getBranchesUseCase: GetBranchesUseCase,
    private val getSpcPremiumRatesUseCase: GetSpcPremiumRatesUseCase,
    private val getFreelancePremiumRangeUseCase: GetFreelancePremiumRangeUseCase,
    private val calculateFreelanceSalaryUseCase: CalculateFreelanceSalaryUseCase,
    private val makeFreelanceContractUseCase: MakeFreelanceContractUseCase,
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
            is StudentInsuranceContractIntent.SelectMonthlyPremium -> handleSelectMonthlyPremium(intent.amount)
            StudentInsuranceContractIntent.CalculateMonthlyPremium -> handleCalculateMonthlyPremium()
            is StudentInsuranceContractIntent.SetAgreementConfirmed -> handleSetAgreementConfirmed(intent.confirmed)
            StudentInsuranceContractIntent.SubmitContract -> handleSubmitContract()
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
            identityInfoUseCase.getCities().collect { cities ->
                emit(PartialState.CitiesLoaded(cities.toCityPresentation()))
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
            identityInfoUseCase.getProvinces().collect { provinces ->
                emit(PartialState.ProvincesLoaded(provinces.toProvincePresentation()))
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

    private fun handleSelectBranchProvince(province: ProvincePR): Flow<PartialState> = flow {
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
        emit(
            PartialState.BranchCitiesLoaded(
                uiState.value.cities.filterByProvinceCode(province.provinceCode),
            ),
        )
        emit(PartialState.BranchesLoaded(emptyList()))
    }

    private fun loadBranches(cityCode: String): Flow<PartialState> = flow {
        emit(PartialState.BranchesLoading(true))
        try {
            getBranchesUseCase(cityCode).collect { branches ->
                emit(PartialState.BranchesLoaded(branches.toBranchPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        } finally {
            emit(PartialState.BranchesLoading(false))
        }
    }

    private fun handleGoToNextStep(): Flow<PartialState> {
        val nextStep = uiState.value.currentStep.stepIndex
            .let { StudentInsuranceContractStep.entries.firstOrNull { step -> step.stepIndex == it + 1 } }
            ?: return flow { }
        if (!uiState.value.canGoNext) return flow { }

        return merge(
            flow { emit(PartialState.StepChanged(nextStep)) },
            if (nextStep == StudentInsuranceContractStep.STEP_SALARY) {
                loadFreelancePremiumRange()
            } else {
                flow { }
            },
        )
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

    private fun handleSelectBranchCity(city: CityPR): Flow<PartialState> = merge(
        flow {
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
            emit(PartialState.BranchesLoaded(emptyList()))
        },
        loadBranches(city.cityCode),
    )

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

    private fun handleSelectPremiumRate(rate: SpcPremiumRateOptionPR): Flow<PartialState> = flow {
        emit(PartialState.PremiumRateSelected(rate.code))
    }

    private fun loadFreelancePremiumRange(): Flow<PartialState> = flow {
        val params = buildPremiumRangeParams() ?: return@flow
        emit(PartialState.PremiumRangeLoading(true))
        emit(PartialState.PremiumCalculated(false))
        try {
            getFreelancePremiumRangeUseCase(params).collect { range ->
                val presentation = range.toPremiumRangePresentation()
                emit(PartialState.PremiumRangeLoaded(presentation))
                emit(PartialState.SelectedMonthlyPremiumChanged(presentation.lowPremium))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        } finally {
            emit(PartialState.PremiumRangeLoading(false))
        }
    }

    private fun buildPremiumRangeParams(): FreelancePremiumRangeParams? {
        val insuranceId = uiState.value.registrationInfo?.insuranceId?.takeIf { it.isNotBlank() }
            ?: return null
        val spcRateCode = uiState.value.selectedPremiumRateCode ?: return null
        return FreelancePremiumRangeParams(
            treatmentSupportCode = DEFAULT_TREATMENT_SUPPORT_CODE,
            spcRateCode = spcRateCode,
            insuranceId = insuranceId,
        )
    }

    private fun handleSelectMonthlyPremium(amount: Long): Flow<PartialState> = flow {
        emit(PartialState.SelectedMonthlyPremiumChanged(amount))
    }

    private fun handleCalculateMonthlyPremium(): Flow<PartialState> = flow {
        val params = buildCalculateSalaryParams() ?: return@flow
        emit(PartialState.CalculatingPremium(true))
        try {
            calculateFreelanceSalaryUseCase(params).collect { salary ->
                emit(PartialState.CalculatedMonthlySalaryLoaded(salary))
                emit(PartialState.PremiumCalculated(true))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        } finally {
            emit(PartialState.CalculatingPremium(false))
        }
    }

    private fun buildCalculateSalaryParams(): FreelanceCalculateSalaryParams? {
        val monthlyPremium = uiState.value.selectedMonthlyPremium ?: return null
        val spcRateCode = uiState.value.selectedPremiumRateCode ?: return null
        return FreelanceCalculateSalaryParams(
            monthlyPremium = monthlyPremium,
            treatmentSupportCode = DEFAULT_TREATMENT_SUPPORT_CODE,
            spcRateCode = spcRateCode,
        )
    }

    private fun handleSetAgreementConfirmed(confirmed: Boolean): Flow<PartialState> = flow {
        emit(PartialState.AgreementConfirmedChanged(confirmed))
    }

    private fun handleSubmitContract(): Flow<PartialState> = flow {
        val params = buildMakeContractParams() ?: return@flow
        emit(PartialState.SubmittingContract(true))
        try {
            makeFreelanceContractUseCase(params).collect { result ->
                emit(PartialState.ContractSubmitted(result.toContractResultPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        } finally {
            emit(PartialState.SubmittingContract(false))
        }
    }

    private fun buildMakeContractParams(): FreelanceMakeContractParams? {
        val monthlyPremium = uiState.value.selectedMonthlyPremium ?: return null
        val premiumRateCode = uiState.value.selectedPremiumRateCode ?: return null
        val insuranceId = uiState.value.registrationInfo?.insuranceId?.takeIf { it.isNotBlank() } ?: return null
        val branch = uiState.value.branchSelection
        if (!branch.isValid) return null
        return FreelanceMakeContractParams(
            monthlyPremium = monthlyPremium,
            request = FreelanceMakeContractRequestDN(
                brchCodeNew = branch.branchCode,
                cityCode = branch.cityCode,
                cntDrmn = DEFAULT_TREATMENT_SUPPORT_CODE,
                cntFreeJobCode = insuranceId,
                guid = DEFAULT_APPLICANT_GUID,
                guidName = DEFAULT_APPLICANT_GUID_NAME,
                premiumRateCode = premiumRateCode,
                provinceCode = branch.provinceCode,
            ),
        )
    }

    private companion object {
        /**
         * Step 8 (treatment support) is not implemented yet; legacy app uses cntDrmn "1".
         */
        const val DEFAULT_TREATMENT_SUPPORT_CODE = "1"
        const val DEFAULT_APPLICANT_GUID = "00"
        const val DEFAULT_APPLICANT_GUID_NAME = "00"
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
        is PartialState.PremiumRangeLoading -> currentState.copy(
            isPremiumRangeLoading = partialState.isLoading,
        )
        is PartialState.PremiumRangeLoaded -> currentState.copy(
            isPremiumRangeLoading = false,
            premiumRange = partialState.premiumRange,
        )
        is PartialState.SelectedMonthlyPremiumChanged -> currentState.copy(
            selectedMonthlyPremium = partialState.amount,
            calculatedMonthlySalary = null,
            isPremiumCalculated = false,
        )
        is PartialState.CalculatingPremium -> currentState.copy(
            isCalculatingPremium = partialState.isCalculating,
        )
        is PartialState.PremiumCalculated -> currentState.copy(
            isPremiumCalculated = partialState.calculated,
        )
        is PartialState.CalculatedMonthlySalaryLoaded -> currentState.copy(
            calculatedMonthlySalary = partialState.salary,
        )
        is PartialState.AgreementConfirmedChanged -> currentState.copy(
            isAgreementConfirmed = partialState.confirmed,
        )
        is PartialState.SubmittingContract -> currentState.copy(
            isSubmittingContract = partialState.isSubmitting,
        )
        is PartialState.ContractSubmitted -> currentState.copy(
            submittedContract = partialState.result,
        )
        is PartialState.StepChanged -> currentState.copy(
            currentStep = partialState.step,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
