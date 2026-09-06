package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.workshop.EmployerAgreementByWorkshopDTO
import com.tamin.taminhamrah.model.workshop.EmployerAgreementSubmissionDN
import com.tamin.taminhamrah.model.workshop.EmployerCommitmentInfoDTO
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopDTO
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowDTO
import com.tamin.taminhamrah.model.workshop.WorkshopOrganizationDTO
import com.tamin.taminhamrah.model.workshop.WorkshopWithoutContractDTO
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Wire → domain for خدمات غیرحضوری کارفرما.
 *
 * The mappers exist to absorb the server's quirks before anything above core-data sees them: every
 * absent string collapses to `""` here (once, not `?: ""` per screen), a missing nested `workshop`
 * becomes an empty [com.tamin.taminhamrah.model.workshop.WorkshopSummaryDN] rather than a null, and
 * the lower-case wire keys (`emailaddr`, `mobileno`, `wokshopId`) are read into their proper domain
 * names. A rename on any of those paths fails here rather than as a blank card at runtime.
 */
class EmployerOnlineServicesMapperTest {

    // ----------------------------------------------- step 2 — employer identity block

    @Test
    fun `commitment info maps current mobile and email onto the contact block`() {
        val dto = EmployerCommitmentInfoDTO(
            firstName = "رضا",
            lastName = "کارفرما",
            nationalCode = "0012345678",
            mobile = "09120000000",
            email = "boss@example.com",
        )

        val dn = dto.toDomain()

        assertEquals("رضا", dn.firstName)
        assertEquals("کارفرما", dn.lastName)
        assertEquals("رضا کارفرما", dn.fullName)
        assertEquals("0012345678", dn.nationalCode)
        // `mobile`/`email` on the wire are the *currently registered* values, kept apart from the
        // newly requested ones the wizard collects.
        assertEquals("09120000000", dn.currentMobile)
        assertEquals("boss@example.com", dn.currentEmail)
    }

    @Test
    fun `commitment info with everything omitted maps to blanks, not nulls`() {
        val dn = EmployerCommitmentInfoDTO().toDomain()

        assertEquals("", dn.firstName)
        assertEquals("", dn.nationalCode)
        assertEquals("", dn.currentMobile)
        assertEquals("", dn.currentEmail)
    }

    // ----------------------------------------------- step 2 — workshops without a contract

    @Test
    fun `workshop-without-contract reads the wokshopId typo and the nested branch office`() {
        val dto = WorkshopWithoutContractDTO(
            workshopName = "کارگاه تولیدی الف",
            nationalId = "10861234567",
            // Server typo kept verbatim — the key is `wokshopId`, not `workshopId`.
            workshopId = "1071410004",
            branchCode = "123",
            postalCode = "1234567890",
            tel = "02112345678",
            address = "تهران",
            organization = WorkshopOrganizationDTO(organizationName = "شعبه یک تهران", code = "0960"),
        )

        val dn = dto.toDomain()

        assertEquals("1071410004", dn.workshopId)
        assertEquals("123", dn.branchCode)
        assertEquals("کارگاه تولیدی الف", dn.name)
        assertEquals("10861234567", dn.nationalId)
        assertEquals("تهران", dn.address)
        assertEquals("شعبه یک تهران", dn.branchOfficeName)
        assertEquals("0960", dn.branchOfficeCode)
        // Both identity halves present -> the row can be drilled into.
        assertEquals(true, dn.hasIdentity)
    }

    @Test
    fun `workshop-without-contract without an organization block keeps the branch office blank`() {
        val dn = WorkshopWithoutContractDTO(workshopId = "1", branchCode = "2").toDomain()

        assertEquals("", dn.branchOfficeName)
        assertEquals("", dn.branchOfficeCode)
        assertEquals("", dn.address)
    }

    // ----------------------------------------------- contract / پیمانکار rows

    @Test
    fun `contract row maps mobileNo and folds the nested workshop into a summary`() {
        val dto = WorkshopContractRowDTO(
            contractRow = "02100014",
            startDate = "14030101",
            endDate = "14041230",
            firstName = "علی",
            lastName = "پیمانکار",
            mobile = "09123334444",
            email = "ali@example.com",
            nationalCode = "0021234567",
            tel = "02133334444",
            postalCode = "1111111111",
            workshop = EmployerWorkshopDTO(workshopId = "1071410004", workshopName = "کارگاه الف"),
        )

        val dn = dto.toDomain()

        assertEquals("02100014", dn.contractRow)
        assertEquals("14030101", dn.startDate)
        assertEquals("علی پیمانکار", dn.fullName)
        assertEquals("09123334444", dn.mobile)
        assertEquals("0021234567", dn.nationalCode)
        assertEquals("1071410004", dn.workshop.workshopId)
        assertEquals("کارگاه الف", dn.workshop.name)
    }

    @Test
    fun `contract row with no nested workshop still produces an empty summary, not a null`() {
        val dn = WorkshopContractRowDTO(contractRow = "1").toDomain()

        assertEquals("", dn.workshop.workshopId)
        assertEquals("", dn.workshop.name)
    }

    // ----------------------------------------------- management side — agreements of a workshop

    @Test
    fun `agreement-by-workshop reads the lower-case wire keys`() {
        val dto = EmployerAgreementByWorkshopDTO(
            paymentSequence = "7",
            startDate = "14030101",
            commitmentDate = "14030102",
            email = "boss@example.com",
            mobile = "09120000000",
            workshop = EmployerWorkshopDTO(workshopId = "1071410004", branchCode = "123", workshopName = "کارگاه الف"),
        )

        val dn = dto.toDomain()

        assertEquals("7", dn.paymentSequence)
        assertEquals("14030101", dn.startDate)
        assertEquals("14030102", dn.commitmentDate)
        assertEquals("boss@example.com", dn.email)
        assertEquals("09120000000", dn.mobile)
        assertEquals("1071410004", dn.workshop.workshopId)
        assertEquals("123", dn.workshop.branchCode)
    }

    // ----------------------------------------------- step 3 — submission body

    @Test
    fun `submission maps straight onto the request body, mobile keyed as mobileNo on the wire`() {
        val dto = EmployerAgreementSubmissionDN(
            mobile = "09121234567",
            email = "boss@example.com",
            ticketCode = "654321",
        ).toDto()

        assertEquals("09121234567", dto.mobile)
        assertEquals("boss@example.com", dto.email)
        assertEquals("654321", dto.ticketCode)
    }
}
