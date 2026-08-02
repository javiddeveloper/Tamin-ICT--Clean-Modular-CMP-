package com.tamin.taminhamrah.feature.treatment.ui.model

import com.tamin.taminhamrah.model.treatment.DeservedTreatmentPR

/**
 * Whether one insured person is entitled to treatment support.
 *
 * This is the decision only — how it is colored, worded and badged is the screen's
 * business. Keeping the rule free of Compose is what makes it testable.
 */
sealed interface CoverageStatus {

    /** The entitlement check has not come back yet. */
    data object Pending : CoverageStatus

    /** Treatment support is active. */
    data object Covered : CoverageStatus

    /**
     * Treatment support is absent. [reason] is always populated: a refusal is recognized
     * *by* its message, so there is no way to be rejected without one.
     */
    data class Rejected(val reason: String) : CoverageStatus
}

/**
 * The wording the service uses when it refuses in [DeservedTreatmentPR.message] instead of in
 * `finalDesc`.
 *
 * Matched as a whole phrase rather than on «عدم» alone: that fragment also sits inside ordinary
 * words — «بعدم», «مساعدم», «متقاعدم» — so a bare substring test can refuse someone who is covered.
 */
private const val NOT_ENTITLED_PHRASE = "عدم استحقاق"

/**
 * The service pads its text and leaves double spaces inside it, and the card now prints the
 * refusal verbatim, so whichever field wins is tidied before it is shown.
 */
private val WHITESPACE_RUN = Regex("\\s+")

/**
 * What this record says the refusal is, or null when it states none.
 *
 * `finalDesc` is the verdict field and is already phrased for the insured person to read, so it
 * decides. `message` narrates the event behind a refusal rather than being one — «…از کفالت خارج
 * شده است» is not itself a refusal — so it only counts when it carries [NOT_ENTITLED_PHRASE].
 *
 * What has actually been seen from the endpoint: an entitled person comes back with both fields
 * null, a refused one with `finalDesc` filled. A refusal worded only in `message` has not been
 * observed — the branch stays because reading a refusal as covered is the exact failure this rule
 * exists to prevent, and it costs one comparison.
 *
 * Both branches test for content before returning, so the result is never blank — which is what
 * lets [CoverageStatus.Rejected.reason] be non-null.
 */
private fun DeservedTreatmentPR.refusalOrNull(): String? = when {
    finalDesc.isNotBlank() -> finalDesc
    message.contains(NOT_ENTITLED_PHRASE) -> message
    else -> null
}?.replace(WHITESPACE_RUN, " ")?.trim()

/**
 * Resolves [patient]'s entitlement from the entitlement records.
 *
 * The endpoint behind these (`booklet-req/lackEntitlement`) returns the person's record either
 * way — an entitled person comes back with `finalDesc` and `message` both null, and a refused one
 * with `finalDesc` spelling out why. So the verdict is whether there is a refusal *worded*, not
 * whether a record exists: an entitled record carries plenty of other data (branch, insurance
 * type, booklet dates) that the card displays.
 *
 * Dependants are covered by the main insured person's entitlement and have no record of
 * their own, so only the main person is ever pending or rejected.
 */
fun coverageStatusOf(
    patient: PatientItemPR,
    deservedList: List<DeservedTreatmentPR>,
): CoverageStatus {
    if (patient.isDependent) return CoverageStatus.Covered

    val mainRecord = deservedList.firstOrNull() ?: return CoverageStatus.Pending

    return mainRecord.refusalOrNull()?.let(CoverageStatus::Rejected) ?: CoverageStatus.Covered
}
