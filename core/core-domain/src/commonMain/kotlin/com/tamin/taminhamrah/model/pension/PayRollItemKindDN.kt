package com.tamin.taminhamrah.model.pension

/** `clpType` of a payslip row: what the amount is. */
enum class PayRollItemKindDN(val code: String) {
    PAYMENT("1"),
    DEDUCTION("2"),
    LOAN("3"),
    ;

    companion object {
        fun fromCode(code: String?): PayRollItemKindDN? = entries.firstOrNull { it.code == code?.trim() }
    }
}
