package com.tamin.taminhamrah.model.contractAffair

/**
 * The kind of special-insured contract, as the `premiumTypeCode` / `insuranceTypeCode`
 * the `special-insured-services` backend uses on the wire.
 *
 * Drives which variant of the cancel and PDF-report endpoints a contract operation hits
 * (see `ContractAffairRepository.cancelContract` / `downloadContractReport`), mirroring
 * `old_android`'s `ContractListFragment` branching on `insuranceTypeCode`.
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

/**
 * Job codes that change which امور قرارداد operations a contract offers (mirrored from
 * `old_android` `Constants`): red-crescent members can't پرداخت حق بیمه or غیرفعال کردن,
 * medical students can't پرداخت حق بیمه.
 */
object ContractFreeJobCode {
    const val RED_CRESCENT_CODE = "113798"
    const val MEDICAL_STUDENT_CODE = "110977"

    /** `cntFreeJobCode` for a زنان خانه‌دار contract (legacy `Constants.WOMEN_CONTRACT_CODE`). */
    const val WOMEN_CONTRACT_CODE = "099785"

    /** `cntFreeJobCode` for a دانشجو contract (legacy `Constants.STUDENT_CONTRACT_CODE`). */
    const val STUDENT_CONTRACT_CODE = "099796"
}
