package com.tamin.taminhamrah.feature.contracts.flow.guardian

import com.tamin.taminhamrah.model.contractFlow.BranchSelectionFormPR
import com.tamin.taminhamrah.model.contractFlow.GuardianFormPR
import com.tamin.taminhamrah.model.contracts.FreelanceContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractRequestDN
import com.tamin.taminhamrah.model.contracts.GuardianShipDetailDN
import kotlinx.datetime.Instant

/**
 * Builds the freelance-family «contract by guardian» request
 * (student / freelance / housewife).
 * Returns null when branch, guardian form, document guid, letter date,
 * free-job code, or ward national id is incomplete.
 */
fun buildFreelanceContractByGuardianParams(
    selectedSalary: Long,
    branch: BranchSelectionFormPR,
    treatmentSupportCode: String,
    premiumRateCode: String,
    freeJobCode: String,
    guardianForm: GuardianFormPR,
    wardNationalId: String,
    documentDescription: String,
    contractImageGuid: String = DEFAULT_CONTRACT_IMAGE_GUID,
    contractImageName: String = DEFAULT_CONTRACT_IMAGE_GUID_NAME,
): FreelanceContractByGuardianParams? {
    if (!branch.isValid) return null
    if (!guardianForm.isValid) return null
    val guid = guardianForm.documentGuid ?: return null
    val letterEpoch = guardianForm.letterDateEpoch ?: return null
    if (wardNationalId.isBlank()) return null
    if (freeJobCode.isBlank()) return null

    return FreelanceContractByGuardianParams(
        selectedSalary = selectedSalary,
        contract = FreelanceMakeContractRequestDN(
            brchCodeNew = branch.branchCode,
            cityCode = branch.cityCode,
            cntDrmn = treatmentSupportCode,
            cntFreeJobCode = freeJobCode,
            guid = contractImageGuid,
            guidName = contractImageName,
            premiumRateCode = premiumRateCode,
            provinceCode = branch.provinceCode,
        ),
        protector = GuardianShipDetailDN(
            proCode = wardNationalId,
            guid = guid,
            guidName = documentDescription,
            nid = guardianForm.nationalId,
            fullName = guardianForm.fullName,
            protectorLetterNo = guardianForm.letterNumber,
            protectorLetterDate = Instant.fromEpochMilliseconds(letterEpoch).toString(),
        ),
    )
}

private const val DEFAULT_CONTRACT_IMAGE_GUID = "00"
private const val DEFAULT_CONTRACT_IMAGE_GUID_NAME = "00"
