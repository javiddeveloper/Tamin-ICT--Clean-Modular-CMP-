package com.tamin.taminhamrah.feature.contracts.flow

import com.tamin.taminhamrah.contractFlow.ContractApplicantType
import com.tamin.taminhamrah.model.contractFlow.BranchSelectionFormPR
import com.tamin.taminhamrah.model.contractFlow.UploadImagePR
import com.tamin.taminhamrah.model.contracts.ContractDN

/**
 * Values seeded into [com.tamin.taminhamrah.feature.contracts.flow.ui.contract.ContractFlowUiState]
 * when opening the stepper to edit an existing active contract.
 */
data class ExistingContractEditSeed(
    val branchSelection: BranchSelectionFormPR,
    val freeJobCode: String?,
    val freeJobName: String?,
    val treatmentSupportCode: String,
    val isTreatmentCommitmentConfirmed: Boolean,
    val selectedPremiumRateCode: String?,
    val selectedMonthlyPremium: Long?,
    val calculatedMonthlySalary: Long?,
    val isPremiumCalculated: Boolean,
    val uploadedDocument: UploadImagePR?,
    val contractApplicantType: ContractApplicantType,
)

private const val TREATMENT_WITH = "1"
private const val TREATMENT_WITHOUT = "2"
private const val OPTIONAL_PREMIUM_PERCENT = 27L
private const val DEFAULT_GUID = "00"

fun seedExistingContractEdit(
    contract: ContractDN,
    isOptionalInsurance: Boolean,
): ExistingContractEditSeed {
    val salary = contract.salary
    val selectedPremium = when {
        salary == null || salary <= 0L -> null
        isOptionalInsurance -> (salary * OPTIONAL_PREMIUM_PERCENT) / 100L
        else -> salary
    }
    val treatment = when (contract.cntDrmn) {
        TREATMENT_WITH -> TREATMENT_WITH
        else -> TREATMENT_WITHOUT
    }
    val guid = contract.guid?.takeIf { it.isNotBlank() && it != DEFAULT_GUID }
    val guidName = contract.guidName?.takeIf { it.isNotBlank() } ?: DEFAULT_GUID
    val document = guid?.let {
        UploadImagePR(
            imageId = it,
            fileName = guidName,
            description = guidName,
        )
    }
    return ExistingContractEditSeed(
        branchSelection = BranchSelectionFormPR(
            provinceCode = contract.provinceCode.orEmpty(),
            provinceName = contract.provinceName.orEmpty(),
            cityCode = contract.cityCode.orEmpty(),
            cityName = "",
            branchCode = contract.brchCodeNew?.takeIf { it.isNotBlank() }
                ?: contract.branchCode.orEmpty(),
            branchName = "",
        ),
        freeJobCode = contract.cntFreeJobCode?.takeIf { it.isNotBlank() },
        freeJobName = contract.freeJob?.discrioption?.takeIf { it.isNotBlank() },
        treatmentSupportCode = treatment,
        isTreatmentCommitmentConfirmed = treatment == TREATMENT_WITH,
        selectedPremiumRateCode = contract.premiumRate?.spcrateCode
            ?: contract.premiumRateCode?.takeIf { it.isNotBlank() && it != "00" },
        selectedMonthlyPremium = selectedPremium,
        calculatedMonthlySalary = salary?.takeIf { it > 0L },
        isPremiumCalculated = selectedPremium != null && salary != null && salary > 0L,
        uploadedDocument = document,
        contractApplicantType = ContractApplicantType.PERSONAL,
    )
}
