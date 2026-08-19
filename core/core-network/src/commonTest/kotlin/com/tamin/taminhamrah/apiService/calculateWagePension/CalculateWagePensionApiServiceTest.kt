package com.tamin.taminhamrah.apiService.calculateWagePension

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.util.ApiTestUtils
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CalculateWagePensionApiServiceTest : BaseApiTest() {

    @Test
    fun `getPersonalInfo should return organization and insurance ids`() = runTest {
        val ktorfit = createMockKtorfit(
            ApiTestUtils.createJsonResponse(CalculateWagePensionTestData.personalInfoSuccess)
        )
        val apiService = ktorfit.createCalculateWagePensionApiService()

        val response = apiService.getPersonalInfo()

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertEquals("12345", response.data?.organizationId)
        assertEquals("9876543210", response.data?.insuranceId)
        assertEquals("Tehran Main", response.data?.branch)
    }

    @Test
    fun `isMultipleWorkshops should return result 1`() = runTest {
        val ktorfit = createMockKtorfit(
            ApiTestUtils.createJsonResponse(CalculateWagePensionTestData.multipleWorkshopsYes)
        )
        val apiService = ktorfit.createCalculateWagePensionApiService()

        val response = apiService.isMultipleWorkshops("12345", "9876543210")

        assertEquals(200, response.status)
        assertEquals(1, response.data?.result)
    }

    @Test
    fun `isMultipleWorkshops should return result 0`() = runTest {
        val ktorfit = createMockKtorfit(
            ApiTestUtils.createJsonResponse(CalculateWagePensionTestData.multipleWorkshopsNo)
        )
        val apiService = ktorfit.createCalculateWagePensionApiService()

        val response = apiService.isMultipleWorkshops("12345", "9876543210")

        assertEquals(0, response.data?.result)
    }

    @Test
    fun `calculateMultipleWorkshops should return pension amount`() = runTest {
        val ktorfit = createMockKtorfit(
            ApiTestUtils.createJsonResponse(CalculateWagePensionTestData.multipleWorkshopsCalc)
        )
        val apiService = ktorfit.createCalculateWagePensionApiService()

        val response = apiService.calculateMultipleWorkshops("12345", "9876543210")

        assertEquals(200, response.status)
        assertEquals(25_000_000, response.data?.result)
    }
}

object CalculateWagePensionTestData {
    val personalInfoSuccess = """
        {
            "organizationId": "12345",
            "insuranceId": "9876543210",
            "branch": "Tehran Main"
        }
    """.trimIndent()

    val multipleWorkshopsYes = """
        { "result": 1 }
    """.trimIndent()

    val multipleWorkshopsNo = """
        { "result": 0 }
    """.trimIndent()

    val multipleWorkshopsCalc = """
        { "result": 25000000 }
    """.trimIndent()
}
