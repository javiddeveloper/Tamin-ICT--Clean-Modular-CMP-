package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.CommonTestData
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CommonApiServiceTest : BaseApiTest() {

    @Test
    fun `getJobTitle should return job title list`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = CommonTestData.jobTitleSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createCommonApiService()

        val response = apiService.getJobTitle(emptyMap())

        assertEquals(200, response.status)
        val list = response.data?.list.orEmpty()
        assertEquals(1, list.size)
        assertEquals("123", list.first().jobCode)
    }

    @Test
    fun `getRegistrationDeclarationForm should return HttpStatement`() = runTest {
        val ktorfit = createMockKtorfit(ApiTestUtils.createJsonResponse(dataJson = ""))
        val apiService = ktorfit.createCommonApiService()

        val response = apiService.getRegistrationDeclarationForm()
        assertNotNull(response)
    }

    @Test
    fun `checkInsuredInfo should return the pensioner-detection list`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = """{ "total": 2, "list": ["05", "این سرویس برای شما فعال نیست"], "typeUser": null }"""
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createCommonApiService()

        val response = apiService.checkInsuredInfo()

        assertEquals(200, response.status)
        assertEquals(listOf("05", "این سرویس برای شما فعال نیست"), response.data?.list)
    }
}
