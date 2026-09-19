package com.tamin.taminhamrah.model.common

/** Gender code as the identity services send it. */
enum class GenderCodeDN(val code: String) {
    MALE("01"),
    FEMALE("02"),
    ;

    companion object {
        fun fromCode(code: String?): GenderCodeDN? = entries.firstOrNull { it.code == code?.trim() }
    }
}
