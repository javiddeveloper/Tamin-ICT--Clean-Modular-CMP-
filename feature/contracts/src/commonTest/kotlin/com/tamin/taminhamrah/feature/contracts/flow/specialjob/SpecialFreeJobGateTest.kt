package com.tamin.taminhamrah.feature.contracts.flow.specialjob

import com.tamin.taminhamrah.model.contracts.FreelanceSpecialJobCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SpecialFreeJobGateTest {

    @Test
    fun `red crescent rejects after day limit`() {
        val decision = resolveRedCrescentSelection(
            jalaliDayOfMonth = RED_CRESCENT_DAY_LIMIT + 1,
            status = FreelanceSpecialJobCode.RED_CRESCENT_ELIGIBLE_STATUS,
        )

        val rejected = assertIs<SpecialFreeJobDecision.Rejected>(decision)
        assertEquals(SpecialFreeJobRejectReason.RED_CRESCENT_DAY_LIMIT, rejected.reason)
    }

    @Test
    fun `red crescent rejects when status is not eligible`() {
        val decision = resolveRedCrescentSelection(
            jalaliDayOfMonth = 1,
            status = "denied",
        )

        val rejected = assertIs<SpecialFreeJobDecision.Rejected>(decision)
        assertEquals(SpecialFreeJobRejectReason.RED_CRESCENT_NOT_ELIGIBLE, rejected.reason)
    }

    @Test
    fun `red crescent accepts eligible status and locks premium`() {
        val decision = resolveRedCrescentSelection(
            jalaliDayOfMonth = RED_CRESCENT_DAY_LIMIT,
            status = FreelanceSpecialJobCode.RED_CRESCENT_ELIGIBLE_STATUS,
        )

        val accepted = assertIs<SpecialFreeJobDecision.Accepted>(decision)
        assertEquals(FreelanceSpecialJobCode.RED_CRESCENT_PREMIUM_RATE, accepted.lockedPremiumRate)
        assertEquals(true, accepted.forceTreatmentSupport)
        assertEquals(true, accepted.hidePremiumSlider)
        assertEquals(false, accepted.allowsPayment)
    }

    @Test
    fun `red crescent treats true and 1 as eligible`() {
        assertIs<SpecialFreeJobDecision.Accepted>(
            resolveRedCrescentSelection(jalaliDayOfMonth = 5, status = "true"),
        )
        assertIs<SpecialFreeJobDecision.Accepted>(
            resolveRedCrescentSelection(jalaliDayOfMonth = 5, status = "1"),
        )
    }

    @Test
    fun `medical student rejects non-ok status`() {
        val decision = resolveMedicalStudentSelection(status = "ok")

        val rejected = assertIs<SpecialFreeJobDecision.Rejected>(decision)
        assertEquals(SpecialFreeJobRejectReason.MEDICAL_STUDENT_NOT_ALLOWED, rejected.reason)
    }

    @Test
    fun `medical student accepts ok14 and locks premium without forced treatment`() {
        val decision = resolveMedicalStudentSelection(
            status = FreelanceSpecialJobCode.MEDICAL_STUDENT_OK_STATUS,
        )

        val accepted = assertIs<SpecialFreeJobDecision.Accepted>(decision)
        assertEquals(FreelanceSpecialJobCode.MEDICAL_STUDENT_PREMIUM_RATE, accepted.lockedPremiumRate)
        assertEquals(false, accepted.forceTreatmentSupport)
        assertEquals(true, accepted.hidePremiumSlider)
        assertEquals(false, accepted.allowsPayment)
    }
}
