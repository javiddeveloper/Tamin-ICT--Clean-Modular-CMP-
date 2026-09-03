package com.tamin.taminhamrah.feature.contracts.flow.ui.contract

import com.tamin.taminhamrah.feature.contracts.flow.config.ContractFlowConfig
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contractFlow.BranchSelectionFormPR
import com.tamin.taminhamrah.contractFlow.ContractApplicantType
import com.tamin.taminhamrah.model.contractFlow.ContractEligibilityPR
import com.tamin.taminhamrah.contractFlow.ContractStep
import com.tamin.taminhamrah.model.contractFlow.FreelanceContractResultPR
import com.tamin.taminhamrah.model.contractFlow.FreelancePremiumRangePR
import com.tamin.taminhamrah.model.contractFlow.GuardianFormPR
import com.tamin.taminhamrah.model.contractFlow.SpcPremiumRateOptionPR
import com.tamin.taminhamrah.model.contractFlow.UploadImagePR
import com.tamin.taminhamrah.model.contractFlow.UserInfoFormPR
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.ContractPR
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import com.tamin.taminhamrah.model.subdominant.SubdominantItemPR

data class ContractFlowUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrationInfo: RegistrationInfoPR? = null,
    val existingContracts: List<ContractPR> = emptyList(),
    val rawTypedContracts: List<ContractDN> = emptyList(),
    val hasLoadedTypedContracts: Boolean = false,
    val allContracts: List<ContractDN> = emptyList(),
    val hasLoadedAllContracts: Boolean = false,
    val allContractsLoadFailed: Boolean = false,
    val eligibility: ContractEligibilityPR? = null,
    val isRulesConfirmed: Boolean = false,
    val userInfo: UserInfoFormPR = UserInfoFormPR(),
    val config: ContractFlowConfig? = null,
    val freeJobs: List<FreeJobDN> = emptyList(),
    val selectedFreeJobCode: String? = null,
    val selectedFreeJobName: String? = null,
    val isFreeJobsLoading: Boolean = false,
    val contractApplicantType: ContractApplicantType = ContractApplicantType.PERSONAL,
    val guardianForm: GuardianFormPR = GuardianFormPR(),
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
    val forceTreatmentSupport: Boolean = false,
    val treatmentSupportCode: String = TREATMENT_SUPPORT_WITH,
    val isTreatmentCommitmentConfirmed: Boolean = false,
    val dependents: List<SubdominantItemPR> = emptyList(),
    val isDependentsLoading: Boolean = false,
    val hasLoadedDependents: Boolean = false,
    val dependentsError: String? = null,
    val hidePremiumSlider: Boolean = false,
    val lockedPremiumRateCode: String? = null,
    val genderGateError: String? = null,
    val preflightGateError: String? = null,
    val allowsOnlinePayment: Boolean = false,
    val currentStep: ContractStep = ContractStep.STEP_REGISTRATION,
) {
    val canGoNext: Boolean
        get() {
            val flowConfig = config ?: return false
            return !isSavingContact && when (currentStep) {
                ContractStep.STEP_REGISTRATION ->
                    registrationInfo != null &&
                        genderGateError == null &&
                        preflightGateError == null &&
                        (eligibility == null || eligibility.isEligible)
                ContractStep.STEP_AUTHORIZATION -> eligibility?.isEligible == true
                ContractStep.STEP_CONTRACT_TERMS -> isRulesConfirmed
                ContractStep.STEP_USER_INFO -> isUserInfoStepComplete(userInfo)
                ContractStep.STEP_CONTRACT_APPLICANT ->
                    contractApplicantType == ContractApplicantType.PERSONAL ||
                        (contractApplicantType == ContractApplicantType.GUARDIAN && guardianForm.isValid)
                ContractStep.STEP_SELECT_BRANCH -> branchSelection.isValid
                ContractStep.STEP_UPLOAD_IMAGE ->
                    documentDescription.isNotBlank() && uploadedDocuments.isNotEmpty()
                ContractStep.STEP_TREATMENT_SUPPORT ->
                    treatmentSupportCode == TREATMENT_SUPPORT_WITHOUT ||
                        (treatmentSupportCode == TREATMENT_SUPPORT_WITH && isTreatmentCommitmentConfirmed)
                ContractStep.STEP_INSURANCE_PREMIUM -> {
                    val hasPremiumRate = selectedPremiumRateCode != null || lockedPremiumRateCode != null
                    val hasFreeJob = !flowConfig.requiresFreeJob || selectedFreeJobCode != null
                    hasPremiumRate && hasFreeJob
                }
                ContractStep.STEP_SALARY -> isPremiumCalculated
                ContractStep.STEP_SUBMIT_CONTRACT -> isAgreementConfirmed && !isSubmittingContract
            }
        }

    companion object {
        const val TREATMENT_SUPPORT_WITH = "1"
        const val TREATMENT_SUPPORT_WITHOUT = "2"
    }

    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data class RegistrationInfoLoaded(val info: RegistrationInfoPR) : PartialState()
        data class ContractsLoaded(val contracts: List<ContractPR>) : PartialState()
        data class RawTypedContractsLoaded(val contracts: List<ContractDN>) : PartialState()
        data class AllContractsLoaded(val contracts: List<ContractDN>) : PartialState()
        data object AllContractsLoadFailed : PartialState()
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
        data class StepChanged(val step: ContractStep) : PartialState()
        data class ForceTreatmentSupportChanged(val forced: Boolean) : PartialState()
        data class HidePremiumSliderChanged(val hidden: Boolean) : PartialState()
        data class LockedPremiumRateChanged(val code: String?) : PartialState()
        data class GenderGateError(val message: String?) : PartialState()
        data class PreflightGateError(val message: String?) : PartialState()
        data class PaymentAllowedChanged(val allowed: Boolean) : PartialState()
        data class TreatmentSupportCodeChanged(val code: String) : PartialState()
        data class TreatmentCommitmentChanged(val confirmed: Boolean) : PartialState()
        data class DependentsLoading(val isLoading: Boolean) : PartialState()
        data class DependentsLoaded(val dependents: List<SubdominantItemPR>) : PartialState()
        data class DependentsError(val message: String?) : PartialState()
        data class GuardianFormChanged(val form: GuardianFormPR) : PartialState()
        data class GuardianDocumentUploading(val isUploading: Boolean) : PartialState()
        data class GuardianDocumentUploaded(val guid: String, val name: String, val bytes: ByteArray) : PartialState()
        data object GuardianDocumentCleared : PartialState()
    }
}

sealed class ContractFlowIntent {
    data object LoadInitialData : ContractFlowIntent()
    data object GoToNextStep : ContractFlowIntent()
    data object GoToPreviousStep : ContractFlowIntent()
    data class SetRulesConfirmed(val confirmed: Boolean) : ContractFlowIntent()
    data class UpdateUserInfo(val userInfo: UserInfoFormPR) : ContractFlowIntent()
    data class SetContractApplicantType(val type: ContractApplicantType) : ContractFlowIntent()
    data class UpdateGuardianForm(val form: GuardianFormPR) : ContractFlowIntent()
    data class UploadGuardianImage(val fileName: String, val bytes: ByteArray) : ContractFlowIntent()
    data object ClearGuardianDocument : ContractFlowIntent()
    data class SelectBranchProvince(val province: ProvincePR) : ContractFlowIntent()
    data class SelectBranchCity(val city: CityPR) : ContractFlowIntent()
    data class SelectBranch(val branch: BranchPR) : ContractFlowIntent()
    data class UpdateDocumentDescription(val description: String) : ContractFlowIntent()
    data class UploadPickedImage(val fileName: String, val bytes: ByteArray) : ContractFlowIntent()
    data object ClearUploadedDocument : ContractFlowIntent()
    data class SelectPremiumRate(val rate: SpcPremiumRateOptionPR) : ContractFlowIntent()
    data class SelectFreeJob(val job: FreeJobDN) : ContractFlowIntent()
    data class SelectMonthlyPremium(val amount: Long) : ContractFlowIntent()
    data object CalculateMonthlyPremium : ContractFlowIntent()
    data class SetAgreementConfirmed(val confirmed: Boolean) : ContractFlowIntent()
    data class SelectTreatmentSupport(val withSupport: Boolean) : ContractFlowIntent()
    data class SetTreatmentCommitment(val confirmed: Boolean) : ContractFlowIntent()
    data object LoadDependents : ContractFlowIntent()
    data object SubmitContract : ContractFlowIntent()
}

sealed class ContractFlowEvent {
    data class ShowPaymentOption(val contractNumber: String, val amount: Long) : ContractFlowEvent()
    data class ShowMessage(val message: String) : ContractFlowEvent()
}

internal fun isUserInfoStepComplete(userInfo: UserInfoFormPR): Boolean =
    userInfo.cityCode.isNotBlank() &&
        userInfo.cityName.isNotBlank() &&
        userInfo.address.isNotBlank() &&
        userInfo.zipCode.length >= 10 &&
        userInfo.phoneNumber.isNotBlank()
