package com.tamin.taminhamrah.apiService.contract

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.model.contracts.ContractDTO
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDTO
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractRequestDTO
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDTO
import com.tamin.taminhamrah.model.contracts.PremiumRateDTO
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.ContractsTestData
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ContractsApiServiceTest : BaseApiTest() {

    @Test
    fun `getContractList should return contract list`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = ContractsTestData.contractsListSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.create<ContractsApiService>()

        val response = apiService.getContractList(emptyMap())

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)

        val listData = response.data
        assertNotNull(listData)
        assertEquals(1, listData.total)

        val contracts: List<ContractDTO> = listData.list.orEmpty()
        assertEquals(1, contracts.size)

        val firstContract = contracts.first()
        assertEquals(478176974, firstContract.contractNumber)
        assertEquals(1780398668987L, firstContract.contractDate)
        assertEquals(362592593L, firstContract.salary)
        assertEquals("1", firstContract.cntDrmn)
        assertEquals("فعال بعلت تنظیم قرارداد", firstContract.contractStatusObject?.selfIsuContStatDesc)
        assertEquals(1, firstContract.contractStatusObject?.selfIsuContStatCode)
        assertEquals("اختیاری", firstContract.premiumType?.insuranceDescription)
        assertEquals("بیمه اختیاری ۲۷ درصد", firstContract.premiumRate?.spcrateDescription)
        assertEquals("تاسیساتی", firstContract.freeJob?.discrioption)
    }

    @Test
    fun `getSpcPremiumRates should return premium rate list`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = ContractsTestData.spcPremiumRateSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.create<ContractsApiService>()

        val response = apiService.getSpcPremiumRates()

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)

        val listData = response.data
        assertNotNull(listData)
        assertEquals(2, listData.total)

        val rates: List<PremiumRateDTO> = listData.list.orEmpty()
        assertEquals(2, rates.size)
        assertEquals("01", rates.first().spcrateCode)
        assertEquals("صاحبان حرف  ومشاغل ازاد12درصد", rates.first().spcrateDescription)
        assertEquals("12", rates.first().insurDpercent)
    }

    @Test
    fun `getFreelancePremiumRange should return low and high premium bounds`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = ContractsTestData.freelancePremiumRangeSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.create<ContractsApiService>()

        val response = apiService.getFreelancePremiumRange(
            treatmentSupportCode = "1",
            spcRateCode = "01",
            insuranceId = "099796",
        )

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)

        val data: FreelancePremiumRangeDTO = response.data!!
        assertEquals(0L, data.paymentTabayi)
        assertEquals(25_989_368L, data.lowPremium)
        assertEquals(538, data.history)
        assertEquals(139_654_620L, data.highPremium)
    }

    @Test
    fun `calculateFreelanceSalary should return calculated monthly salary`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = ContractsTestData.freelanceCalculateSalarySuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.create<ContractsApiService>()

        val response = apiService.calculateFreelanceSalary(
            monthlyPremium = 60_300_000L,
            treatmentSupportCode = "1",
            spcRateCode = "01",
        )

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        assertEquals(502_500_000L, response.data)
    }

    @Test
    fun `makeFreelanceContract should return contract number and date`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = ContractsTestData.freelanceMakeContractSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.create<ContractsApiService>()

        val response = apiService.makeFreelanceContract(
            monthlyPremium = 25_989_368L,
            request = FreelanceMakeContractRequestDTO(
                brchCodeNew = "0360",
                cityCode = "2442",
                cntDrmn = "1",
                cntFreeJobCode = "099796",
                guid = "00",
                guidName = "00",
                premiumRateCode = "01",
                provinceCode = "33",
            ),
        )

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)

        val data: FreelanceContractResultDTO = response.data!!
        assertEquals(478_176_975L, data.contractNumber)
        assertEquals(1_782_132_474_000L, data.contractDate)
    }
}
