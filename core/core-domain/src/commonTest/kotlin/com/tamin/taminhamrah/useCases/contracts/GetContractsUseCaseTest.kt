package com.tamin.taminhamrah.useCases.contracts

import app.cash.turbine.test
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.ContractStatusObjectDN
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import com.tamin.taminhamrah.model.contracts.PremiumTypeDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.contracts.FakeContractsRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetContractsUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: GetContractsUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = GetContractsUseCase(repository)
    }

    @Test
    fun `invoke should return contracts from repository`() = runTest {
        val expectedList = listOf(sampleContract())
        repository.contractsResult = expectedList

        useCase().test {
            val result = awaitItem()
            assertEquals(expectedList, result)
            awaitComplete()
        }

        assertEquals(null, repository.lastQuery)
    }

    @Test
    fun `invoke should pass query to repository`() = runTest {
        val query = ApiQueryParamDN(page = 1, start = 0, limit = 10)
        repository.contractsResult = emptyList()

        useCase(query).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(query, repository.lastQuery)
    }

    @Test
    fun `studentInsuranceContracts should delegate to repository`() = runTest {
        val expectedList = listOf(sampleContract())
        repository.contractsResult = expectedList

        useCase.studentInsuranceContracts().test {
            assertEquals(expectedList, awaitItem())
            awaitComplete()
        }

        assertEquals(true, repository.studentInsuranceContractsCalled)
        assertEquals(null, repository.lastQuery)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }

    private fun sampleContract() = ContractDN(
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
