package com.tamin.taminhamrah.mapper.contracts

import com.tamin.taminhamrah.contractFlow.ContractEligibilityReason
import com.tamin.taminhamrah.model.contracts.ContractDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ContractEligibilityMapperTest {

    @Test
    fun `status 2 maps to age under 50 eligibility`() {
        val eligibility = listOf(contract(eligibilityStatus = "2")).resolveEligibility()

        assertTrue(eligibility.isEligible)
        assertEquals(2, eligibility.statusCode)
        assertEquals(ContractEligibilityReason.AGE_UNDER_FIFTY, eligibility.reason)
    }

    @Test
    fun `status 5 maps to ineligible`() {
        val eligibility = listOf(contract(eligibilityStatus = "5")).resolveEligibility()

        assertFalse(eligibility.isEligible)
        assertEquals(ContractEligibilityReason.AGE_HISTORY_NOT_MET, eligibility.reason)
    }

    @Test
    fun `status 3 builds dynamic history and age message`() {
        val eligibility = listOf(
            contract(
                eligibilityStatus = "3",
                history = "0",
                age = "360926",
            ),
        ).resolveEligibility()

        assertTrue(eligibility.isEligible)
        assertEquals(ContractEligibilityReason.HISTORY_AND_AGE_DYNAMIC, eligibility.reason)
        assertEquals("0", eligibility.historyDays)
        assertEquals("36/09/26", eligibility.ageFormatted)
    }

    @Test
    fun `empty contracts returns unavailable eligibility`() {
        val eligibility = emptyList<ContractDN>().resolveEligibility()

        assertFalse(eligibility.isEligible)
        assertEquals(-1, eligibility.statusCode)
    }

    @Test
    fun `contract without eligibilityStatus returns unavailable`() {
        val eligibility = listOf(contract(eligibilityStatus = null)).resolveEligibility()

        assertFalse(eligibility.isEligible)
        assertEquals(-1, eligibility.statusCode)
    }

    private fun contract(
        eligibilityStatus: String?,
        history: String? = null,
        age: String? = null,
    ): ContractDN = ContractDN(
        adultLetterDate = null,
        adultLetterNumber = null,
        age = age,
        branchCode = null,
        brchCodeNew = null,
        cancelDate = null,
        cancelUID = null,
        canceldesc = null,
        cityCode = null,
        cntDrmn = null,
        cntFreeJobCode = null,
        cntIncPayDate3t4 = null,
        cntMedicalFlag = null,
        comment = null,
        commissionStatus = null,
        confirmDate = null,
        confirmUID = null,
        contractDate = null,
        contractNumber = null,
        contractStatus = null,
        contractStatusObject = null,
        creatDate = null,
        createDate = null,
        createUID = null,
        eligibilityStatus = eligibilityStatus,
        freeJob = null,
        guid = null,
        guidName = null,
        history = history,
        insuranceId = null,
        isStudent = null,
        medicalExemptionStatus = null,
        militaryServiceLicense = null,
        mobileNumber = null,
        natinoalCode = null,
        physicalStatus = null,
        premiumRate = null,
        premiumRateCode = null,
        premiumType = null,
        premiumTypeCode = null,
        provinceCode = null,
        provinceName = null,
        refCode = null,
        salary = null,
        startDate = null,
        statusDate = null,
        wage = null,
    )
}
