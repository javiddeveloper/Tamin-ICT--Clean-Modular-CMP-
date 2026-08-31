package com.tamin.taminhamrah.model.workshop

/** One برگ پرداخت. [docDate] and [collectDate] are epoch millis, not Jalali strings. */
data class PaymentSheetDN(
    val debitNumber: String = "",
    val agreementRow: String = "",
    val payId: String = "",
    val amount: Long? = null,
    val docDate: Long? = null,
    val collectDate: Long? = null,
    val status: PaymentSheetStatus = PaymentSheetStatus.UNKNOWN,
    val statusDescription: String = "",
    val debitCreateReasonDescription: String = "",
    val payKindDescription: String = "",
    val documentNumber: String = "",
)

/** نوع برگ پرداخت, as the service codes it — also the value its search filter sends. */
enum class PaymentSheetStatus(val code: String) {
    /** باطل */
    VOID("1"),

    /** وصول */
    COLLECTED("2"),

    /** موثر */
    EFFECTIVE("3"),

    UNKNOWN(""),
    ;

    companion object {
        fun fromCode(code: String?): PaymentSheetStatus =
            entries.firstOrNull { it.code == code && it != UNKNOWN } ?: UNKNOWN
    }
}

/** One entry of the علت ایجاد بدهی picker. */
data class DebitReasonDN(
    val code: String = "",
    val title: String = "",
)
