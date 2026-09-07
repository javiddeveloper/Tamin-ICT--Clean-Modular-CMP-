package com.tamin.taminhamrah.feature.contracts.flow.specialjob

import com.tamin.taminhamrah.model.contracts.FreelanceSpecialJobCode

/**
 * Outcome of evaluating a special free-job selection (هلال احمر / دانشجوی پزشکی).
 * Kept pure so the four shared contract flows cannot diverge on these gates.
 */
sealed class SpecialFreeJobDecision {
    data class Accepted(
        val lockedPremiumRate: String,
        val forceTreatmentSupport: Boolean,
        val hidePremiumSlider: Boolean = true,
        val allowsPayment: Boolean = false,
    ) : SpecialFreeJobDecision()

    data class Rejected(val reason: SpecialFreeJobRejectReason) : SpecialFreeJobDecision()
}

enum class SpecialFreeJobRejectReason {
    RED_CRESCENT_DAY_LIMIT,
    RED_CRESCENT_NOT_ELIGIBLE,
    MEDICAL_STUDENT_NOT_ALLOWED,
}

const val RED_CRESCENT_DAY_LIMIT = 20

fun resolveRedCrescentSelection(
    jalaliDayOfMonth: Int,
    status: String,
): SpecialFreeJobDecision {
    if (jalaliDayOfMonth > RED_CRESCENT_DAY_LIMIT) {
        return SpecialFreeJobDecision.Rejected(SpecialFreeJobRejectReason.RED_CRESCENT_DAY_LIMIT)
    }
    if (!isRedCrossEligible(status)) {
        return SpecialFreeJobDecision.Rejected(SpecialFreeJobRejectReason.RED_CRESCENT_NOT_ELIGIBLE)
    }
    return SpecialFreeJobDecision.Accepted(
        lockedPremiumRate = FreelanceSpecialJobCode.RED_CRESCENT_PREMIUM_RATE,
        forceTreatmentSupport = true,
    )
}

fun resolveMedicalStudentSelection(status: String): SpecialFreeJobDecision {
    if (status != FreelanceSpecialJobCode.MEDICAL_STUDENT_OK_STATUS) {
        return SpecialFreeJobDecision.Rejected(SpecialFreeJobRejectReason.MEDICAL_STUDENT_NOT_ALLOWED)
    }
    return SpecialFreeJobDecision.Accepted(
        lockedPremiumRate = FreelanceSpecialJobCode.MEDICAL_STUDENT_PREMIUM_RATE,
        forceTreatmentSupport = false,
    )
}

fun isRedCrossEligible(status: String): Boolean =
    status.equals(FreelanceSpecialJobCode.RED_CRESCENT_ELIGIBLE_STATUS, ignoreCase = true) ||
        status.equals("true", ignoreCase = true) ||
        status == "1"
