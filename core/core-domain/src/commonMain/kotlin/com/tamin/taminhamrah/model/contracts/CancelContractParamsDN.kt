package com.tamin.taminhamrah.model.contracts

/**
 * Input for غیرفعال کردن قرارداد (`ContractsRepository.cancelContract`).
 *
 * [stateCode] is the chosen [ContractStateDN.code] (the termination reason) and becomes the
 * request path segment; [premiumType] selects the optional vs. freelance endpoint variant.
 */
data class CancelContractParamsDN(
    val premiumType: ContractPremiumType,
    val stateCode: Int,
    val description: String?,
    val stateChange: ContractStateChange = ContractStateChange.CANCEL,
)
