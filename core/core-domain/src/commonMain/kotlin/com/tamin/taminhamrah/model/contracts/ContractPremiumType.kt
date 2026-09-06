package com.tamin.taminhamrah.model.contracts

/**
 * The kind of special-insured contract, as the `premiumTypeCode` / `insuranceTypeCode`
 * the `special-insured-services` backend uses on the wire.
 *
 * Drives which variant of the cancel and PDF-report endpoints a contract operation hits
 * (see `ContractsRepository.cancelContract` / `downloadContractReport`), mirroring
 * `old_android`'s `ContractListFragment` branching on `insuranceTypeCode`.
 *
 * The pre-existing [ContractPremiumTypeCode] constant object is kept for the contract-creation
 * flow that already references it; new operations code should prefer this enum.
 */
enum class ContractPremiumType(val code: String) {
    /** حرف و مشاغل آزاد */
    FREELANCE("01"),

    /** بیمه اختیاری */
    OPTIONAL("02"),

    /** تکمیل سوابق کسری از ماه */
    FRACTION("38"),
    ;

    companion object {
        fun fromCode(code: String?): ContractPremiumType? =
            entries.firstOrNull { it.code == code }
    }
}
