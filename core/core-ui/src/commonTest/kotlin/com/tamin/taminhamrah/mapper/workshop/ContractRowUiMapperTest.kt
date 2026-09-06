package com.tamin.taminhamrah.mapper.workshop

import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.WorkshopContractDN
import com.tamin.taminhamrah.model.workshop.WorkshopSummaryDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * The domain → card half of ردیف‌های پیمان.
 *
 * Both sources land on one card model, so what matters here is that they land on the *same* fields
 * from different places, and that a value the service never sent is never invented.
 */
class ContractRowUiMapperTest {

    private val agreement = EmployerAgreementDN(
        contractRow = "6",
        startDate = "14040407",
        // Deliberately different from startDate. The card's «تاریخ تعهد» tile reads `startdate`,
        // matching the old app's own binding — `list_item_employer_workshop.xml` binds
        // `label_date_of_commitment` to `item.getLocalDate(item.startdate)`, not to `letDate`.
        // Setting both is what makes the assertion below able to tell them apart.
        commitmentDate = "13990101",
        email = "karan.school@mail.com",
        mobile = "09143018372",
        workshop = WorkshopSummaryDN(
            workshopId = "9028212822",
            branchCode = "0210",
            name = "دبستان کارن ۲",
            address = "بجنورد، بلوار مدرس",
            // The workshop record carries its own contract row. The card must not read this one.
            contractRow = "999",
        ),
    )

    @Test
    fun agreementRowFormatsEveryColumnTheCardDraws() {
        val row = agreement.toContractRow()

        assertEquals("۶", row.rowLabel)
        assertEquals("۹۰۲۸۲۱۲۸۲۲", row.workshopCodeLabel)
        assertEquals("۱۴۰۴/۰۴/۰۷", row.commitmentDate)
        // Not letDate — see the fixture comment. On the live payload the two carry the same value,
        // so only a fixture that differs can pin the choice.
        assertNotEquals("۱۳۹۹/۰۱/۰۱", row.commitmentDate)
        assertEquals("۰۹۱۴۳۰۱۸۳۷۲", row.mobile)
        assertEquals("karan.school@mail.com", row.email)
        assertEquals("دبستان کارن ۲", row.name)
        assertEquals("بجنورد، بلوار مدرس", row.address)
    }

    /**
     * ردیف پیمان belongs to the agreement, not to the workshop nested inside it.
     *
     * Both are called a contract row and both are populated on this endpoint, so reading the wrong
     * one produces a plausible number on every card rather than an obvious failure.
     */
    @Test
    fun agreementRowUsesTheAgreementsRowNotTheWorkshops() {
        assertEquals("۶", agreement.toContractRow().rowLabel)
    }

    /** The raw codes travel on as typed: they become path segments, where Persian digits match nothing. */
    @Test
    fun identityStaysAscii() {
        val row = agreement.toContractRow()
        assertEquals("9028212822", row.workshopId)
        assertEquals("0210", row.branchCode)
    }

    @Test
    fun leanRowFillsTheFourBasicColumnsAndLeavesContactEmpty() {
        val row = WorkshopContractDN(
            contractRow = "3",
            startDate = "14030120",
            workshopId = "9007441260",
            branchCode = "0421",
            workshopName = "شرکت راه‌سازی البرز شرق",
        ).toContractRow()

        assertEquals("۳", row.rowLabel)
        assertEquals("۹۰۰۷۴۴۱۲۶۰", row.workshopCodeLabel)
        assertEquals("۱۴۰۳/۰۱/۲۰", row.commitmentDate)
        assertEquals("شرکت راه‌سازی البرز شرق", row.name)

        // Not dashed — absent. The card omits the whole contact block on this tab rather than
        // drawing three cells the service was never asked for.
        assertEquals("", row.mobile)
        assertEquals("", row.email)
        assertEquals("", row.address)
    }

    /**
     * A value the service omitted renders as the design's dash, never as a plausible stand-in.
     *
     * Address is the exception and stays blank, because its tile is dropped rather than dashed.
     */
    @Test
    fun omittedValuesDashExceptAddress() {
        val row = EmployerAgreementDN().toContractRow()

        assertTrue(row.rowLabel.isNotBlank())
        assertEquals(row.rowLabel, row.workshopCodeLabel)
        assertEquals(row.rowLabel, row.commitmentDate)
        assertEquals(row.rowLabel, row.mobile)
        assertEquals(row.rowLabel, row.email)
        assertEquals("", row.address)
    }
}
