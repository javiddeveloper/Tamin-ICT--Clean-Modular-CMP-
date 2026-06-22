package com.tamin.taminhamrah.mapper.contracts

import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.ContractStatusObjectDN
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import com.tamin.taminhamrah.model.contracts.PremiumTypeDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ContractMapperTest {

    @Test
    fun `toPresentation should map active contract correctly`() {
        val dn = activeContract()

        val pr = dn.toPresentation()

        assertEquals("478176974", pr.contractNumber)
        assertEquals("فعال بعلت تنظیم قرارداد", pr.statusDesc)
        assertTrue(pr.isActive)
        assertEquals("اختیاری", pr.insuranceType)
        assertEquals("بیمه اختیاری ۲۷ درصد", pr.monthlyPremiumLabel)
        assertEquals("362592593", pr.monthlyIncome)
        assertTrue(pr.hasTreatmentSupport)
        assertEquals("حمایت درمان دارد", pr.treatmentSupportText)
        assertEquals("تاسیساتی", pr.jobTitle)
        assertTrue(pr.requestDate.isNotEmpty())
    }

    @Test
    fun `toPresentation should map cancelled contract correctly`() {
        val dn = activeContract().copy(
            contractStatusObject = ContractStatusObjectDN(
                selfIsuContStatDesc = "ابطال به دلیل سپری شدن مهلت قانونی",
                selfIsuContStatCode = 2,
            ),
            cntDrmn = "2",
        )

        val pr = dn.toPresentation()

        assertFalse(pr.isActive)
        assertFalse(pr.hasTreatmentSupport)
        assertEquals("حمایت درمان ندارد", pr.treatmentSupportText)
    }

    @Test
    fun `toPresentation should handle null nested values`() {
        val dn = ContractDN(
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
            contractNumber = null,
            contractStatus = "فعال",
            contractStatusObject = null,
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

        val pr = dn.toPresentation()

        assertEquals("", pr.contractNumber)
        assertEquals("فعال", pr.statusDesc)
        assertEquals("", pr.insuranceType)
        assertEquals("", pr.monthlyPremiumLabel)
        assertEquals("", pr.monthlyIncome)
        assertTrue(pr.hasTreatmentSupport)
        assertEquals("", pr.jobTitle)
        assertEquals("", pr.requestDate)
    }

    private fun activeContract() = ContractDN(
        adultLetterDate = null,
        adultLetterNumber = null,
        age = null,
        branchCode = null,
        brchCodeNew = null,
        cancelDate = null,
        cancelUID = null,
        canceldesc = null,
        cityCode = null,
        cntDrmn = "1",
        cntFreeJobCode = null,
        cntIncPayDate3t4 = null,
        cntMedicalFlag = null,
        comment = null,
        commissionStatus = null,
        confirmDate = null,
        confirmUID = null,
        contractDate = 1780398668987L,
        contractNumber = 478176974,
        contractStatus = null,
        contractStatusObject = ContractStatusObjectDN(
            selfIsuContStatDesc = "فعال بعلت تنظیم قرارداد",
            selfIsuContStatCode = 1,
        ),
        creatDate = null,
        createDate = null,
        createUID = null,
        eligibilityStatus = null,
        freeJob = FreeJobDN(
            discrioption = "تاسیساتی",
            endDate = null,
            fixRank = null,
            id = null,
            iscoCode = null,
            jobCode = null,
            startDate = null,
            status = null,
        ),
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
        premiumRate = PremiumRateDN(
            govermentPercent = null,
            insurDpercent = "27",
            payrespitelOne = null,
            payrespitelTwo = null,
            selfIsuTypeCode = null,
            spcLowDayWage = null,
            spcrateCode = null,
            spcrateDescription = "بیمه اختیاری ۲۷ درصد",
            status = null,
            statusStDate = null,
            treatmentPercap = null,
        ),
        premiumRateCode = null,
        premiumType = PremiumTypeDN(
            insuranceDescription = "اختیاری",
            insuranceKind = "اختیاری",
            insuranceTypeCode = "02",
            status = null,
            statusDate = null,
        ),
        premiumTypeCode = null,
        provinceCode = null,
        provinceName = null,
        refCode = null,
        salary = 362592593L,
        startDate = null,
        statusDate = null,
        wage = null,
    )
}
