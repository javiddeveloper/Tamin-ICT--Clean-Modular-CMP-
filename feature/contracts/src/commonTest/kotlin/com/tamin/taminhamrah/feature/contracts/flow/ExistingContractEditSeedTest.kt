package com.tamin.taminhamrah.feature.contracts.flow

import com.tamin.taminhamrah.contractFlow.ContractApplicantType
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExistingContractEditSeedTest {

    @Test
    fun `seeds freelance fields from list contract`() {
        val seed = seedExistingContractEdit(
            contract = sampleContract(
                salary = 25_000_000L,
                cntDrmn = "1",
                guid = "abc-guid",
                guidName = "مدرک",
            ),
            isOptionalInsurance = false,
        )

        assertEquals("33", seed.branchSelection.provinceCode)
        assertEquals("2442", seed.branchSelection.cityCode)
        assertEquals("0360", seed.branchSelection.branchCode)
        assertEquals("099796", seed.freeJobCode)
        assertEquals("تاسیساتی", seed.freeJobName)
        assertEquals("1", seed.treatmentSupportCode)
        assertTrue(seed.isTreatmentCommitmentConfirmed)
        assertEquals("01", seed.selectedPremiumRateCode)
        assertEquals(25_000_000L, seed.selectedMonthlyPremium)
        assertEquals(25_000_000L, seed.calculatedMonthlySalary)
        assertTrue(seed.isPremiumCalculated)
        assertEquals("abc-guid", seed.uploadedDocument?.imageId)
        assertEquals(ContractApplicantType.PERSONAL, seed.contractApplicantType)
    }

    @Test
    fun `optional seekbar premium is twenty seven percent of salary`() {
        val seed = seedExistingContractEdit(
            contract = sampleContract(salary = 100_000_000L, cntDrmn = "2"),
            isOptionalInsurance = true,
        )

        assertEquals(27_000_000L, seed.selectedMonthlyPremium)
        assertEquals(100_000_000L, seed.calculatedMonthlySalary)
        assertEquals("2", seed.treatmentSupportCode)
        assertFalse(seed.isTreatmentCommitmentConfirmed)
    }

    @Test
    fun `default guid is not treated as an uploaded document`() {
        val seed = seedExistingContractEdit(
            contract = sampleContract(guid = "00", guidName = "00"),
            isOptionalInsurance = false,
        )
        assertNull(seed.uploadedDocument)
    }

    private fun sampleContract(
        salary: Long? = 1L,
        cntDrmn: String? = "1",
        guid: String? = "00",
        guidName: String? = "00",
    ) = ContractDN(
        adultLetterDate = null,
        adultLetterNumber = null,
        age = null,
        branchCode = "0360",
        brchCodeNew = "0360",
        cancelDate = null,
        cancelUID = null,
        canceldesc = null,
        cityCode = "2442",
        cntDrmn = cntDrmn,
        cntFreeJobCode = "099796",
        cntIncPayDate3t4 = null,
        cntMedicalFlag = null,
        comment = null,
        commissionStatus = null,
        confirmDate = null,
        confirmUID = null,
        contractDate = null,
        contractNumber = 483222268,
        contractStatus = null,
        contractStatusObject = null,
        creatDate = null,
        createDate = null,
        createUID = null,
        eligibilityStatus = null,
        freeJob = FreeJobDN(
            discrioption = "تاسیساتی",
            endDate = null,
            fixRank = null,
            id = 1,
            iscoCode = null,
            jobCode = "099796",
            startDate = null,
            status = null,
        ),
        guid = guid,
        guidName = guidName,
        history = null,
        insuranceId = null,
        isStudent = null,
        medicalExemptionStatus = null,
        militaryServiceLicense = null,
        mobileNumber = null,
        natinoalCode = null,
        physicalStatus = null,
        premiumRate = PremiumRateDN(
            govermentPercent = null,
            insurDpercent = "27",
            payrespitelOne = null,
            payrespitelTwo = null,
            selfIsuTypeCode = "01",
            spcLowDayWage = null,
            spcrateCode = "01",
            spcrateDescription = null,
            status = null,
            statusStDate = null,
            treatmentPercap = null,
        ),
        premiumRateCode = "01",
        premiumType = null,
        premiumTypeCode = "01",
        provinceCode = "33",
        provinceName = "خراسان رضوی",
        refCode = null,
        salary = salary,
        startDate = null,
        statusDate = null,
        wage = null,
    )
}
