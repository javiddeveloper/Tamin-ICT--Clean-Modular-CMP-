package com.tamin.taminhamrah.feature.contracts.flow.preflight

import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.ContractStatusCode
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import com.tamin.taminhamrah.util.PersianDateFormatter

enum class ContractPreflightBlock {
    NOT_REGISTERED,
    ACTIVE_CONTRACT,
    UNDER_AGE,
    CANCELLED_OVER_20_DAYS,
    CANCELLED_OVER_3_MONTHS,
    OTHER_ACTIVE_CONTRACT,
}

fun resolvePreflightBlock(
    registration: RegistrationInfoPR,
    typedContracts: List<ContractDN>,
    allContracts: List<ContractDN>,
    currentPremiumTypeCode: String,
): ContractPreflightBlock? {
    if (!registration.insuranceIdValid) return ContractPreflightBlock.NOT_REGISTERED

    if (isUnderAge(registration.dateOfBirthEpoch)) return ContractPreflightBlock.UNDER_AGE

    typedContracts.forEach { contract ->
        when (contract.contractStatusObject?.selfIsuContStatCode) {
            ContractStatusCode.ACTIVE -> return ContractPreflightBlock.ACTIVE_CONTRACT
            ContractStatusCode.CANCELLED_OVER_20_DAYS -> return ContractPreflightBlock.CANCELLED_OVER_20_DAYS
            ContractStatusCode.CANCELLED_OVER_3_MONTHS -> return ContractPreflightBlock.CANCELLED_OVER_3_MONTHS
        }
    }

    val hasOtherActive = allContracts.any { contract ->
        contract.contractStatusObject?.selfIsuContStatCode == ContractStatusCode.ACTIVE &&
            contract.premiumType?.insuranceTypeCode?.let { it != currentPremiumTypeCode } == true
    }
    if (hasOtherActive) return ContractPreflightBlock.OTHER_ACTIVE_CONTRACT

    return null
}

private fun isUnderAge(dateOfBirthEpoch: Long?): Boolean {
    val age = PersianDateFormatter.ageYearsFromBirthEpoch(dateOfBirthEpoch) ?: return false
    return age < MINIMUM_CONTRACT_AGE_YEARS
}

private const val MINIMUM_CONTRACT_AGE_YEARS = 18
