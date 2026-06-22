package com.tamin.taminhamrah.feature.studentInsuranceContract.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract.StudentInsuranceContractEvent
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract.StudentInsuranceContractIntent
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract.StudentInsuranceContractUiState
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract.StudentInsuranceContractUiState.PartialState
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper.resolveEligibility
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper.toCityOptions
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.StudentInsuranceContractStep
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.UserInfoFormPR
import com.tamin.taminhamrah.mapper.contracts.toPresentation
import com.tamin.taminhamrah.useCases.common.GetCitiesUseCase
import com.tamin.taminhamrah.useCases.contracts.GetContractsUseCase
import com.tamin.taminhamrah.useCases.contracts.GetRegistrationInfoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

class StudentInsuranceContractViewModel(
    private val getRegistrationInfoUseCase: GetRegistrationInfoUseCase,
    private val getContractsUseCase: GetContractsUseCase,
    private val getCitiesUseCase: GetCitiesUseCase,
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
        }
    }

    private fun handleLoadInitialData(): Flow<PartialState> = merge(
        loadRegistrationInfo(),
        loadContracts(),
        loadCities(),
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
        is PartialState.CitiesLoaded -> currentState.copy(
            isCitiesLoading = false,
            cities = partialState.cities,
        )
        is PartialState.StepChanged -> currentState.copy(
            currentStep = partialState.step,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
