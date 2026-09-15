package com.tamin.taminhamrah.model.pension

/** `statusDesc` of a pension inquiry row. */
enum class PensionerStatusDN(val code: String) {
    /** The person is not a pensioner at all. */
    NOT_PENSIONER("00"),
    ACTIVE("01"),
    ;

    companion object {
        fun fromCode(code: String?): PensionerStatusDN? = entries.firstOrNull { it.code == code?.trim() }
    }
}
