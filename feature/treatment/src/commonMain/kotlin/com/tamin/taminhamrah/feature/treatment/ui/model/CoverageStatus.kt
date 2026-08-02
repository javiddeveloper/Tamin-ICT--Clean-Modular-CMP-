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
 * Some refusals are worded only in [DeservedTreatmentPR.message], by negating the entitlement
 * wording ("عدم استحقاق"), without a `finalDesc` to go with them.
 */
private const val NOT_ENTITLED_MARKER = "عدم"

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

    // finalDesc first: it is the verdict, and it is worded for the insured person to read.
    // message is the fallback, and only when it actually negates — it otherwise describes the
    // event behind a refusal rather than being one.
    return when {
        mainRecord.finalDesc.isNotBlank() ->
            CoverageStatus.Rejected(reason = mainRecord.finalDesc)

        mainRecord.message.contains(NOT_ENTITLED_MARKER) ->
            CoverageStatus.Rejected(reason = mainRecord.message)

        else -> CoverageStatus.Covered
    }
}
