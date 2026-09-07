package com.tamin.taminhamrah.feature.contracts.flow.specialjob

import com.tamin.taminhamrah.model.contracts.FreelanceSpecialJobCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class SpecialFreeJobGateTest {

    @Test
    fun `red crescent rejects after day 20`() {
        val outcome = resolveSpecialFreeJob(
            jobCode = FreelanceSpecialJobCode.RED_CRESCENT_CODE,
            jalaliDay = 21,
            redCrossStatus = FreelanceSpecialJobCode.RED_CRESCENT_ELIGIBLE_STATUS,
            medicalStudentStatus = FreelanceSpecialJobCode.MEDICAL_STUDENT_OK_STATUS,
        )

        val rejected = assertIs<SpecialFreeJobOutcome.Rejected>(outcome)
        assertEquals(SpecialFreeJobRejectReason.RED_CRESCENT_DAY_LIMIT, rejected.reason)
    }

    @Test
    fun `red crescent rejects when eligibility service denies`() {
        val outcome = resolveSpecialFreeJob(
            jobCode = FreelanceSpecialJobCode.RED_CRESCENT_CODE,
            jalaliDay = 10,
            redCrossStatus = "denied",
            medicalStudentStatus = FreelanceSpecialJobCode.MEDICAL_STUDENT_OK_STATUS,
        )

        val rejected = assertIs<SpecialFreeJobOutcome.Rejected>(outcome)
        assertEquals(SpecialFreeJobRejectReason.RED_CRESCENT_NOT_ELIGIBLE, rejected.reason)
    }

    @Test
    fun `red crescent accepts eligible status on or before day 20`() {
        val outcome = resolveSpecialFreeJob(
            jobCode = FreelanceSpecialJobCode.RED_CRESCENT_CODE,
            jalaliDay = 20,
            redCrossStatus = "OK",
            medicalStudentStatus = "ignored",
        )

        val accepted = assertIs<SpecialFreeJobOutcome.Accepted>(outcome)
        assertTrue(accepted.forceTreatmentSupport)
        assertEquals(FreelanceSpecialJobCode.RED_CRESCENT_PREMIUM_RATE, accepted.lockedPremiumRate)
        assertTrue(accepted.hidePremiumSlider)
        assertEquals(false, accepted.allowsPayment)
    }

    @Test
    fun `medical student rejects when status is not ok14`() {
        val outcome = resolveSpecialFreeJob(
            jobCode = FreelanceSpecialJobCode.MEDICAL_STUDENT_CODE,
            jalaliDay = 5,
            redCrossStatus = "ignored",
            medicalStudentStatus = "ok",
        )

        val rejected = assertIs<SpecialFreeJobOutcome.Rejected>(outcome)
        assertEquals(SpecialFreeJobRejectReason.MEDICAL_STUDENT_NOT_ALLOWED, rejected.reason)
    }

    @Test
    fun `medical student accepts ok14 and locks premium rate`() {
        val outcome = resolveSpecialFreeJob(
            jobCode = FreelanceSpecialJobCode.MEDICAL_STUDENT_CODE,
            jalaliDay = 25,
            redCrossStatus = "ignored",
            medicalStudentStatus = FreelanceSpecialJobCode.MEDICAL_STUDENT_OK_STATUS,
        )

        val accepted = assertIs<SpecialFreeJobOutcome.Accepted>(outcome)
        assertEquals(false, accepted.forceTreatmentSupport)
        assertEquals(FreelanceSpecialJobCode.MEDICAL_STUDENT_PREMIUM_RATE, accepted.lockedPremiumRate)
        assertTrue(accepted.hidePremiumSlider)
        assertEquals(false, accepted.allowsPayment)
    }

    @Test
    fun `regular free job is not gated`() {
        val outcome = resolveSpecialFreeJob(
            jobCode = "099796",
            jalaliDay = 25,
            redCrossStatus = "denied",
            medicalStudentStatus = "denied",
        )

        assertIs<SpecialFreeJobOutcome.Regular>(outcome)
    }

    @Test
    fun `red cross eligibility accepts ok true and 1`() {
        assertTrue(isRedCrossEligible("ok"))
        assertTrue(isRedCrossEligible("TRUE"))
        assertTrue(isRedCrossEligible("1"))
    }
}
