package com.tamin.taminhamrah.mapper.workshop

import com.tamin.taminhamrah.model.workshop.EmployerAgreementByWorkshopDN
import com.tamin.taminhamrah.model.workshop.EmployerContactInfoDN
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowDN
import com.tamin.taminhamrah.model.workshop.WorkshopSummaryDN
import com.tamin.taminhamrah.model.workshop.WorkshopWithoutContractDN
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Domain → presentation for خدمات غیرحضوری کارفرما.
 *
 * The mapping edge is where every label becomes final: Latin digits turn Persian, compact Jalali
 * (`14030101`) gains its `/` separators, and anything the service left blank becomes the design's
 * dash. Identity fields that travel on to the next screen / into query parameters
 * ([WorkshopWithoutContractPR.workshopId] / `branchCode`) are the deliberate exception — they stay
 * raw. These tests pin both halves of that contract.
 */
class EmployerOnlineServicesUiMapperTest {

    @Test
    fun `contact info dashes blanks and Persian-digits the numeric fields`() {
        val pr = EmployerContactInfoDN(
            firstName = "رضا",
            lastName = "کارفرما",
            nationalCode = "0012345678",
            currentMobile = "09120000000",
            currentEmail = "",
        ).toPresentation()

        assertEquals("رضا کارفرما", pr.fullName)
        assertEquals("۰۰۱۲۳۴۵۶۷۸", pr.nationalCode)
        assertEquals("۰۹۱۲۰۰۰۰۰۰۰", pr.currentMobile)
        // Email is text, not digits — a blank one is the dash, never Persian-converted.
        assertEquals("-", pr.currentEmail)
    }

    @Test
    fun `workshop-without-contract keeps identity raw but formats the code label`() {
        val pr = WorkshopWithoutContractDN(
            workshopId = "1071410004",
            branchCode = "123",
            name = "کارگاه تولیدی الف",
            nationalId = "10861234567",
            postalCode = "1234567890",
            tel = "02112345678",
            address = "تهران",
            branchOfficeName = "شعبه یک تهران",
        ).toPresentation()

        // Raw — these become path segments / query params on the drill-down.
        assertEquals("1071410004", pr.workshopId)
        assertEquals("123", pr.branchCode)
        assertEquals(true, pr.hasIdentity)
        // Display — same number, Persian digits, under شماره کارگاه.
        assertEquals("۱۰۷۱۴۱۰۰۰۴", pr.codeLabel)
        assertEquals("کارگاه تولیدی الف", pr.name)
        assertEquals("۱۰۸۶۱۲۳۴۵۶۷", pr.nationalId)
        assertEquals("شعبه یک تهران", pr.branchOfficeName)
    }

    @Test
    fun `workshop-without-contract dashes every field the service omitted`() {
        val pr = WorkshopWithoutContractDN(workshopId = "1", branchCode = "2").toPresentation()

        assertEquals("-", pr.name)
        assertEquals("-", pr.nationalId)
        assertEquals("-", pr.postalCode)
        assertEquals("-", pr.tel)
        assertEquals("-", pr.address)
        assertEquals("-", pr.branchOfficeName)
    }

    @Test
    fun `contract row separates compact Jalali dates and joins the name`() {
        val pr = WorkshopContractRowDN(
            contractRow = "02100014",
            startDate = "14030101",
            endDate = "14041230",
            firstName = "علی",
            lastName = "پیمانکار",
            mobile = "09123334444",
            email = "ali@example.com",
            nationalCode = "0021234567",
            workshop = WorkshopSummaryDN(workshopId = "1071410004", name = "کارگاه الف"),
        ).toPresentation()

        assertEquals("۰۲۱۰۰۰۱۴", pr.contractRow)
        assertEquals("علی پیمانکار", pr.fullName)
        assertEquals("۱۴۰۳/۰۱/۰۱", pr.startDate)
        assertEquals("۱۴۰۴/۱۲/۳۰", pr.endDate)
        assertEquals("۰۹۱۲۳۳۳۴۴۴۴", pr.mobile)
        assertEquals("ali@example.com", pr.email)
        assertEquals("کارگاه الف", pr.workshopName)
        assertEquals("۱۰۷۱۴۱۰۰۰۴", pr.workshopCodeLabel)
    }

    @Test
    fun `agreement-by-workshop pulls identity from the nested workshop`() {
        val pr = EmployerAgreementByWorkshopDN(
            paymentSequence = "7",
            startDate = "14030101",
            commitmentDate = "14030102",
            email = "boss@example.com",
            mobile = "09120000000",
            workshop = WorkshopSummaryDN(
                workshopId = "1071410004",
                branchCode = "123",
                name = "کارگاه الف",
                address = "تهران",
            ),
        ).toPresentation()

        assertEquals("1071410004", pr.workshopId)
        assertEquals("123", pr.branchCode)
        assertEquals("۷", pr.paymentSequence)
        assertEquals("کارگاه الف", pr.workshopName)
        assertEquals("۱۰۷۱۴۱۰۰۰۴", pr.workshopCodeLabel)
        assertEquals("تهران", pr.address)
        assertEquals("۱۴۰۳/۰۱/۰۱", pr.startDate)
        assertEquals("۱۴۰۳/۰۱/۰۲", pr.commitmentDate)
        assertEquals("۰۹۱۲۰۰۰۰۰۰۰", pr.mobile)
        assertEquals("boss@example.com", pr.email)
    }
}
