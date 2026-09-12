package com.tamin.taminhamrah.apiService.fractionContract

import com.tamin.taminhamrah.apiService.BaseApiTest
import de.jensklingenberg.ktorfit.Ktorfit
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FractionContractApiServiceTest : BaseApiTest() {

    @Test
    fun checkAgeAndHistory_returnsEligibility() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": {
                    "isInsurance": true,
                    "checkFractionMonthStatus": "1",
                    "eligibilityStatus": 2,
                    "newAge": "250101",
                    "history": 120,
                    "city": "تهران",
                    "provinceCode": "تهران",
                    "provinceName": "01",
                    "organizationId": "شعبه-۱",
                    "contract": {
                        "insuranceId": "1234567890",
                        "branchCode": "0101",
                        "premiumType": { "insuranceTypeCode": "01" }
                    }
                }
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createFractionContractApiService()

        val response = apiService.checkAgeAndHistory()

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertEquals(true, response.data?.isInsurance)
        assertEquals("1", response.data?.checkFractionMonthStatus)
        assertEquals(2, response.data?.eligibilityStatus)
        assertEquals("تهران", response.data?.provinceName)
        assertEquals("01", response.data?.provinceCode)
        assertEquals("شعبه-۱", response.data?.organizationAddress)
        assertEquals("01", response.data?.contract?.premiumType?.insuranceTypeCode)
    }

    @Test
    fun checkAgeAndHistory_nullData_isAllowed() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": null
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createFractionContractApiService()

        val response = apiService.checkAgeAndHistory()

        assertEquals(200, response.status)
        assertNull(response.data)
    }

    @Test
    fun makeFractionContract_returnsResult() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": {
                    "contractNumber": 987654321,
                    "contractDate": 1710000000000
                }
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createFractionContractApiService()

        val response = apiService.makeFractionContract(
            com.tamin.taminhamrah.model.fractionContract.MakeFractionContractRequestDTO()
        )

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertEquals(987654321L, response.data?.contractNumber)
        assertEquals(1710000000000L, response.data?.contractDate)
        assertTrue(response.data?.contractNumber != null)
    }
}
