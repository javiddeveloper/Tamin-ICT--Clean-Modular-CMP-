package com.tamin.taminhamrah.model.funeralAllowance

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * [DeceasedValidationDN.fromRawList] parses the positional string array the backend returns from
 * `shortterm/validateFuneral/{nationalCode}` (native `DeceasedInfoResponse`). The index map and the
 * "at least 8 entries and data[6] == \"1\"" eligibility rule are legacy behaviour, so each edge is
 * pinned here. [FuneralAllowanceInfoDN.toSubmitParams] and [fullName] are also covered.
 */
class FuneralAllowanceModelsTest {

    private fun rawList(
        name: String = "زهرا رضایی",
        relationship: String = "همسر",
        eligibleFlag: String = "1",
        message: String = "دارای شرایط می‌باشید",
        dependentStatus: String = "همسر",
        deathDate: String = "14050110",
    ): List<String?> = listOf(
        "0", "1", "2", "3",       // [0..3] unused
        name,                     // [4]
        relationship,             // [5]
        eligibleFlag,             // [6]
        message,                  // [7]
        "8",                      // [8] unused
        dependentStatus,          // [9]
        "10", "11", "12",         // [10..12] unused
        deathDate,                // [13]
    )

    @Test
    fun `fromRawList maps the positional fields the app uses`() {
        val result = DeceasedValidationDN.fromRawList(rawList())

        assertEquals("زهرا رضایی", result.deceasedFullName)
        assertEquals("همسر", result.relationship)
        assertEquals("دارای شرایط می‌باشید", result.message)
        assertEquals("همسر", result.dependentStatus)
        assertTrue(result.isEligible)
    }

    @Test
    fun `fromRawList formats an 8-digit death date as yyyy slash MM slash dd`() {
        val result = DeceasedValidationDN.fromRawList(rawList(deathDate = "14050110"))

        assertEquals("1405/01/10", result.deathDate)
    }

    @Test
    fun `fromRawList leaves a non 8-digit death date untouched`() {
        val result = DeceasedValidationDN.fromRawList(rawList(deathDate = "1405/01/10"))

        assertEquals("1405/01/10", result.deathDate)
    }

    @Test
    fun `fromRawList treats a non-1 eligibility flag as not eligible`() {
        assertFalse(DeceasedValidationDN.fromRawList(rawList(eligibleFlag = "0")).isEligible)
        assertFalse(DeceasedValidationDN.fromRawList(rawList(eligibleFlag = "")).isEligible)
    }

    @Test
    fun `fromRawList treats a short list as not eligible and falls back to empty strings`() {
        val result = DeceasedValidationDN.fromRawList(listOf("a", "b", "c"))

        assertFalse(result.isEligible)
        assertEquals("", result.deceasedFullName)
        assertEquals("", result.relationship)
        assertEquals("", result.deathDate)
    }

    @Test
    fun `fromRawList tolerates null entries`() {
        val list = rawList().toMutableList().also { it[4] = null; it[5] = null }
        val result = DeceasedValidationDN.fromRawList(list)

        assertEquals("", result.deceasedFullName)
        assertEquals("", result.relationship)
    }

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
