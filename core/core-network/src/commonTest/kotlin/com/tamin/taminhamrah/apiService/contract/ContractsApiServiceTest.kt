package com.tamin.taminhamrah.apiService.contract

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.model.contracts.ContractDTO
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDTO
import com.tamin.taminhamrah.model.contracts.PremiumRateDTO
import com.tamin.taminhamrah.model.contracts.SaveContactPersonalDTO
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDTO
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
        val apiService = ktorfit.createContractsApiService()

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

        assertNotNull(firstContract.contractStatusObject)
        assertEquals(1, firstContract.contractStatusObject?.selfIsuContStatCode)

        assertNotNull(firstContract.premiumType)
        assertEquals("02", firstContract.premiumType?.insuranceTypeCode)

        assertNotNull(firstContract.premiumRate)
        assertEquals("27", firstContract.premiumRate?.insurDpercent)

        assertNotNull(firstContract.freeJob)
    }

    @Test
    fun `getSpcPremiumRates should return premium rate list`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = ContractsTestData.spcPremiumRateSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createContractsApiService()

        val response = apiService.getSpcPremiumRates()

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)

        val listData = response.data
        assertNotNull(listData)
        assertEquals(2, listData.total)

        val rates: List<PremiumRateDTO> = listData.list.orEmpty()
        assertEquals(2, rates.size)

        val firstRate = rates.first()
        assertEquals("01", firstRate.spcrateCode)
        assertEquals("12", firstRate.insurDpercent)

        val secondRate = rates[1]
        assertEquals("02", secondRate.spcrateCode)
        assertEquals("14", secondRate.insurDpercent)
    }

    @Test
    fun `getFreelancePremiumRange should return low and high premium bounds`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = ContractsTestData.freelancePremiumRangeSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createContractsApiService()

        val response = apiService.getFreelancePremiumRange(
            treatmentSupportCode = "1",
            spcRateCode = "01",
            freeJobCode = "099796",
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
        val apiService = ktorfit.createContractsApiService()

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
    fun `saveContact should return successful response with null data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(dataJson = "null")

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createContractsApiService()

        val response = apiService.saveContact(
            SaveContactRequestDTO(
                address = "اينجا56564544545",
                mobile = "",
                personal = SaveContactPersonalDTO(ssn = "2487741923"),
                phoneNumber = "02126555891",
                zipCode = "4915784967",
            ),
        )

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
    }

}
