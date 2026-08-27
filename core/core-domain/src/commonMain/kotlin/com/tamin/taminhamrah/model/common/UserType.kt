package com.tamin.taminhamrah.model.common

enum class UserType {
    PENSIONER, INSURED, ANONYMOUS, TEMPORARY;

    companion object {
        fun fromNameOrNull(name: String?): UserType? = entries.find { it.name == name }
    }
}
