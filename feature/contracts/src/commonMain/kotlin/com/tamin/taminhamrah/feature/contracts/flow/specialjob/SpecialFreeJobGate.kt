package com.tamin.taminhamrah.feature.contracts.flow.specialjob

import com.tamin.taminhamrah.model.contracts.FreelanceSpecialJobCode

enum class SpecialFreeJobRejectReason {
    RED_CRESCENT_DAY_LIMIT,
    RED_CRESCENT_NOT_ELIGIBLE,
    MEDICAL_STUDENT_NOT_ALLOWED,
}

sealed class SpecialFreeJobOutcome {
    data object Regular : SpecialFreeJobOutcome()

    data class Rejected(
        val reason: SpecialFreeJobRejectReason,
    ) : SpecialFreeJobOutcome()

    data class Accepted(
        val forceTreatmentSupport: Boolean,
        val lockedPremiumRate: String,
        val hidePremiumSlider: Boolean,
        val allowsPayment: Boolean,
    ) : SpecialFreeJobOutcome()
}

/**
 * Pure gate for freelance special free-job codes («هلال احمر» / «دانشجوی پزشکی»).
 * Callers fetch eligibility statuses, then pass them here with the current Jalali day.
 */
fun resolveSpecialFreeJob(
    jobCode: String,
    jalaliDay: Int,
    redCrossStatus: String,
    medicalStudentStatus: String,
): SpecialFreeJobOutcome = when (jobCode) {
    FreelanceSpecialJobCode.RED_CRESCENT_CODE -> {
        when {
            jalaliDay > RED_CRESCENT_DAY_LIMIT ->
                SpecialFreeJobOutcome.Rejected(SpecialFreeJobRejectReason.RED_CRESCENT_DAY_LIMIT)
            !isRedCrossEligible(redCrossStatus) ->
                SpecialFreeJobOutcome.Rejected(SpecialFreeJobRejectReason.RED_CRESCENT_NOT_ELIGIBLE)
            else -> SpecialFreeJobOutcome.Accepted(
                forceTreatmentSupport = true,
                lockedPremiumRate = FreelanceSpecialJobCode.RED_CRESCENT_PREMIUM_RATE,
                hidePremiumSlider = true,
                allowsPayment = false,
            )
        }
    }
    FreelanceSpecialJobCode.MEDICAL_STUDENT_CODE -> {
        if (medicalStudentStatus != FreelanceSpecialJobCode.MEDICAL_STUDENT_OK_STATUS) {
            SpecialFreeJobOutcome.Rejected(SpecialFreeJobRejectReason.MEDICAL_STUDENT_NOT_ALLOWED)
        } else {
            SpecialFreeJobOutcome.Accepted(
                forceTreatmentSupport = false,
                lockedPremiumRate = FreelanceSpecialJobCode.MEDICAL_STUDENT_PREMIUM_RATE,
                hidePremiumSlider = true,
                allowsPayment = false,
            )
        }
    }
    else -> SpecialFreeJobOutcome.Regular
}

fun isRedCrossEligible(status: String): Boolean =
    status.equals(FreelanceSpecialJobCode.RED_CRESCENT_ELIGIBLE_STATUS, ignoreCase = true) ||
        status.equals("true", ignoreCase = true) ||
        status == "1"

const val RED_CRESCENT_DAY_LIMIT = 20
