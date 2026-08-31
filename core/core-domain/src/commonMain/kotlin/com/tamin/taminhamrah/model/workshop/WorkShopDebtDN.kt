package com.tamin.taminhamrah.model.workshop

/**
 * A workshop debt, as served by both the payable list (گردش حساب بدهی) and the objectionable list
 * (اعتراض به بدهی) — one shape, two endpoints.
 */
data class WorkShopDebtDN(
    val debitNumber: String = "",
    /** تاریخ ابلاغ — what the objection filing window is measured from. */
    val orderRecipeDate: String = "",
    val customerCode: String = "",
    val customerTypeCode: String = "",
    val agreementRow: String = "",
    val debitAmount: Long? = null,
    val debitRemain: Long? = null,
    val debitStartDate: String = "",
    val debitEndDate: String = "",
    val debitStepCode: String = "",
    val debitStatCode: String = "",
    val debitCreateReasonDescription: String = "",
    val primaryVoteNumber: String = "",
    val primaryVoteDate: String = "",
    val executiveNotifyDate: String = "",
    /** Non-null once an objection has been filed; it addresses that objection's PDF. */
    val seqNo: Long? = null,
) {
    /** What this row's single action does — see [ObjectionKind]. */
    val objectionKind: ObjectionKind
        get() = when {
            seqNo != null -> ObjectionKind.FILED
            debitStepCode == STEP_ESTIMATE && debitStatCode == STAT_OBJECTIONABLE -> ObjectionKind.ESTIMATE
            debitStepCode == STEP_PRIMARY_VOTE && debitStatCode == STAT_OBJECTIONABLE -> ObjectionKind.PRIMARY_VOTE
            else -> ObjectionKind.FILED
        }

    /** The بدوی vote block is only part of the row when the service actually sent a vote number. */
    val hasPrimaryVote: Boolean get() = primaryVoteNumber.isNotBlank()

    private companion object {
        const val STEP_ESTIMATE = "01"
        const val STEP_PRIMARY_VOTE = "02"
        const val STAT_OBJECTIONABLE = "03"
    }
}

/**
 * Which objection a debt row admits.
 *
 * [FILED] covers both "already objected" and "not objectionable": in either case the row offers
 * viewing rather than filing, which is exactly what the old client collapsed into its DEFAULT.
 * [filingWindowDays] is the deadline the service's `diff-days` count is checked against.
 */
enum class ObjectionKind(val filingWindowDays: Int?) {
    /** اعتراض به بدهی برآوردی */
    ESTIMATE(filingWindowDays = 31),

    /** اعتراض به رای هیئت بدوی */
    PRIMARY_VOTE(filingWindowDays = 21),

    /** مشاهده اعتراض */
    FILED(filingWindowDays = null),
    ;

    /** Whether an objection filed [elapsedDays] after the debt was served is still in time. */
    fun isWithinWindow(elapsedDays: Int): Boolean =
        filingWindowDays?.let { elapsedDays <= it } ?: false
}
