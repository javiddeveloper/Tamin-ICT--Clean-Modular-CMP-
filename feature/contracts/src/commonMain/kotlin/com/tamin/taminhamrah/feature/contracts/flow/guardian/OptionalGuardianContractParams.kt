package com.tamin.taminhamrah.feature.contracts.flow.guardian

import com.tamin.taminhamrah.model.contractFlow.BranchSelectionFormPR
import com.tamin.taminhamrah.model.contractFlow.GuardianFormPR
import com.tamin.taminhamrah.model.contracts.GuardianShipDetailDN
import com.tamin.taminhamrah.model.contracts.OptionalContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.OptionalMakeContractRequestDN
import kotlinx.datetime.Instant

/**
 * Builds the optional-insurance «contract by guardian» request.
 * Returns null when branch, guardian form, document guid, letter date, or ward national id is incomplete.
 */
fun buildOptionalContractByGuardianParams(
    selectedSalary: Long,
    branch: BranchSelectionFormPR,
    treatmentSupportCode: String,
    premiumRateCode: String,
    guardianForm: GuardianFormPR,
    wardNationalId: String,
    documentDescription: String,
): OptionalContractByGuardianParams? {
    if (!branch.isValid) return null
    if (!guardianForm.isValid) return null
    val guid = guardianForm.documentGuid ?: return null
    val letterEpoch = guardianForm.letterDateEpoch ?: return null
    if (wardNationalId.isBlank()) return null

    return OptionalContractByGuardianParams(
        selectedSalary = selectedSalary,
        contract = OptionalMakeContractRequestDN(
            brchCodeNew = branch.branchCode,
            cityCode = branch.cityCode,
            cntDrmn = treatmentSupportCode,
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
