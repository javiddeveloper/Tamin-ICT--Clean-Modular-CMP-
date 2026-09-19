package com.tamin.taminhamrah.mapper.contracts

import com.tamin.taminhamrah.model.contractFlow.ContractEligibilityPR
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
