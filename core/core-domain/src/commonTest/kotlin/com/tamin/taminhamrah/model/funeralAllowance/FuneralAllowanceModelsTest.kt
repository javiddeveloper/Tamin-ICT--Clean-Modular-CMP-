package com.tamin.taminhamrah.model.funeralAllowance

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Covers the pure domain helpers on the funeral-allowance models: [FuneralAllowanceInfoDN.toSubmitParams]
 * and [FuneralAllowanceInfoDN.fullName].
 *
 * The positional-array decoding that used to live here (`DeceasedValidationDN.fromRawList`) moved to
 * `DeceasedValidationDTO.fromPositional` in core-network — its edge cases are pinned in that module's
 * `DeceasedValidationDTOTest`, and the DTO→domain date formatting in core-data's
 * `FuneralAllowanceRepositoryImplTest`.
 */
class FuneralAllowanceModelsTest {

    @Test
    fun `toSubmitParams copies branch and insured fields and the entered deceased id`() {
        val info = sampleInfo()

        val params = info.toSubmitParams("0055667788")

        assertEquals("0055667788", params.deceasedNationalId)
        assertEquals("10", params.branchCode)
        assertEquals("شعبه مرکزی", params.branchName)
        assertEquals("علی", params.insuranceFirstName)
        assertEquals("رضایی", params.insuranceLastName)
        assertEquals("09121234567", params.mobileNumber)
        assertEquals("0012345678", params.nationalCode)
        assertEquals("1234567", params.insuranceNumber)
        assertEquals("07", params.requestHelpType)
    }

    @Test
    fun `toSubmitParams falls back to the default help type when the info help type is blank`() {
        val params = sampleInfo(requestHelpType = "  ").toSubmitParams("0055667788")

        assertEquals(FUNERAL_ALLOWANCE_HELP_TYPE, params.requestHelpType)
    }

    @Test
    fun `fullName joins first and last name and skips blank parts`() {
        assertEquals("علی رضایی", sampleInfo().fullName)
        assertEquals("رضایی", sampleInfo(firstName = "").fullName)
        assertEquals("علی", sampleInfo(lastName = "  ").fullName)
    }

    private fun sampleInfo(
        firstName: String = "علی",
        lastName: String = "رضایی",
        requestHelpType: String = "07",
    ) = FuneralAllowanceInfoDN(
        firstName = firstName,
        lastName = lastName,
        insuranceNumber = "1234567",
        bankAccount = "0203456789001",
        bankName = "بانک ملت",
        mobileNumber = "09121234567",
        branchName = "شعبه مرکزی",
        branchCode = "10",
        nationalCode = "0012345678",
        deceasedNationalId = "",
        requestHelpType = requestHelpType,
        hasBankAccountIssue = false,
        registeredRequest = null,
    )
}
