package com.tamin.taminhamrah.mapper.employerInfo

import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EmployerInfoMapperTest {

    @Test
    fun toPresentation_mapsLegalWorkshopCorrectly() {
        val dn = LegalWorkshopDN(name = "شرکت پتروشیمی", nationalCode = "10101234567")
        val pr = dn.toPresentation()

        assertEquals("شرکت پتروشیمی", pr.name)
        assertEquals("10101234567", pr.nationalCode)
    }

    @Test
    fun toPresentation_mapsLegalWorkshopCeoCorrectly() {
        val dn = LegalWorkshopCeoDN(firstName = "علی", lastName = "محمدی")
        val pr = dn.toPresentation()

        assertEquals("علی", pr.firstName)
        assertEquals("محمدی", pr.lastName)
        assertEquals("علی محمدی", pr.fullName)
    }

    @Test
    fun toWorkshopItemPRs_deduplicatesDuplicateWorkshopContentEvenWithDifferentPymseq() {
        val workshop = EmployerWorkshopDN(
            workshopId = "0968210170",
            branchCode = "0960",
            branchTitle = "شعبه کرج",
            workshopName = "آموزشگاه کامپیوتر توکلی",
            characterCode = "01",
            lastAddress = "کرج، میدان شهدا",
        )
        // API sends agreements that have different agreement sequences (pymseq) or timestamps
        // but have identical workshop content
        val agreement1 = EmployerAgreementDN(
            pymseq = "1001",
            emailaddr = "tavakoli@email.com",
            mobileno = "09120000000",
            letDate = "14030519",
            workshop = workshop,
        )
        val agreement2 = EmployerAgreementDN(
            pymseq = "1002",
            emailaddr = "tavakoli@email.com",
            mobileno = "09120000000",
            letDate = "14030519",
            workshop = workshop,
        )
        val agreement3 = EmployerAgreementDN(
            pymseq = "1003",
            emailaddr = "tavakoli@email.com",
            mobileno = "09120000000",
            letDate = "14030519",
            workshop = workshop,
        )

        val result = listOf(agreement1, agreement2, agreement3).toWorkshopItemPRs()

        // Only 1 item should be shown because everything in the workshop object content is the same
        assertEquals(1, result.size)
        val item = result.first()
        assertEquals("1001", item.id)
        assertEquals("آموزشگاه کامپیوتر توکلی", item.name)
        assertEquals("0968210170", item.code)
        assertEquals("0960", item.bcode)
        assertEquals("شعبه کرج", item.branch)
        assertEquals("شعبه کرج – 0960", item.branchLabel)
        assertFalse(item.isLegal)
        assertEquals("tavakoli@email.com", item.email)
        assertEquals("09120000000", item.mobile)
        assertEquals("کرج، میدان شهدا", item.address)
    }

    @Test
    fun toWorkshopItemPRs_preservesDistinctAgreements() {
        val workshop1 = EmployerWorkshopDN(
            workshopId = "0968210170",
            branchCode = "0960",
            branchTitle = "شعبه کرج",
            workshopName = "آموزشگاه کامپیوتر توکلی",
            characterCode = "01",
        )
        val workshop2 = EmployerWorkshopDN(
            workshopId = "0081631829",
            branchCode = "1202",
            branchTitle = "شعبه ۲ مشهد",
            workshopName = "شرکت صنایع دما بخار",
            characterCode = "02",
        )
        val agreement1 = EmployerAgreementDN(pymseq = "1001", workshop = workshop1)
        val agreement2 = EmployerAgreementDN(pymseq = "1002", workshop = workshop2)

        val result = listOf(agreement1, agreement2, agreement1).toWorkshopItemPRs()

        assertEquals(2, result.size)
        assertEquals("1001", result[0].id)
        assertEquals("آموزشگاه کامپیوتر توکلی", result[0].name)
        assertFalse(result[0].isLegal)

        assertEquals("1002", result[1].id)
        assertEquals("شرکت صنایع دما بخار", result[1].name)
        assertTrue(result[1].isLegal)
    }

    @Test
    fun toWorkshopItemPRs_preservesAgreementsWithDifferentDetails() {
        val workshop = EmployerWorkshopDN(
            workshopId = "0968210170",
            branchCode = "0960",
            branchTitle = "شعبه کرج",
            workshopName = "آموزشگاه کامپیوتر توکلی",
            characterCode = "01",
        )
        // Two agreements for same workshop but different letDate
        val agreement1 = EmployerAgreementDN(pymseq = "1001", letDate = "14020101", workshop = workshop)
        val agreement2 = EmployerAgreementDN(pymseq = "1002", letDate = "14030101", workshop = workshop)

        val result = listOf(agreement1, agreement2).toWorkshopItemPRs()

        assertEquals(2, result.size)
        assertEquals("1402/01/01", result[0].letDate)
        assertEquals("1403/01/01", result[1].letDate)
    }
}