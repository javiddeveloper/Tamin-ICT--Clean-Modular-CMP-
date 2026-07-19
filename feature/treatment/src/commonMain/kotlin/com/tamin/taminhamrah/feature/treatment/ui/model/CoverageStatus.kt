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
 * The service reports a refusal as free text rather than a flag, and marks it by negating
 * the entitlement wording ("عدم استحقاق").
 */
private const val NOT_ENTITLED_MARKER = "عدم"

/**
 * Resolves [patient]'s entitlement from the deserved-treatment records.
 *
 * Dependants are covered by the main insured person's entitlement and have no record of
 * their own, so only the main person is ever pending or rejected.
 */
fun coverageStatusOf(
    patient: PatientItem,
    deservedList: List<DeservedTreatmentPR>,
): CoverageStatus {
    if (patient.isDependent) return CoverageStatus.Covered

    val mainRecord = deservedList.firstOrNull() ?: return CoverageStatus.Pending

    return if (mainRecord.message.contains(NOT_ENTITLED_MARKER)) {
        CoverageStatus.Rejected(reason = mainRecord.message)
    } else {
        CoverageStatus.Covered
    }
}
