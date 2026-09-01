package com.tamin.taminhamrah.model.workshop

/** پیگیری وضعیت اعتراض — one previously-filed objection and its current review status. */
data class WorkShopObjectionDN(
    val seqNo: Long? = null,
    val workshopId: String = "",
    val debitNumber: String = "",
    val branchCode: String = "",
    val objectionType: WorkShopObjectionType = WorkShopObjectionType.UNKNOWN,
    val objectionDate: String = "",
    val objectionDescription: String = "",
    val status: WorkShopObjectionStatus = WorkShopObjectionStatus.UNKNOWN,
    val voteTypeDescription: String = "",
)

/**
 * Which kind of request this row is, and which report endpoint its PDF comes from.
 *
 * Distinct from [ObjectionKind] on purpose: that enum answers whether a *new* objection may still
 * be filed against a debt row; this one answers what an *already-filed* row's document/report
 * endpoint and label are, and it has a third case (ماده ۱۶) that filing has no equivalent for.
 */
enum class WorkShopObjectionType(val code: String?) {
    /** اعتراض به بدهی برآوردی */
    ESTIMATE("1"),

    /** اعتراض به رای هیئت بدوی */
    PRIMARY_VOTE("2"),

    /** درخواست رسیدگی به بدهی قطعی (مادهٔ ۱۶) */
    ARTICLE_SIXTEEN("3"),

    UNKNOWN(null),
    ;

    companion object {
        fun fromCode(code: String?): WorkShopObjectionType =
            entries.firstOrNull { it.code == code } ?: UNKNOWN
    }
}

/**
 * Review status of a filed objection, as `debit-objection/objection-all` reports it.
 *
 * There is no "rejected" case here (unlike [ArticleSixteenRequestStatus]) — every code this service
 * sends is a step of one forward-moving review, not a terminal accept/reject pair.
 */
enum class WorkShopObjectionStatus(val code: String?) {
    /** ثبت درخواست */
    SUBMITTED("1"),

    /** بازنگری محاسبات */
    CALCULATION_REVIEW("2"),

    /** طرح در هیئت */
    BOARD_REVIEW("3"),

    /** تجدید محاسبه شده */
    RECALCULATED("4"),

    /** تخصیص زمان */
    TIME_ALLOCATED("5"),

    /** تایید رای */
    APPROVED("6"),

    UNKNOWN(null),
    ;

    companion object {
        fun fromCode(code: String?): WorkShopObjectionStatus =
            entries.firstOrNull { it.code == code } ?: UNKNOWN
    }
}

/** One پیامک sent about an objection, in the order the service returns them. */
data class SmsMessageDN(
    val description: String = "",
    val status: WorkShopObjectionStatus = WorkShopObjectionStatus.UNKNOWN,
)
