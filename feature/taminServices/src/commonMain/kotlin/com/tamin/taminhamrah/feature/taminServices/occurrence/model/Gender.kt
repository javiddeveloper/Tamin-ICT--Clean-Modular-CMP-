package com.tamin.taminhamrah.feature.taminServices.occurrence.model;

enum class Gender(
    val code: String,
    val displayName: String,
    /** The legacy "occurence" submit endpoint's own gender scale (1 = male, 2 = female). */
    val legacyCode: Int,
) {
    MALE("01", "مرد", 1),
    FEMALE("02", "زن", 2);

    companion object {
        fun fromCode(code: String?): Gender? =
            entries.find { it.code == code }
    }
}
