package com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract

import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.BranchSelectionFormPR
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.CityOptionPR
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.ContractApplicantType
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.ContractEligibilityPR
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.FreelanceContractResultPR
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.FreelancePremiumRangePR
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.SpcPremiumRateOptionPR
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
    val contractApplicantType: ContractApplicantType = ContractApplicantType.PERSONAL,
    val branchSelection: BranchSelectionFormPR = BranchSelectionFormPR(),
    val cities: List<CityOptionPR> = emptyList(),
    val branchCities: List<CityOptionPR> = emptyList(),
    val provinces: List<CityOptionPR> = emptyList(),
    val branches: List<CityOptionPR> = emptyList(),
    val isCitiesLoading: Boolean = false,
    val isProvincesLoading: Boolean = false,
    val isBranchCitiesLoading: Boolean = false,
    val isBranchesLoading: Boolean = false,
    val premiumRates: List<SpcPremiumRateOptionPR> = emptyList(),
    val selectedPremiumRateCode: String? = null,
    val isPremiumRatesLoading: Boolean = false,
    val premiumRange: FreelancePremiumRangePR? = null,
    val selectedMonthlyPremium: Long? = null,
    val isPremiumRangeLoading: Boolean = false,
    val isCalculatingPremium: Boolean = false,
    val isPremiumCalculated: Boolean = false,
    val calculatedMonthlySalary: Long? = null,
    val isAgreementConfirmed: Boolean = false,
    val isSubmittingContract: Boolean = false,
    val submittedContract: FreelanceContractResultPR? = null,
    val currentStep: StudentInsuranceContractStep = StudentInsuranceContractStep.STEP_REGISTRATION,
) {
    val canGoNext: Boolean
        get() = when (currentStep) {
            StudentInsuranceContractStep.STEP_REGISTRATION ->
                registrationInfo != null && eligibility != null
            StudentInsuranceContractStep.STEP_AUTHORIZATION -> eligibility?.isEligible == true
            StudentInsuranceContractStep.STEP_CONTRACT_TERMS -> isRulesConfirmed
            StudentInsuranceContractStep.STEP_USER_INFO -> userInfo.isValid
            StudentInsuranceContractStep.STEP_CONTRACT_APPLICANT -> true
            StudentInsuranceContractStep.STEP_SELECT_BRANCH -> branchSelection.isValid
            StudentInsuranceContractStep.STEP_UPLOAD_IMAGE,
            StudentInsuranceContractStep.STEP_TREATMENT_SUPPORT,
            -> true
            StudentInsuranceContractStep.STEP_INSURANCE_PREMIUM -> selectedPremiumRateCode != null
            StudentInsuranceContractStep.STEP_SALARY -> isPremiumCalculated
            StudentInsuranceContractStep.STEP_SUBMIT_CONTRACT -> submittedContract != null
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
        data class ProvincesLoading(val isLoading: Boolean) : PartialState()
        data class ProvincesLoaded(val provinces: List<CityOptionPR>) : PartialState()
        data class BranchCitiesLoading(val isLoading: Boolean) : PartialState()
        data class BranchCitiesLoaded(val cities: List<CityOptionPR>) : PartialState()
        data class BranchesLoading(val isLoading: Boolean) : PartialState()
        data class BranchesLoaded(val branches: List<CityOptionPR>) : PartialState()
        data class ContractApplicantTypeChanged(val type: ContractApplicantType) : PartialState()
        data class BranchSelectionChanged(val branchSelection: BranchSelectionFormPR) : PartialState()
        data class PremiumRatesLoading(val isLoading: Boolean) : PartialState()
        data class PremiumRatesLoaded(val premiumRates: List<SpcPremiumRateOptionPR>) : PartialState()
        data class PremiumRateSelected(val code: String) : PartialState()
        data class PremiumRangeLoading(val isLoading: Boolean) : PartialState()
        data class PremiumRangeLoaded(val premiumRange: FreelancePremiumRangePR) : PartialState()
        data class SelectedMonthlyPremiumChanged(val amount: Long) : PartialState()
        data class CalculatingPremium(val isCalculating: Boolean) : PartialState()
        data class PremiumCalculated(val calculated: Boolean) : PartialState()
        data class CalculatedMonthlySalaryLoaded(val salary: Long) : PartialState()
        data class AgreementConfirmedChanged(val confirmed: Boolean) : PartialState()
        data class SubmittingContract(val isSubmitting: Boolean) : PartialState()
        data class ContractSubmitted(val result: FreelanceContractResultPR) : PartialState()
        data class StepChanged(val step: StudentInsuranceContractStep) : PartialState()
    }
}

sealed class StudentInsuranceContractIntent {
    data object LoadInitialData : StudentInsuranceContractIntent()
    data object GoToNextStep : StudentInsuranceContractIntent()
    data object GoToPreviousStep : StudentInsuranceContractIntent()
    data class SetRulesConfirmed(val confirmed: Boolean) : StudentInsuranceContractIntent()
    data class UpdateUserInfo(val userInfo: UserInfoFormPR) : StudentInsuranceContractIntent()
    data class SetContractApplicantType(val type: ContractApplicantType) : StudentInsuranceContractIntent()
    data class SelectBranchProvince(val province: CityOptionPR) : StudentInsuranceContractIntent()
    data class SelectBranchCity(val city: CityOptionPR) : StudentInsuranceContractIntent()
    data class SelectBranch(val branch: CityOptionPR) : StudentInsuranceContractIntent()
    data class SelectPremiumRate(val rate: SpcPremiumRateOptionPR) : StudentInsuranceContractIntent()
    data class SelectMonthlyPremium(val amount: Long) : StudentInsuranceContractIntent()
    data object CalculateMonthlyPremium : StudentInsuranceContractIntent()
    data class SetAgreementConfirmed(val confirmed: Boolean) : StudentInsuranceContractIntent()
    data object SubmitContract : StudentInsuranceContractIntent()
}

sealed class StudentInsuranceContractEvent
