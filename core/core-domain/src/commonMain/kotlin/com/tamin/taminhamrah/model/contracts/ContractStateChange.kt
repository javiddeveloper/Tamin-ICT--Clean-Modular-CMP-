package com.tamin.taminhamrah.model.contracts

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
