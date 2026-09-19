package com.tamin.taminhamrah.mapper.workshop

import com.tamin.taminhamrah.model.workshop.AssignerContractDN
import com.tamin.taminhamrah.model.workshop.AssignerPartyDN
import com.tamin.taminhamrah.model.workshop.BaseDocumentCategory
import com.tamin.taminhamrah.model.workshop.BaseDocumentDN
import com.tamin.taminhamrah.model.workshop.BaseDocumentKind
import com.tamin.taminhamrah.model.workshop.ComputationalBaseDN
import com.tamin.taminhamrah.model.workshop.ComputationalBaseStatus
import com.tamin.taminhamrah.model.workshop.SettlementCertificateDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * The domain → screen half of واگذارندگان: which tab a پیمان lands in, and the formatting and grouping
 * decisions where a wrong answer would still look plausible on screen.
 */
class AssignerUiMapperTest {

    private fun contract(endDate: String) = AssignerContractDN(
        contractRow = "1",
        contractEndDate = endDate,
        employer = AssignerPartyDN(workshopId = "9028212822", branchCode = "0210"),
    )

    // ------------------------------------------------------------------ جاری / خاتمه‌یافته

    @Test
    fun aContractWhoseEndDateHasPassedIsFinished() {
        assertTrue(contract(endDate = "13990101").toPresentation().isFinished)
    }

    @Test
    fun aContractEndingInTheFutureIsActive() {
        assertFalse(contract(endDate = "15000101").toPresentation().isFinished)
    }

    /** Both shapes the services send a Jalali date in are read, not only the compact one. */
    @Test
    fun aSeparatedEndDateIsReadToo() {
        assertTrue(contract(endDate = "1399/01/01").toPresentation().isFinished)
    }

    /**
     * No end date is not an ended contract.
     *
     * The field is unconfirmed on the live service. If it never arrives, every پیمان must still show
     * under جاری rather than the whole list disappearing into خاتمه‌یافته.
     */
    @Test
    fun aMissingOrUnreadableEndDateStaysActive() {
        assertFalse(contract(endDate = "").toPresentation().isFinished)
        assertFalse(contract(endDate = "2024-03-20T00:00:00Z").toPresentation().isFinished)
    }

    // ------------------------------------------------------------------- درخواست مفاصاحساب

    /**
     * The request is a PUT onto an id built from four keys; a پیمان missing any of them offers the
     * action disabled rather than filing under an id the service does not know.
     */
    @Test
    fun settlementNeedsEveryKeyOfTheRequestId() {
        val complete = AssignerContractDN(
            contractRow = "1",
            contractSequence = "3",
            branchCode = "0310",
            employer = AssignerPartyDN(workshopId = "9028212822", branchCode = "0210"),
        ).toPresentation()

        assertTrue(complete.canRequestSettlement)
        assertFalse(complete.copy(contractSequence = "").canRequestSettlement)
        assertFalse(complete.copy(branchCode = "").canRequestSettlement)
        assertFalse(complete.copy(contractRow = "").canRequestSettlement)
    }

    // ----------------------------------------------------------------------- مبانی محاسباتی

    /**
     * Zero and missing are different answers for a مبلغ.
     *
     * The service reporting ۰ ریال is a fact; the service reporting nothing is a gap, and printing
     * the gap as ۰ would state an amount nobody gave.
     */
    @Test
    fun aZeroAmountPrintsWhileAMissingOneDashes() {
        val zero = ComputationalBaseDN(letterNumber = "1", amount = 0L).toPresentation()
        val missing = ComputationalBaseDN(letterNumber = "1", amount = null).toPresentation()

        assertTrue(zero.amount.contains("ریال"))
        assertFalse(missing.amount.contains("ریال"))
        assertNotEquals(zero.amount, missing.amount)
    }

    /** The raw amount survives next to the formatted one, so the bases can be added up. */
    @Test
    fun theRawAmountIsKeptForTheTotal() {
        assertEquals(840_000_000L, ComputationalBaseDN(amount = 840_000_000L).toPresentation().amountRials)
        assertEquals(null, ComputationalBaseDN(amount = null).toPresentation().amountRials)
    }

    /** The total adds what was sent and skips what was not; with nothing sent it is a gap, not ۰. */
    @Test
    fun theDeclaredTotalAddsOnlyTheAmountsSent() {
        val bases = listOf(840_000L, null, 160_000L).map { ComputationalBaseDN(amount = it).toPresentation() }

        assertEquals(ComputationalBaseDN(amount = 1_000_000L).toPresentation().amount, bases.declaredTotal())
        assertEquals(
            ComputationalBaseDN(amount = null).toPresentation().amount,
            listOf(ComputationalBaseDN(amount = null).toPresentation()).declaredTotal(),
        )
    }

    /**
     * The workflow stage the detail screen leads with. Each code is its old-app stage; a bare `3`
     * reads as `03`; a code outside the table is null, which the screen dashes as the old app did.
     */
    @Test
    fun theStatusCodeNamesItsStage() {
        assertEquals(ComputationalBaseStatus.SMS_SENT, ComputationalBaseDN(statusCode = "01").toPresentation().status)
        assertEquals(ComputationalBaseStatus.REJECTED, ComputationalBaseDN(statusCode = "04").toPresentation().status)
        assertEquals(
            ComputationalBaseStatus.SETTLEMENT_SERVED,
            ComputationalBaseDN(statusCode = "17").toPresentation().status,
        )
        assertEquals(ComputationalBaseStatus.CONFIRMED, ComputationalBaseDN(statusCode = "3").toPresentation().status)
        assertEquals(null, ComputationalBaseDN(statusCode = "18").toPresentation().status)
        assertEquals(null, ComputationalBaseDN(statusCode = "").toPresentation().status)
        // Seventeen stages, each with its own code.
        assertEquals(17, ComputationalBaseStatus.entries.map { it.code }.toSet().size)
    }

    /** A debt order not yet issued dashes, one that is prints in Persian digits. */
    @Test
    fun debtOrderNumbersPrintInPersianOrDash() {
        val base = ComputationalBaseDN(finalOrderNumber = "4512009").toPresentation()

        assertEquals("۴۵۱۲۰۰۹", base.finalOrderNumber)
        assertEquals(ComputationalBaseDN().toPresentation().letterNumber, base.estimatedOrderNumber)
    }

    /** A base with no period leaves both ends blank, so the row drops the line instead of «— تا —». */
    @Test
    fun aMissingPeriodStaysBlank() {
        val base = ComputationalBaseDN(letterNumber = "1").toPresentation()

        assertEquals("", base.periodStart)
        assertEquals("", base.periodEnd)
    }

    @Test
    fun aCertificatePrintsItsNumberInPersianAndSeparatesItsDate() {
        val certificate = SettlementCertificateDN(serial = "1080611", number = "38-7712405", date = "14021103")
            .toPresentation()

        assertEquals("۳۸-۷۷۱۲۴۰۵", certificate.number)
        assertEquals("۱۴۰۲/۱۱/۰۳", certificate.date)
    }

    /** Each of the four codes the old app names gets its heading; anything else is still shown. */
    @Test
    fun documentCodesMapToTheirHeadings() {
        val expected = mapOf(
            "1" to BaseDocumentCategory.LETTER,
            "2" to BaseDocumentCategory.SUBCONTRACTOR,
            "3" to BaseDocumentCategory.SUPPLEMENT,
            "4" to BaseDocumentCategory.FINAL_STATUS,
            "9" to BaseDocumentCategory.OTHER,
            "" to BaseDocumentCategory.OTHER,
        )
        expected.forEach { (code, category) ->
            assertEquals(category, BaseDocumentCategory.fromCode(code), "code «$code»")
        }
        assertEquals(BaseDocumentCategory.OTHER, BaseDocumentCategory.fromCode(null))
    }

    /**
     * A letter is identified by its code alone, whatever its type.
     *
     * The old app also required `documentType == "1"` for this heading, which hid a letter filed as a
     * PDF from every section. Pinned so the difference stays a decision rather than an accident.
     */
    @Test
    fun aLetterFiledAsPdfIsStillALetter() {
        val document = BaseDocumentDN(
            documentId = "b2",
            kind = BaseDocumentKind.PDF,
            categoryCode = "1",
        ).toPresentation()

        assertEquals(BaseDocumentCategory.LETTER, document.category)
        assertEquals(BaseDocumentKind.PDF, document.kind)
    }
}
