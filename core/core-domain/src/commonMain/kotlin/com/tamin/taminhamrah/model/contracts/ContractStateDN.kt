package com.tamin.taminhamrah.model.contracts

/**
 * A selectable contract-termination reason from `special-insured-services/list-self-contract-state`
 * (علت خاتمه قرارداد). [code] is the `selfIsuContStatDode` value and is what the cancel endpoint
 * expects as its path segment.
 */
data class ContractStateDN(
    val code: Int?,
    val description: String?,
)
