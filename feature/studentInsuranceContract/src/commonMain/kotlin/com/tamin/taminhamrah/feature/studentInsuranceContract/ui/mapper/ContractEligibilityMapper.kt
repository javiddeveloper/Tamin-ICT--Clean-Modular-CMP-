package com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper

import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.ContractEligibilityPR
import com.tamin.taminhamrah.model.contracts.ContractDN

fun List<ContractDN>.resolveEligibility(): ContractEligibilityPR {
    val contract = firstOrNull { !it.eligibilityStatus.isNullOrBlank() }
        ?: return ContractEligibilityPR.unavailable()
    val statusCode = contract.eligibilityStatus?.toIntOrNull() ?: -1
    return ContractEligibilityPR.from(
        statusCode = statusCode,
        history = contract.history,
        age = contract.age,
    )
}
