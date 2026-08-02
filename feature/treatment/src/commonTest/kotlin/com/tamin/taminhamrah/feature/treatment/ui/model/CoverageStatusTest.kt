package com.tamin.taminhamrah.feature.treatment.ui.model

import kotlin.test.Test
import kotlin.test.assertEquals

class CoverageStatusTest {

    private val mainPatient = PatientItemPR(
        nationalId = "1234567890",
        fullName = "رضا احمدی",
        isDependent = false,
    )

    private val dependantPatient = PatientItemPR(
        nationalId = "0987654321",
        fullName = "سارا احمدی",
        isDependent = true,
    )

    private fun recordWith(message: String = "", finalDesc: String = "") =
        TreatmentMocks.deservedTreatment.copy(message = message, finalDesc = finalDesc)

    @Test
    fun `a refusal worded only in finalDesc is rejected and carries it as the reason`() {
        // Verbatim from booklet-req/lackEntitlement/0017312213, who the card wrongly showed as
        // entitled: the refusal is in finalDesc and the message never says "عدم", so matching on
        // the message alone read this as covered.
        val finalDesc = "بدليل مختومه‌شدن قرارداد مشاغل آزاد، برخورداري از درمان ميسر نمي‌باشد."
        val message =
            " بيمه شده تبعي در تاريخ  1400/01/01 در شعبه  سيزده تهران بدون ثبت دليل  از کفالت خارج شده است "

        assertEquals(
            CoverageStatus.Rejected(reason = finalDesc),
            coverageStatusOf(mainPatient, listOf(recordWith(message = message, finalDesc = finalDesc))),
        )
    }

    @Test
    fun `the reason is unpadded and single-spaced for the card to print`() {
        // The service pads its text and doubles spaces inside it; the card shows this verbatim.
        assertEquals(
            CoverageStatus.Rejected(reason = "بدليل مختومه‌شدن قرارداد، برخورداري ميسر نمي‌باشد."),
            coverageStatusOf(
                mainPatient,
                listOf(recordWith(finalDesc = "  بدليل مختومه‌شدن قرارداد،  برخورداري  ميسر نمي‌باشد. ")),
            ),
        )
    }

    @Test
    fun `a message containing عدم inside an ordinary word is not a refusal`() {
        // «عدم» is a substring of everyday words, so matching it alone would refuse someone who
        // is covered. Only the full «عدم استحقاق» counts.
        assertEquals(
            CoverageStatus.Covered,
            coverageStatusOf(mainPatient, listOf(recordWith(message = "پس از آن مساعدم کردند"))),
        )
    }

    @Test
    fun `a message narrating an event is not itself a refusal`() {
        // Verbatim from 0017312213, minus the finalDesc: the message describes leaving sponsorship
        // and never says «عدم استحقاق», so on its own it decides nothing.
        assertEquals(
            CoverageStatus.Covered,
            coverageStatusOf(
                mainPatient,
                listOf(recordWith(message = " بيمه شده تبعي در تاريخ  1400/01/01 از کفالت خارج شده است ")),
            ),
        )
    }

    @Test
    fun `a record with neither field worded is covered`() {
        // Verbatim shape of booklet-req/lackEntitlement/0946168113, who is entitled: the endpoint
        // still returns a record, with message and finalDesc both null. Presence of a record is
        // therefore not itself a refusal.
        assertEquals(
            CoverageStatus.Covered,
            coverageStatusOf(mainPatient, listOf(recordWith())),
        )
    }

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
