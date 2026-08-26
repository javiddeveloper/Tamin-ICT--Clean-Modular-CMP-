package com.tamin.taminhamrah.model.pension

enum class PaymentTypeDN(val code: String) {
    MONTHLY("01"),
    BONUS("03"),
    ARREARS_INCREASE("08");

    companion object {
        fun fromCode(code: String): PaymentTypeDN = entries.find { it.code == code } ?: MONTHLY
    }
}
