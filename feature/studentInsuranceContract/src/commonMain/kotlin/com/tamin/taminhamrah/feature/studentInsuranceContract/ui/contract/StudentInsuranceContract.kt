package com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract

import com.tamin.taminhamrah.model.studentContract.BranchSelectionFormPR
import com.tamin.taminhamrah.model.studentContract.ContractApplicantType
import com.tamin.taminhamrah.model.studentContract.UploadImagePR
import com.tamin.taminhamrah.model.studentContract.ContractEligibilityPR
import com.tamin.taminhamrah.model.studentContract.FreelanceContractResultPR
import com.tamin.taminhamrah.model.studentContract.FreelancePremiumRangePR
import com.tamin.taminhamrah.model.studentContract.InsuranceContractKind
import com.tamin.taminhamrah.model.studentContract.SpcPremiumRateOptionPR
import com.tamin.taminhamrah.model.studentContract.StudentInsuranceContractStep
import com.tamin.taminhamrah.model.studentContract.UserInfoFormPR
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.model.contracts.ContractPR
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR


data class StudentInsuranceContractUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrationInfo: RegistrationInfoPR? = null,
    val existingContracts: List<ContractPR> = emptyList(),
    val eligibility: ContractEligibilityPR? = null,
    val isRulesConfirmed: Boolean = false,
    val userInfo: UserInfoFormPR = UserInfoFormPR(),
    val contractKind: InsuranceContractKind = InsuranceContractKind.STUDENT,
    val freeJobs: List<FreeJobDN> = emptyList(),
    val selectedFreeJobCode: String? = null,
    val selectedFreeJobName: String? = null,
    val isFreeJobsLoading: Boolean = false,
    val contractApplicantType: ContractApplicantType = ContractApplicantType.PERSONAL,
    val branchSelection: BranchSelectionFormPR = BranchSelectionFormPR(),
    val cities: List<CityPR> = emptyList(),
    val branchCities: List<CityPR> = emptyList(),
    val provinces: List<ProvincePR> = emptyList(),
    val branches: List<BranchPR> = emptyList(),
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
    val isSavingContact: Boolean = false,
    val isSubmittingContract: Boolean = false,
    val submittedContract: FreelanceContractResultPR? = null,
    val documentDescription: String = "",
    val documentPreviewBytes: ByteArray? = null,
    val uploadedDocuments: List<UploadImagePR> = emptyList(),
    val isUploadingDocument: Boolean = false,
    val uploadDocumentError: String? = null,
    val currentStep: StudentInsuranceContractStep = StudentInsuranceContractStep.STEP_REGISTRATION,
) {
    val canGoNext: Boolean
        get() = !isSavingContact && when (currentStep) {
            StudentInsuranceContractStep.STEP_REGISTRATION ->
                registrationInfo != null && eligibility != null
            StudentInsuranceContractStep.STEP_AUTHORIZATION -> eligibility?.isEligible == true
            StudentInsuranceContractStep.STEP_CONTRACT_TERMS -> isRulesConfirmed
            StudentInsuranceContractStep.STEP_USER_INFO -> isUserInfoStepComplete(userInfo)
            StudentInsuranceContractStep.STEP_CONTRACT_APPLICANT -> true
            StudentInsuranceContractStep.STEP_SELECT_BRANCH -> branchSelection.isValid
            StudentInsuranceContractStep.STEP_UPLOAD_IMAGE -> true
            StudentInsuranceContractStep.STEP_TREATMENT_SUPPORT -> true
            StudentInsuranceContractStep.STEP_INSURANCE_PREMIUM -> {
                val hasPremiumRate = selectedPremiumRateCode != null
                val hasFreeJob = !contractKind.requiresFreeJob || selectedFreeJobCode != null
                hasPremiumRate && hasFreeJob
            }
            StudentInsuranceContractStep.STEP_SALARY -> isPremiumCalculated
            StudentInsuranceContractStep.STEP_SUBMIT_CONTRACT -> submittedContract != null
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
        data class CitiesLoaded(val cities: List<CityPR>) : PartialState()
        data class ProvincesLoading(val isLoading: Boolean) : PartialState()
        data class ProvincesLoaded(val provinces: List<ProvincePR>) : PartialState()
        data class BranchCitiesLoading(val isLoading: Boolean) : PartialState()
        data class BranchCitiesLoaded(val cities: List<CityPR>) : PartialState()
        data class BranchesLoading(val isLoading: Boolean) : PartialState()
        data class BranchesLoaded(val branches: List<BranchPR>) : PartialState()
        data class ContractKindChanged(val kind: InsuranceContractKind) : PartialState()
        data class FreeJobsLoading(val isLoading: Boolean) : PartialState()
        data class FreeJobsLoaded(val freeJobs: List<FreeJobDN>) : PartialState()
        data class FreeJobSelected(val jobCode: String, val jobName: String) : PartialState()
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
        data class SavingContact(val isSaving: Boolean) : PartialState()
        data object ContactSaved : PartialState()
        data class SubmittingContract(val isSubmitting: Boolean) : PartialState()
        data class ContractSubmitted(val result: FreelanceContractResultPR) : PartialState()
        data class DocumentDescriptionChanged(val description: String) : PartialState()
        data class DocumentPreviewSet(val bytes: ByteArray) : PartialState()
        data class UploadingDocument(val isUploading: Boolean) : PartialState()
        data class UploadDocumentError(val message: String?) : PartialState()
        data class DocumentUploaded(val document: UploadImagePR) : PartialState()
        data object UploadedDocumentCleared : PartialState()
        data class StepChanged(val step: StudentInsuranceContractStep) : PartialState()
    }
}

sealed class StudentInsuranceContractIntent {
    data class LoadInitialData(val kind: InsuranceContractKind) : StudentInsuranceContractIntent()
    data object GoToNextStep : StudentInsuranceContractIntent()
    data object GoToPreviousStep : StudentInsuranceContractIntent()
    data class SetRulesConfirmed(val confirmed: Boolean) : StudentInsuranceContractIntent()
    data class UpdateUserInfo(val userInfo: UserInfoFormPR) : StudentInsuranceContractIntent()
    data class SetContractApplicantType(val type: ContractApplicantType) : StudentInsuranceContractIntent()
    data class SelectBranchProvince(val province: ProvincePR) : StudentInsuranceContractIntent()
    data class SelectBranchCity(val city: CityPR) : StudentInsuranceContractIntent()
    data class SelectBranch(val branch: BranchPR) : StudentInsuranceContractIntent()
    data class UpdateDocumentDescription(val description: String) : StudentInsuranceContractIntent()
    data class UploadPickedImage(val fileName: String, val bytes: ByteArray) : StudentInsuranceContractIntent()
    data object ClearUploadedDocument : StudentInsuranceContractIntent()
    data class SelectPremiumRate(val rate: SpcPremiumRateOptionPR) : StudentInsuranceContractIntent()
    data class SelectFreeJob(val job: FreeJobDN) : StudentInsuranceContractIntent()
    data class SelectMonthlyPremium(val amount: Long) : StudentInsuranceContractIntent()
    data object CalculateMonthlyPremium : StudentInsuranceContractIntent()
    data class SetAgreementConfirmed(val confirmed: Boolean) : StudentInsuranceContractIntent()
    data object SubmitContract : StudentInsuranceContractIntent()
}

sealed class StudentInsuranceContractEvent

internal fun isUserInfoStepComplete(userInfo: UserInfoFormPR): Boolean =
    userInfo.cityCode.isNotBlank() &&
        userInfo.cityName.isNotBlank() &&
        userInfo.address.isNotBlank() &&
        userInfo.zipCode.length >= 10 &&
        userInfo.phoneNumber.isNotBlank()
