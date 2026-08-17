package com.tamin.taminhamrah.model.workshop

/**
 * One debt a ماده ۱۶ request can be filed against.
 *
 * Every figure here is echoed back verbatim when the request is submitted, which is why the whole
 * row travels with the request rather than just its number.
 */
data class WorkshopsDebtListModelDN(
    val debitNumber: String = "",
    val debitAmount: Long? = null,
    val debitRemain: Long? = null,
    val debitStartDate: String = "",
    val debitEndDate: String = "",
    val debitStepCode: String = "",
    val agreementRow: String = "",
    val customerTypeCode: String = "",
    val insuranceAmount: Long? = null,
    val unemploymentAmount: Long? = null,
    val fineAmount: Long? = null,
    val otherAmount: Long? = null,
    /** Selects the ماده ۴۲/۴۳/۴۴ wording on step 2: `1` / `2` / `3`. */
    val kindDoc: String = "",
    val executiveNumber: String = "",
    val executiveDate: String = "",
    /** تاریخ ابلاغ اجراییه — the date the one-day filing deadline is measured from. */
    val executiveNotifyDate: String = "",
    val primaryVoteNumber: String = "",
    val primaryVoteDate: String = "",
    val seqNo: Long? = null,
    val status: Article16RequestStatus = Article16RequestStatus.NONE,
)

/**
 * State of the ماده ۱۶ request filed against a debt.
 *
 * [NONE] is the row with no request yet — the only state that offers filing one. The service sends
 * no code at all in that case, so it is not given one here either.
 */
enum class Article16RequestStatus(val code: String?) {
    NONE(null),

    /** ثبت درخواست */
    SUBMITTED("1"),

    /** نقص مدارک */
    DOCUMENT_DEFECT("7"),

    /** رد درخواست */
    REJECTED("8"),

    /** تایید درخواست */
    APPROVED("9"),

    /** نامشخص — a code the service published that this client does not model. */
    UNKNOWN(""),
    ;

    companion object {
        fun fromCode(code: String?): Article16RequestStatus = when {
            code.isNullOrBlank() || code == "0" -> NONE
            else -> entries.firstOrNull { it.code == code } ?: UNKNOWN
        }
    }
}
