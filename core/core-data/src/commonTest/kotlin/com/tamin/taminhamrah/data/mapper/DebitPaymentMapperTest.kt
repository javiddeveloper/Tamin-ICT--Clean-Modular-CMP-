package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.workshop.DebitPaymentRequestDN
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopDTO
import com.tamin.taminhamrah.model.workshop.WorkshopCharacterDTO
import com.tamin.taminhamrah.model.workshop.WorkshopLegalDTO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Domain → wire for `pay-normal-debit`.
 *
 * The endpoint takes the workshop's character as `nationalType` and, for a حقوقی workshop only,
 * its national id as `nationalId`. That choice is made here rather than in the domain model,
 * which carries both facts raw — so this is the one place the rule can be checked.
 */
class DebitPaymentMapperTest {

    private fun request(characterCode: String, legalNationalId: String) = DebitPaymentRequestDN(
        workshopId = "9028218513",
        branchCode = "6310",
        debitNumber = "6310030089235",
        agreementRow = "",
        characterCode = characterCode,
        legalNationalId = legalNationalId,
    ).toDto()

    @Test
    fun `a legal workshop sends its national id`() {
        val dto = request(characterCode = "02", legalNationalId = "10101234567")

        assertEquals("10101234567", dto.nationalId)
        assertEquals("02", dto.nationalType)
    }

    @Test
    fun `a natural person has no national id of its own`() {
        val dto = request(characterCode = "01", legalNationalId = "")

        assertNull(dto.nationalId)
        assertEquals("01", dto.nationalType)
    }

    /**
     * A حقیقی workshop that somehow arrived carrying a legal id must still send none — the web
     * client keys the decision on the character code alone, not on whether an id is present.
     */
    @Test
    fun `the character code decides, not the presence of an id`() {
        val dto = request(characterCode = "01", legalNationalId = "10101234567")

        assertNull(dto.nationalId)
    }

    /** Blank is not a national id: it would reach the wire as `""` instead of `null`. */
    @Test
    fun `a legal workshop with no id recorded sends null rather than blank`() {
        val dto = request(characterCode = "02", legalNationalId = "")

        assertNull(dto.nationalId)
    }

    @Test
    fun `the workshop's legal national id is read off the wire`() {
        val domain = EmployerWorkshopDTO(
            workshopId = "9028218513",
            branchCode = "6310",
            character = WorkshopCharacterDTO(characterCode = "02", characterDesc = "حقوقی"),
            legalWorkshop = WorkshopLegalDTO(nationalId = "10101234567"),
        ).toDomain()

        assertEquals("02", domain.characterCode)
        assertEquals("10101234567", domain.legalNationalId)
    }

    @Test
    fun `an absent legalWorkshop object becomes a blank id, not a crash`() {
        val domain = EmployerWorkshopDTO(
            workshopId = "9028218513",
            branchCode = "6310",
            character = WorkshopCharacterDTO(characterCode = "01", characterDesc = "حقیقی"),
        ).toDomain()

        assertEquals("", domain.legalNationalId)
    }
}
