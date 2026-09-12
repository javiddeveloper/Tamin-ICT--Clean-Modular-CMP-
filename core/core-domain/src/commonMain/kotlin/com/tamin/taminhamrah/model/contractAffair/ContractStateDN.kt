package com.tamin.taminhamrah.model.contractAffair

/**
 * A selectable contract-termination reason from `special-insured-services/list-self-contract-state`
 * (علت خاتمه قرارداد). [code] is the `selfIsuContStatDode` value and is what the cancel endpoint
 * expects as its path segment.
 */
data class ContractStateDN(
    val code: Int?,
    val description: String?,
)

/**
 * The `contractStatus` value sent in the body of `update-self-contract-state` /
 * `freelance-update-self-contract-state` when the insured changes a contract's state.
 *
 * Only the cancel (غیرفعال کردن قرارداد) transition is exposed today; `old_android` sends the
 * literal `99` (`Constants.STATUS_CANCEL_CONTRACT`).
 */
enum class ContractStateChange(val value: Int) {
    CANCEL(99),
}

/**
 * Input for غیرفعال کردن قرارداد (`ContractAffairRepository.cancelContract`).
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
