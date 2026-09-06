package com.tamin.taminhamrah.model.contracts

object ContractFreeJobCode {
    const val STUDENT_CONTRACT_CODE = "099796"
    const val WOMEN_CONTRACT_CODE = "099785"

    /**
     * Job codes that change which امور قرارداد operations a contract offers (mirrored from
     * `old_android` `Constants`): red-crescent members can't پرداخت حق بیمه or غیرفعال کردن,
     * medical students can't پرداخت حق بیمه.
     */
    const val RED_CRESCENT_CODE = "113798"
    const val MEDICAL_STUDENT_CODE = "110977"
}
