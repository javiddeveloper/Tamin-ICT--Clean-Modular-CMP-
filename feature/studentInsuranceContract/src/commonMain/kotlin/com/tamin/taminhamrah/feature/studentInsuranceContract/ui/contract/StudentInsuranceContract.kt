package com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract

import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.ContractEligibilityPR
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.StudentInsuranceContractStep
import com.tamin.taminhamrah.model.contracts.ContractPR
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR


data class StudentInsuranceContractUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrationInfo: RegistrationInfoPR? = null,
    val existingContracts: List<ContractPR> = emptyList(),
    val eligibility: ContractEligibilityPR? = null,
    val isRulesConfirmed: Boolean = false,
    val currentStep: StudentInsuranceContractStep = StudentInsuranceContractStep.STEP_REGISTRATION,
) {
    val canGoNext: Boolean
        get() = when (currentStep) {
            StudentInsuranceContractStep.STEP_REGISTRATION ->
                registrationInfo != null && eligibility != null
            StudentInsuranceContractStep.STEP_AUTHORIZATION -> eligibility?.isEligible == true
            StudentInsuranceContractStep.STEP_CONTRACT_TERMS -> isRulesConfirmed
            else -> false
        }

    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data class RegistrationInfoLoaded(val info: RegistrationInfoPR) : PartialState()
        data class ContractsLoaded(val contracts: List<ContractPR>) : PartialState()
        data class EligibilityLoaded(val eligibility: ContractEligibilityPR) : PartialState()
        data class RulesConfirmedChanged(val confirmed: Boolean) : PartialState()
        data class StepChanged(val step: StudentInsuranceContractStep) : PartialState()
    }
}

sealed class StudentInsuranceContractIntent {
    data object LoadInitialData : StudentInsuranceContractIntent()
    data object GoToNextStep : StudentInsuranceContractIntent()
    data object GoToPreviousStep : StudentInsuranceContractIntent()
    data class SetRulesConfirmed(val confirmed: Boolean) : StudentInsuranceContractIntent()
}

sealed class StudentInsuranceContractEvent
