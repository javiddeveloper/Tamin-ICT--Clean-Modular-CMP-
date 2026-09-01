package com.tamin.taminhamrah.mapper.employerInfo

import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.WorkshopSummaryDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EmployerInfoMapperTest {

    private val karaj = WorkshopSummaryDN(
        workshopId = "0968210170",
        // Identity half two, which the request carries.
        branchCode = "0960",
        name = "آموزشگاه کامپیوتر توکلی",
        // The office number the card labels کد شعبه.
        branchOfficeCode = "0960",
        branchOfficeName = "شعبه کرج",
        characterCode = "01",
        address = "کرج، میدان شهدا",
    )

    private val mashhad = WorkshopSummaryDN(
        workshopId = "0081631829",
        branchCode = "1202",
        name = "شرکت صنایع دما بخار",
        branchOfficeCode = "1202",
        branchOfficeName = "شعبه ۲ مشهد",
        characterCode = "02",
    )

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

    /**
     * The service sends one entry per agreement, so the same workshop arrives repeatedly. Rows
     * identical in every field the card shows are one row to a reader.
     *
     * The merged model carries no agreement sequence, so the surviving row is keyed by its
     * position — which is all the list needs, and all that is left to key on.
     */
    @Test
    fun toWorkshopItemPRs_deduplicatesAgreementsWithIdenticalWorkshopContent() {
        val agreement = EmployerAgreementDN(
            commitmentDate = "14030519",
            email = "tavakoli@email.com",
            mobile = "09120000000",
            workshop = karaj,
        )

        val result = listOf(agreement, agreement, agreement).toWorkshopItemPRs()

        assertEquals(1, result.size)
        val item = result.first()
        assertEquals("0968210170-0960-0", item.id)
        assertEquals("آموزشگاه کامپیوتر توکلی", item.name)
        assertEquals("0968210170", item.code)
        assertEquals("0960", item.bcode)
        assertEquals("شعبه کرج", item.branch)
        assertEquals("شعبه کرج – 0960", item.branchLabel)
        assertEquals("1403/05/19", item.letDate)
        assertFalse(item.isLegal)
        assertEquals("tavakoli@email.com", item.email)
        assertEquals("09120000000", item.mobile)
        assertEquals("کرج، میدان شهدا", item.address)
    }

    @Test
    fun toWorkshopItemPRs_preservesDistinctWorkshopsAndKeepsTheirIdsApart() {
        val first = EmployerAgreementDN(workshop = karaj)
        val second = EmployerAgreementDN(workshop = mashhad)

        val result = listOf(first, second, first).toWorkshopItemPRs()

        assertEquals(2, result.size)
        assertEquals(2, result.map { it.id }.toSet().size)
        assertEquals("آموزشگاه کامپیوتر توکلی", result[0].name)
        assertFalse(result[0].isLegal)
        assertEquals("شرکت صنایع دما بخار", result[1].name)
        assertTrue(result[1].isLegal)
    }

    /** Same workshop, different commitment dates: two real rows, and two distinct keys. */
    @Test
    fun toWorkshopItemPRs_preservesAgreementsThatDifferInTheirDetails() {
        val first = EmployerAgreementDN(commitmentDate = "14020101", workshop = karaj)
        val second = EmployerAgreementDN(commitmentDate = "14030101", workshop = karaj)

        val result = listOf(first, second).toWorkshopItemPRs()

        assertEquals(2, result.size)
        assertEquals(2, result.map { it.id }.toSet().size)
        assertEquals("1402/01/01", result[0].letDate)
        assertEquals("1403/01/01", result[1].letDate)
    }

    /** `"02"` is حقوقی — the only kind whose identity details may be completed. */
    @Test
    fun onlyCharacterCodeZeroTwoIsTreatedAsALegalPerson() {
        assertTrue(EmployerAgreementDN(workshop = mashhad).toWorkshopItemPR().isLegal)
        assertFalse(EmployerAgreementDN(workshop = karaj).toWorkshopItemPR().isLegal)
        assertFalse(
            EmployerAgreementDN(workshop = karaj.copy(characterCode = "")).toWorkshopItemPR().isLegal,
        )
    }
}
