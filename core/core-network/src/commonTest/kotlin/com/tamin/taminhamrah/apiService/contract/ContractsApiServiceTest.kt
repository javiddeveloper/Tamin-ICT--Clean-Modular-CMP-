package com.tamin.taminhamrah.apiService.contract

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.model.contracts.ContractDTO
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
}
