package com.tamin.taminhamrah.feature.contracts.flow.preflight

import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.ContractStatusCode
import com.tamin.taminhamrah.model.contracts.ContractStatusObjectDN
import com.tamin.taminhamrah.model.contracts.PremiumTypeDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import kotlin.test.Test

import kotlin.test.assertEquals
import kotlin.test.assertNull

class ContractPreflightGateTest {

    @Test
    fun `returns NOT_REGISTERED when insurance id is invalid`() {
        val block = resolvePreflightBlock(
            registration = sampleRegistration(insuranceIdValid = false),
            typedContracts = emptyList(),
            allContracts = emptyList(),
            currentPremiumTypeCode = "01",
        )

        assertEquals(ContractPreflightBlock.NOT_REGISTERED, block)
    }

    @Test
    fun `returns ACTIVE_CONTRACT when typed contract is active`() {
        val block = resolvePreflightBlock(
            registration = sampleRegistration(),
            typedContracts = listOf(contractWithStatus(ContractStatusCode.ACTIVE)),
            allContracts = emptyList(),
            currentPremiumTypeCode = "01",
        )

        assertEquals(ContractPreflightBlock.ACTIVE_CONTRACT, block)
    }

    @Test
    fun `returns UNDER_AGE when applicant is younger than 18`() {
        val block = resolvePreflightBlock(
            registration = sampleRegistration(dateOfBirthEpoch = RECENT_BIRTH_EPOCH),
            typedContracts = emptyList(),
            allContracts = emptyList(),
            currentPremiumTypeCode = "01",
        )

        assertEquals(ContractPreflightBlock.UNDER_AGE, block)
    }

    @Test
    fun `returns null when all preflight checks pass`() {
        val block = resolvePreflightBlock(
            registration = sampleRegistration(dateOfBirthEpoch = ADULT_BIRTH_EPOCH),
            typedContracts = emptyList(),
            allContracts = emptyList(),
            currentPremiumTypeCode = "01",
        )

        assertNull(block)
    }

    @Test
    fun `returns CANCELLED_OVER_20_DAYS when typed contract has status 4`() {
        val block = resolvePreflightBlock(
            registration = sampleRegistration(),
            typedContracts = listOf(contractWithStatus(ContractStatusCode.CANCELLED_OVER_20_DAYS)),
            allContracts = emptyList(),
            currentPremiumTypeCode = "01",
        )

        assertEquals(ContractPreflightBlock.CANCELLED_OVER_20_DAYS, block)
    }

    @Test
    fun `returns CANCELLED_OVER_3_MONTHS when typed contract has status 5`() {
        val block = resolvePreflightBlock(
            registration = sampleRegistration(),
            typedContracts = listOf(contractWithStatus(ContractStatusCode.CANCELLED_OVER_3_MONTHS)),
            allContracts = emptyList(),
            currentPremiumTypeCode = "01",
        )

        assertEquals(ContractPreflightBlock.CANCELLED_OVER_3_MONTHS, block)
    }

    @Test
    fun `returns OTHER_ACTIVE_CONTRACT when another premium type is active`() {
        val block = resolvePreflightBlock(
            registration = sampleRegistration(),
            typedContracts = emptyList(),
            allContracts = listOf(
                contractWithStatus(
                    statusCode = ContractStatusCode.ACTIVE,
                    premiumTypeCode = "02",
                ),
            ),
            currentPremiumTypeCode = "01",
        )

        assertEquals(ContractPreflightBlock.OTHER_ACTIVE_CONTRACT, block)
    }

    private fun sampleRegistration(
        insuranceIdValid: Boolean = true,
        dateOfBirthEpoch: Long? = null,
    ) = RegistrationInfoPR(
        fullName = "Test User",
        nationalId = "1234567890",
        birthDateFormatted = "1370/01/01",
        insuranceId = "12345678901",
        genderCode = RegistrationInfoPR.FEMALE_GENDER_CODE,
        address = "Tehran",
        zipCode = "1234567890",
        phoneNumber = "02112345678",
        mobileNumber = "09121234567",
        hasMobile = true,
        insuranceIdValid = insuranceIdValid,
        dateOfBirthEpoch = dateOfBirthEpoch,
    )

    private fun contractWithStatus(
        statusCode: Int,
        premiumTypeCode: String = "01",
    ) = ContractDN(
        adultLetterDate = null,
        adultLetterNumber = null,
        age = null,
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
        contractNumber = 1,
        contractStatus = null,
        contractStatusObject = ContractStatusObjectDN(
            selfIsuContStatDesc = "active",
            selfIsuContStatCode = statusCode,
        ),
        creatDate = null,
        createDate = null,
        createUID = null,
        eligibilityStatus = null,
        freeJob = null,
        guid = null,
        guidName = null,
        history = null,
        insuranceId = null,
        isStudent = null,
        medicalExemptionStatus = null,
        militaryServiceLicense = null,
        mobileNumber = null,
        natinoalCode = null,
        physicalStatus = null,
        premiumRate = null,
        premiumRateCode = null,
        premiumType = PremiumTypeDN(
            insuranceDescription = "type",
            insuranceKind = "type",
            insuranceTypeCode = premiumTypeCode,
            status = null,
            statusDate = null,
        ),
        premiumTypeCode = premiumTypeCode,
        provinceCode = null,
        provinceName = null,
        refCode = null,
        salary = null,
        startDate = null,
        statusDate = null,
        wage = null,
    )

    private companion object {
        // 2024-01-01 UTC — recent enough to be under 18 when the test runs
        const val RECENT_BIRTH_EPOCH = 1_704_067_200_000L
        // 1980-01-01 UTC
        const val ADULT_BIRTH_EPOCH = 315_532_800_000L
    }
}
