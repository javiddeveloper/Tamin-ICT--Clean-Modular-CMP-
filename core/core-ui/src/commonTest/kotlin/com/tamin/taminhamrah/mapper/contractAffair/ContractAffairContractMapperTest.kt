package com.tamin.taminhamrah.mapper.contractAffair

import com.tamin.taminhamrah.model.contractAffair.ContractDN
import com.tamin.taminhamrah.model.contractAffair.ContractPaymentHistoryItemDN
import com.tamin.taminhamrah.model.contractAffair.ContractStateDN
import com.tamin.taminhamrah.model.contractAffair.ContractStatusObjectDN
import com.tamin.taminhamrah.model.contractAffair.FreeJobDN
import com.tamin.taminhamrah.model.contractAffair.PremiumRateDN
import com.tamin.taminhamrah.model.contractAffair.PremiumTypeDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ContractAffairContractMapperTest {

    @Test
    fun `contract toPresentation prefers the status object and derives the active flag`() {
        val pr = contractDN(
            contractNumber = 987654,
            contractStatusObject = ContractStatusObjectDN(
                selfIsuContStatDesc = "قرارداد فعال",
                selfIsuContStatCode = 1,
            ),
            premiumType = PremiumTypeDN(
                insuranceDescription = "حرف و مشاغل آزاد",
                insuranceKind = null,
                insuranceTypeCode = "01",
                status = null,
                statusDate = null,
            ),
            premiumTypeCode = "01",
            premiumRate = PremiumRateDN(
                govermentPercent = null,
                insurDpercent = "27",
                payrespitelOne = null,
                payrespitelTwo = null,
                selfIsuTypeCode = null,
                spcLowDayWage = null,
                spcrateCode = null,
                spcrateDescription = "۱۸ درصد بازنشستگی و فوت",
                status = null,
                statusStDate = null,
                treatmentPercap = null,
            ),
            salary = 100_000_000,
            freeJob = freeJobDN(discrioption = "رانندهٔ تاکسی"),
            cntDrmn = "1",
            cntFreeJobCode = "123456",
        ).toPresentation()

        assertEquals("987654", pr.contractNumber)
        assertEquals("قرارداد فعال", pr.statusDesc)
        assertTrue(pr.isActive)
        assertEquals(1, pr.statusCode)
        assertEquals("حرف و مشاغل آزاد", pr.insuranceType)
        assertEquals("01", pr.premiumTypeCode)
        assertEquals("۱۸ درصد بازنشستگی و فوت", pr.monthlyPremiumLabel)
        assertEquals("100000000", pr.monthlyIncome)
        assertEquals("رانندهٔ تاکسی", pr.jobTitle)
        assertEquals("123456", pr.freeJobCode)
        assertTrue(pr.hasTreatmentSupport)
        assertEquals("حمایت درمان دارد", pr.treatmentSupportText)
        assertTrue(pr.premiumRatePercentLabel.endsWith("درصد"))
        assertNull(pr.deferredDebtLabel)
    }

    @Test
    fun `contract toPresentation marks an annulled contract inactive and drops treatment support`() {
        val pr = contractDN(
            contractStatus = "قرارداد ابطال شده",
            cntDrmn = "2",
            premiumTypeCode = "02",
        ).toPresentation()

        assertEquals("قرارداد ابطال شده", pr.statusDesc)
        assertFalse(pr.isActive)
        assertFalse(pr.hasTreatmentSupport)
        assertEquals("حمایت درمان ندارد", pr.treatmentSupportText)
        // no premiumType/insuranceKind → falls back to the raw code
        assertEquals("02", pr.insuranceType)
    }

    @Test
    fun `contract list toPresentation maps every row`() {
        val list = listOf(
            contractDN(contractNumber = 1, premiumTypeCode = "01"),
            contractDN(contractNumber = 2, premiumTypeCode = "02"),
        ).toPresentation()

        assertEquals(listOf("1", "2"), list.map { it.contractNumber })
    }

    @Test
    fun `contract state toPresentation drops rows without a code`() {
        val states = listOf(
            ContractStateDN(code = 5, description = "ابطال به درخواست بیمه‌شده"),
            ContractStateDN(code = null, description = "بدون کد"),
        ).toPresentation()

        assertEquals(1, states.size)
        assertEquals(5, states.first().code)
        assertEquals("ابطال به درخواست بیمه‌شده", states.first().title)
    }

    @Test
    fun `contract state toPresentation returns null for a null code and empty title for a null description`() {
        assertNull(ContractStateDN(code = null, description = "x").toPresentation())
        assertEquals("", ContractStateDN(code = 9, description = null).toPresentation()?.title)
    }

    @Test
    fun `payment history toPresentation reads paid state from the status text`() {
        val paid = paymentHistoryItem(statusContract = "پرداخت شده").toPresentation()
        val unpaid = paymentHistoryItem(statusContract = "پرداخت نشده").toPresentation()

        assertTrue(paid.isPaid)
        assertEquals("پرداخت شده", paid.statusLabel)
        assertFalse(unpaid.isPaid)
        assertEquals("پرداخت نشده", unpaid.statusLabel)
    }

    @Test
    fun `payment history toPresentation falls back to amount and date when the status is blank`() {
        val withPayment = paymentHistoryItem(
            statusContract = null,
            amountPayment = 53_866_782.0,
            datePayment = "14050610",
        ).toPresentation()
        val withoutPayment = paymentHistoryItem(
            statusContract = null,
            amountPayment = 0.0,
            datePayment = null,
        ).toPresentation()

        assertTrue(withPayment.isPaid)
        assertEquals("53866782", withPayment.amountPayment)
        assertEquals("پرداخت شده", withPayment.statusLabel)
        assertFalse(withoutPayment.isPaid)
        assertEquals("پرداخت نشده", withoutPayment.statusLabel)
    }
}

// --- fixtures -------------------------------------------------------------------------------------

private fun contractDN(
    contractNumber: Int? = null,
    contractStatus: String? = null,
    contractStatusObject: ContractStatusObjectDN? = null,
    premiumType: PremiumTypeDN? = null,
    premiumTypeCode: String? = null,
    premiumRate: PremiumRateDN? = null,
    salary: Long? = null,
    freeJob: FreeJobDN? = null,
    cntDrmn: String? = null,
    cntFreeJobCode: String? = null,
): ContractDN = ContractDN(
    adultLetterDate = null,
    adultLetterNumber = null,
    age = null,
    branchCode = null,
    brchCodeNew = null,
    cancelDate = null,
    cancelUID = null,
    canceldesc = null,
    cityCode = null,
    cntDrmn = cntDrmn,
    cntFreeJobCode = cntFreeJobCode,
    cntIncPayDate3t4 = null,
    cntMedicalFlag = null,
    comment = null,
    commissionStatus = null,
    confirmDate = null,
    confirmUID = null,
    contractDate = null,
    contractNumber = contractNumber,
    contractStatus = contractStatus,
    contractStatusObject = contractStatusObject,
    creatDate = null,
    createDate = null,
    createUID = null,
    eligibilityStatus = null,
    freeJob = freeJob,
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
    premiumRate = premiumRate,
    premiumRateCode = null,
    premiumType = premiumType,
    premiumTypeCode = premiumTypeCode,
    provinceCode = null,
    provinceName = null,
    refCode = null,
    salary = salary,
    startDate = null,
    statusDate = null,
    wage = null,
)

private fun freeJobDN(discrioption: String?): FreeJobDN = FreeJobDN(
    discrioption = discrioption,
    endDate = null,
    fixRank = null,
    id = null,
    iscoCode = null,
    jobCode = null,
    startDate = null,
    status = null,
)

private fun paymentHistoryItem(
    statusContract: String?,
    amountPayment: Double? = 10_000.0,
    datePayment: String? = "14050610",
): ContractPaymentHistoryItemDN = ContractPaymentHistoryItemDN(
    nationalId = "0012345678",
    insuranceId = "77",
    debtNumber = "9001",
    startTermPayment = "140501",
    endTermPayment = "140506",
    totalDebt = 53_866_782.0,
    paymentDeadline = "14051015",
    amountPayment = amountPayment,
    datePayment = datePayment,
    statusContract = statusContract,
    statusRecipient = "وصول شده",
)
