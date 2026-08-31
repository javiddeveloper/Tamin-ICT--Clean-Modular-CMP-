package com.tamin.taminhamrah.feature.taminServices.funeralAllowance

import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.model.FuneralAllowanceInfoPR
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.model.toPR
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.model.toSubmitParams
import com.tamin.taminhamrah.model.funeralAllowance.DeceasedValidationDN
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceInfoDN
import com.tamin.taminhamrah.model.funeralAllowance.RegisteredFuneralRequestDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Domain -> presentation mapping for the funeral-allowance feature. */
class FuneralAllowanceMapperTest {

    @Test
    fun infoDn_toPR_copiesFieldsAndDerivesFullName() {
        val pr = sampleInfoDN().toPR()

        assertEquals("علی رضایی", pr.fullName)
        assertEquals("علی", pr.firstName)
        assertEquals("رضایی", pr.lastName)
        assertEquals("1234567", pr.insuranceNumber)
        assertEquals("10", pr.branchCode)
        assertEquals("0012345678", pr.nationalCode)
        assertEquals("07", pr.requestHelpType)
        assertEquals(false, pr.hasBankAccountIssue)
        assertNull(pr.registeredRequest)
    }

    @Test
    fun infoDn_toPR_mapsRegisteredRequestAndFormatsUnknownDatesAsEmpty() {
        val pr = sampleInfoDN().copy(
            hasBankAccountIssue = true,
            registeredRequest = RegisteredFuneralRequestDN(
                requestId = 998877L,
                deceasedNationalId = "0055667788",
                deathTimestamp = null,
                requestTimestamp = null,
                statusName = "در انتظار اصلاح حساب",
            ),
        ).toPR()

        val registered = requireNotNull(pr.registeredRequest)
        assertEquals(998877L, registered.requestId)
        assertEquals("0055667788", registered.deceasedNationalId)
        assertEquals("در انتظار اصلاح حساب", registered.statusName)
        // formatTimestamp(null) -> "" (RegisteredFuneralRequestPR keeps "" for unknown).
        assertEquals("", registered.deathDate)
        assertEquals("", registered.requestDate)
    }

    @Test
    fun deceasedValidationDn_toPR_copiesEveryField() {
        val pr = DeceasedValidationDN(
            deceasedFullName = "زهرا رضایی",
            relationship = "همسر",
            isEligible = true,
            message = "دارای شرایط می‌باشید",
            dependentStatus = "همسر",
            deathDate = "۱۴۰۵/۰۱/۱۰",
        ).toPR()

        assertEquals("زهرا رضایی", pr.deceasedFullName)
        assertEquals("همسر", pr.relationship)
        assertEquals(true, pr.isEligible)
        assertEquals("دارای شرایط می‌باشید", pr.message)
        assertEquals("همسر", pr.dependentStatus)
        assertEquals("۱۴۰۵/۰۱/۱۰", pr.deathDate)
    }

    @Test
    fun infoPr_toSubmitParams_usesEnteredDeceasedIdAndCopiesInsuredFields() {
        val params = sampleInfoPR().toSubmitParams("0055667788")

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
    fun infoPr_toSubmitParams_blankHelpTypeFallsBackToDefault() {
        val params = sampleInfoPR().copy(requestHelpType = "").toSubmitParams("0055667788")

        assertEquals("07", params.requestHelpType)
    }

    private fun sampleInfoDN() = FuneralAllowanceInfoDN(
        firstName = "علی",
        lastName = "رضایی",
        insuranceNumber = "1234567",
        bankAccount = "0203456789001",
        bankName = "بانک ملت",
        mobileNumber = "09121234567",
        branchName = "شعبه مرکزی",
        branchCode = "10",
        nationalCode = "0012345678",
        deceasedNationalId = "",
        requestHelpType = "07",
        hasBankAccountIssue = false,
        registeredRequest = null,
    )

    private fun sampleInfoPR() = FuneralAllowanceInfoPR(
        fullName = "علی رضایی",
        firstName = "علی",
        lastName = "رضایی",
        insuranceNumber = "1234567",
        bankAccount = "0203456789001",
        bankName = "بانک ملت",
        mobileNumber = "09121234567",
        branchName = "شعبه مرکزی",
        branchCode = "10",
        nationalCode = "0012345678",
        deceasedNationalId = "",
        requestHelpType = "07",
        hasBankAccountIssue = false,
        registeredRequest = null,
    )
}
