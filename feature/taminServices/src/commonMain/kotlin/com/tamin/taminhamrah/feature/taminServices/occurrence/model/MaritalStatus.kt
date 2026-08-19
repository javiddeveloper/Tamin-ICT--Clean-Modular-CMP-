package com.tamin.taminhamrah.feature.taminServices.occurrence.model

enum class MaritalStatus(
    val code: String,
    val displayName: String
) {
    SINGLE("0", "مجرد"),
    MARRIED("1", "متأهل");

    companion object {
        fun fromCode(code: String?): MaritalStatus? =
            entries.find { it.code == code }
    }
}
