package com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract

import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.CityOptionPR
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.ContractEligibilityPR
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.StudentInsuranceContractStep
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.UserInfoFormPR
import com.tamin.taminhamrah.model.contracts.ContractPR
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR


data class StudentInsuranceContractUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrationInfo: RegistrationInfoPR? = null,
    val existingContracts: List<ContractPR> = emptyList(),
    val eligibility: ContractEligibilityPR? = null,
    val isRulesConfirmed: Boolean = false,
    val userInfo: UserInfoFormPR = UserInfoFormPR(),
    val cities: List<CityOptionPR> = emptyList(),
    val isCitiesLoading: Boolean = false,
    val currentStep: StudentInsuranceContractStep = StudentInsuranceContractStep.STEP_REGISTRATION,
) {
    val canGoNext: Boolean
        get() = when (currentStep) {
            StudentInsuranceContractStep.STEP_REGISTRATION ->
                registrationInfo != null && eligibility != null
            StudentInsuranceContractStep.STEP_AUTHORIZATION -> eligibility?.isEligible == true
            StudentInsuranceContractStep.STEP_CONTRACT_TERMS -> isRulesConfirmed
            StudentInsuranceContractStep.STEP_USER_INFO -> userInfo.isValid
            else -> false
        }

    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data class RegistrationInfoLoaded(val info: RegistrationInfoPR) : PartialState()
        data class ContractsLoaded(val contracts: List<ContractPR>) : PartialState()
        data class EligibilityLoaded(val eligibility: ContractEligibilityPR) : PartialState()
        data class RulesConfirmedChanged(val confirmed: Boolean) : PartialState()
        data class UserInfoChanged(val userInfo: UserInfoFormPR) : PartialState()
        data class CitiesLoading(val isLoading: Boolean) : PartialState()
        data class CitiesLoaded(val cities: List<CityOptionPR>) : PartialState()
        data class StepChanged(val step: StudentInsuranceContractStep) : PartialState()
    }
}

sealed class StudentInsuranceContractIntent {
    data object LoadInitialData : StudentInsuranceContractIntent()
    data object GoToNextStep : StudentInsuranceContractIntent()
    data object GoToPreviousStep : StudentInsuranceContractIntent()
    data class SetRulesConfirmed(val confirmed: Boolean) : StudentInsuranceContractIntent()
    data class UpdateUserInfo(val userInfo: UserInfoFormPR) : StudentInsuranceContractIntent()
}

sealed class StudentInsuranceContractEvent
