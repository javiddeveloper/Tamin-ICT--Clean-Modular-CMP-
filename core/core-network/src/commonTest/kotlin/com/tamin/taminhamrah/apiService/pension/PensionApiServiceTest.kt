package com.tamin.taminhamrah.apiService.pension

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.PensionTestData
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PensionApiServiceTest : BaseApiTest() {

    @Test
    fun `getPensionerPayRoll should return payroll data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PensionTestData.payrollSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPensionApiService()

        val response = apiService.getPensionerPayRoll("filter-json")

        assertEquals(200, response.status)
        assertEquals(1, response.data?.id)
        assertEquals("Type A", response.data?.clpType)
        assertEquals(5000000L, response.data?.sumAmount)
        assertEquals("1402", response.data?.hisYear)
    }

    @Test
    fun `getPensionerPayRoll should return error status`() = runTest {
        val family = "SERVER_ERROR"
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = "{}",
            status = 500,
            family = family,
            reason = "Internal Server Error"
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.create<PensionApiService>()

        val response = apiService.getPensionerPayRoll("filter-json")

        assertEquals(500, response.status)
        assertEquals(family, response.family)
    }

    @Test
    fun `getDisabilityPersonalInfo should return personal info data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PensionTestData.disabilityPersonalInfoSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPensionApiService()

        val response = apiService.getDisabilityPersonalInfo()

        assertEquals(200, response.status)
        assertEquals("Ali", response.data?.personal?.firstName)
        assertEquals("Alavi", response.data?.personal?.lastName)
        assertEquals("0012345678", response.data?.personal?.nationalId)
        assertEquals("09121234567", response.data?.mobileNumber)
    }

    @Test
    fun `getUserAge should return age data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PensionTestData.userAgeSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPensionApiService()

        val response = apiService.getUserAge(emptyMap())

        assertEquals(200, response.status)
        assertEquals("30", response.data?.age)
        assertEquals("1370/01/01", response.data?.birthDate)
    }
}
