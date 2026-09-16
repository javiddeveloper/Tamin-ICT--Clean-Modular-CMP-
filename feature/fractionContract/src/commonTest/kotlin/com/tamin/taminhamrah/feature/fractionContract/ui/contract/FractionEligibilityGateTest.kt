package com.tamin.taminhamrah.feature.fractionContract.ui.contract

import com.tamin.taminhamrah.model.contractFlow.UserInfoFormPR
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityPR
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FractionEligibilityGateTest {

    private val notPrimary = "not-primary"
    private val under18 = "under-18"
    private val activeFraction = "active-fraction"
    private val unavailable = "unavailable"

    private fun gate(eligibility: FractionEligibilityPR?) = fractionEligibilityGateError(
        eligibility = eligibility,
        notPrimaryInsuredMessage = notPrimary,
        under18Message = under18,
        activeFractionMessage = activeFraction,
        unavailableMessage = unavailable,
    )

    private fun eligible(
        isInsurance: Boolean? = true,
        newAge: String = "250101",
        checkFractionMonthStatus: String = "1",
        insuranceTypeCode: String = "01",
    ) = FractionEligibilityPR(
        newAge = newAge,
        eligibilityStatus = 2,
        isInsurance = isInsurance,
        checkFractionMonthStatus = checkFractionMonthStatus,
        insuranceTypeCode = insuranceTypeCode,
    )

    @Test
    fun nullEligibility_returnsUnavailable() {
        assertEquals(unavailable, gate(null))
    }

    @Test
    fun isInsuranceNull_returnsNotPrimary() {
        assertEquals(notPrimary, gate(eligible(isInsurance = null)))
    }

    @Test
    fun isInsuranceFalse_returnsNotPrimary() {
        assertEquals(notPrimary, gate(eligible(isInsurance = false)))
    }

    @Test
    fun under18_returnsUnder18() {
        assertEquals(under18, gate(eligible(newAge = "170101")))
    }

    @Test
    fun shortNewAge_treatedAsUnder18() {
        assertEquals(under18, gate(eligible(newAge = "17")))
    }

    @Test
    fun checkFractionMonthStatusNotOne_returnsApiMessage() {
        assertEquals("blocked-by-api", gate(eligible(checkFractionMonthStatus = "blocked-by-api")))
    }

    @Test
    fun blankCheckFractionMonthStatus_returnsUnavailable() {
        assertEquals(unavailable, gate(eligible(checkFractionMonthStatus = "")))
    }

    @Test
    fun activeFractionType38_returnsActiveFraction() {
        assertEquals(
            activeFraction,
            gate(eligible(insuranceTypeCode = FractionContractState.PREMIUM_TYPE_CODE)),
        )
    }

    @Test
    fun fullyEligible_returnsNull() {
        assertNull(gate(eligible()))
    }

    @Test
    fun ageExactly18_isAllowed() {
        assertNull(gate(eligible(newAge = "180101")))
    }
}

class FractionUserInfoStepCompleteTest {

    private fun form(
        cityName: String = "تهران",
        address: String = "آدرس کامل",
        zipCode: String = "1414657771",
        phoneNumber: String = "02188974532",
    ) = UserInfoFormPR(
        cityName = cityName,
        address = address,
        zipCode = zipCode,
        phoneNumber = phoneNumber,
    )

    @Test
    fun completeForm_returnsTrue() {
        assertTrue(isUserInfoStepComplete(form()))
    }

    @Test
    fun blankCityName_returnsFalse() {
        assertFalse(isUserInfoStepComplete(form(cityName = "")))
    }

    @Test
    fun blankAddress_returnsFalse() {
        assertFalse(isUserInfoStepComplete(form(address = "")))
    }

    @Test
    fun shortZip_returnsFalse() {
        assertFalse(isUserInfoStepComplete(form(zipCode = "12345")))
    }

    @Test
    fun shortPhone_returnsFalse() {
        assertFalse(isUserInfoStepComplete(form(phoneNumber = "02188")))
    }

    @Test
    fun mobileNumberAsLandline_returnsFalse() {
        assertFalse(isUserInfoStepComplete(form(phoneNumber = "09123456789")))
    }

    @Test
    fun persianDigitsInZipAndPhone_stillComplete() {
        assertTrue(
            isUserInfoStepComplete(
                form(
                    zipCode = "۱۴۱۴۶۵۷۷۷۱",
                    phoneNumber = "۰۲۱۸۸۹۷۴۵۳۲",
                ),
            ),
        )
    }
}
