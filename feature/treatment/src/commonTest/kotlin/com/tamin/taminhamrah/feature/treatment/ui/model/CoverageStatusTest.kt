package com.tamin.taminhamrah.feature.treatment.ui.model

import kotlin.test.Test
import kotlin.test.assertEquals

class CoverageStatusTest {

    private val mainPatient = PatientItem(
        nationalId = "1234567890",
        fullName = "رضا احمدی",
        isDependent = false,
    )

    private val dependantPatient = PatientItem(
        nationalId = "0987654321",
        fullName = "سارا احمدی",
        isDependent = true,
    )

    private fun recordWith(message: String) =
        TreatmentMocks.deservedTreatment.copy(message = message)

    @Test
    fun `main insured with no record yet is pending`() {
        assertEquals(
            CoverageStatus.Pending,
            coverageStatusOf(mainPatient, deservedList = emptyList()),
        )
    }

    @Test
    fun `main insured with a positive message is covered`() {
        assertEquals(
            CoverageStatus.Covered,
            coverageStatusOf(mainPatient, listOf(recordWith("مشمول حمایت درمانی"))),
        )
    }

    @Test
    fun `main insured with a negated message is rejected and carries the reason`() {
        val message = "عدم استحقاق درمان به دلیل بدهی کارفرما"
        assertEquals(
            CoverageStatus.Rejected(reason = message),
            coverageStatusOf(mainPatient, listOf(recordWith(message))),
        )
    }

    @Test
    fun `an empty message is treated as covered rather than rejected`() {
        // A refusal is recognized by its wording, so nothing to match means nothing to
        // refuse. This is what makes Rejected.reason safe to model as non-null, and it
        // guards the dialog against ever opening with empty text.
        assertEquals(
            CoverageStatus.Covered,
            coverageStatusOf(mainPatient, listOf(recordWith(""))),
        )
    }

    @Test
    fun `dependants are covered regardless of the main record`() {
        // Dependants have no record of their own, so an absent or negative main record
        // must not make them read as pending or rejected.
        assertEquals(
            CoverageStatus.Covered,
            coverageStatusOf(dependantPatient, deservedList = emptyList()),
        )
        assertEquals(
            CoverageStatus.Covered,
            coverageStatusOf(dependantPatient, listOf(recordWith("عدم استحقاق"))),
        )
    }
}
