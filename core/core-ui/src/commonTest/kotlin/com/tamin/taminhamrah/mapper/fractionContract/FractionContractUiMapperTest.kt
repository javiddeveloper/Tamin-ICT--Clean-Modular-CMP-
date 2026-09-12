package com.tamin.taminhamrah.mapper.fractionContract

import com.tamin.taminhamrah.model.fractionContract.FractionContractResultDN
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityDN
import kotlin.test.Test
import kotlin.test.assertEquals

class FractionContractUiMapperTest {

    @Test
    fun eligibilityDn_toPresentation_mapsCorrectly() {
        val domain = FractionEligibilityDN(
            newAge = "250101",
            city = "تهران",
            provinceName = "تهران",
            provinceCode = "01",
            organizationAddress = "شعبه-۱",
            eligibilityStatus = 2,
            history = 120,
            isInsurance = true,
            checkFractionMonthStatus = "1",
            insuranceId = "123",
            insuranceTypeCode = "01",
            contractNumber = "555",
        )

        val presentation = domain.toPresentation()

        assertEquals("250101", presentation.newAge)
        assertEquals("تهران", presentation.city)
        assertEquals("تهران", presentation.provinceName)
        assertEquals("01", presentation.provinceCode)
        assertEquals("شعبه,۱", presentation.organizationAddress)
        assertEquals(2, presentation.eligibilityStatus)
        assertEquals(120, presentation.history)
        assertEquals(true, presentation.isInsurance)
        assertEquals("1", presentation.checkFractionMonthStatus)
        assertEquals("123", presentation.insuranceId)
        assertEquals("01", presentation.insuranceTypeCode)
        assertEquals("555", presentation.contractNumber)
        assertEquals("تهران, تهران, شعبه,۱", presentation.branchAddress)
    }

    @Test
    fun eligibilityDn_toPresentation_nullsBecomeDefaults() {
        val presentation = FractionEligibilityDN().toPresentation()

        assertEquals("", presentation.newAge)
        assertEquals(-1, presentation.eligibilityStatus)
        assertEquals(false, presentation.isInsurance)
        assertEquals("", presentation.branchAddress)
    }

    @Test
    fun contractResultDn_toPresentation_mapsCorrectly() {
        val presentation = FractionContractResultDN(
            contractNumber = 987L,
            contractDate = 1710000000000L,
        ).toPresentation()

        assertEquals("987", presentation.contractNumber)
        assertEquals(1710000000000L, presentation.contractDate)
    }

    @Test
    fun contractResultDn_toPresentation_nullsBecomeDefaults() {
        val presentation = FractionContractResultDN().toPresentation()

        assertEquals("", presentation.contractNumber)
        assertEquals(0L, presentation.contractDate)
    }
}
