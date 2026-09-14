package com.tamin.taminhamrah.feature.contracts.flow

import com.tamin.taminhamrah.model.contracts.ContractFreeJobCode
import com.tamin.taminhamrah.model.contracts.ContractPremiumTypeCode

/**
 * Maps a list-row contract to the create/edit [ContractType] config, matching legacy
 * `ContractListFragment` «ویرایش قرارداد» branching on `insuranceTypeCode` + job code.
 */
fun resolveContractTypeForEdit(
    premiumTypeCode: String,
    freeJobCode: String,
): ContractType? = when (premiumTypeCode) {
    ContractPremiumTypeCode.OPTIONAL -> ContractType.OPTIONAL
    ContractPremiumTypeCode.FREELANCE -> when (freeJobCode) {
        ContractFreeJobCode.STUDENT_CONTRACT_CODE -> ContractType.STUDENT
        ContractFreeJobCode.WOMEN_CONTRACT_CODE -> ContractType.HOUSEWIFE
        else -> ContractType.FREELANCE
    }
    else -> null
}
