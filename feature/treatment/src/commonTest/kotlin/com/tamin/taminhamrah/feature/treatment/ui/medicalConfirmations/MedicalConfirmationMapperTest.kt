package com.tamin.taminhamrah.feature.treatment.ui.medicalConfirmations

import com.tamin.taminhamrah.mapper.treatment.toPresentation
import com.tamin.taminhamrah.model.treatment.ConfirmationStatus
import com.tamin.taminhamrah.model.treatment.MedicalConfirmationDN
import com.tamin.taminhamrah.model.treatment.confirmationStatus
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pins the presentation mapper to a real `commission-confrimation` row.
 *
 * The values below are copied from a live response, not invented: dates arrive as an unseparated
 * `14011011`, the verdict is spelt with an Arabic ي, and the day counts are strings. Showing any
 * of that raw is what the mapper exists to prevent.
 */
class MedicalConfirmationMapperTest {

    @Test
    fun testUnseparatedDatesBecomeReadable() {
        val row = livePayloadRow().toPresentation()

        assertEquals("۱۴۰۱/۱۰/۱۱", row.outpatientRestStartDate)
        assertEquals("۱۴۰۱/۱۲/۱۰", row.outpatientRestEndDate)
    }

    @Test
    fun testOmittedDatesReadAsAPlaceholderRatherThanDigits() {
        val row = livePayloadRow().toPresentation()

        assertEquals("-", row.inpatientRestStartDate)
        assertEquals("-", row.inpatientRestEndDate)
        assertEquals("", row.unapprovedFromDate)
        assertEquals("", row.unapprovedToDate)
    }

    @Test
    fun testArabicLettersAreNormalisedForDisplay() {
        val row = livePayloadRow().toPresentation()

        // The service sends «تائيد شده» with an Arabic yeh; the filter chip beside it reads
        // «تائید شده» with a Persian one.
        assertEquals("تائید شده", row.statusDesc)
        assertEquals("تائید شعبه", row.branchStatus)
    }

    @Test
    fun testTheLiveRowStillClassifiesAsApproved() {
        assertEquals(ConfirmationStatus.APPROVED, livePayloadRow().toPresentation().confirmationStatus)
    }

    @Test
    fun testAbsentRowIdentifierHidesTheCertificateActions() {
        // The service sends no repId at all, so both row actions have nothing to address.
        assertEquals(false, livePayloadRow().toPresentation().hasCertificate)
    }

    /** One row of the live response, verbatim apart from the trim the DTO mapper already applied. */
    private fun livePayloadRow() = MedicalConfirmationDN(
        repId = null,
        supportType = "غرامت دستمزد",
        treatmentCenter = "شوراي پزشکي تهران-پلي کلينيک قدس",
        outpatientRestStartDate = "14011011",
        outpatientRestEndDate = "14011210",
        numberOfOutpatientDays = "60",
        inpatientRestStartDate = null,
        inpatientRestEndDate = null,
        numberOfInpatientDays = "0",
        unapprovedFromDate = null,
        unapprovedToDate = null,
        branchName = "بيست تهران",
        branchStatus = "تائيد شعبه",
        description = "استراحت هاي پزشکي نامبرده ... مورد تاييد ميباشد  (سرپايي)",
        statusDesc = "تائيد شده",
    )
}
