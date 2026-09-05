package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.HistoryTestData
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class HistoryApiServicesTest : BaseApiTest() {

    @Test
    fun `getDastmozdInfos should return successful response with data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = HistoryTestData.dastmozdInfosSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHistoryApiServices()

        val parameters = mapOf("param1" to "value1")
        val response = apiService.getDastmozdInfos(parameters)

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        assertNotNull(response.data)
        assertEquals(1, response.data?.total)
        assertEquals(1, response.data?.list?.size)
        assertEquals("1000", response.data?.list?.first()?.hiswage1)
        assertEquals("1402", response.data?.list?.first()?.hisyear)
    }

    @Test
    fun `getDastmozdInfos should return empty list when no data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = HistoryTestData.dastmozdInfosEmpty
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHistoryApiServices()

        val parameters = mapOf("param1" to "value1")
        val response = apiService.getDastmozdInfos(parameters)

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        assertNotNull(response.data)
        assertEquals(0, response.data?.total)
        assertEquals(0, response.data?.list?.size)
    }

    @Test
    fun `getTalfighInfos should return successful response with data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = HistoryTestData.talfighInfosSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHistoryApiServices()

        val parameters = mapOf("param1" to "value1")
        val response = apiService.getTalfighInfos(parameters)

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        assertNotNull(response.data)
        assertEquals(2, response.data?.total)
        assertEquals(2, response.data?.list?.size)
        assertEquals("30", response.data?.list?.first()?.hisMonth1)
        assertEquals(10, response.data?.list?.first()?.historyYears)
    }

    @Test
    fun `getTalfighInfos should return empty list when no data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = HistoryTestData.talfighInfosEmpty
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHistoryApiServices()

        val parameters = mapOf("param1" to "value1")
        val response = apiService.getTalfighInfos(parameters)

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        assertNotNull(response.data)
        assertEquals(0, response.data?.total)
        assertEquals(0, response.data?.list?.size)
    }

    @Test
    fun `getHistoryJobInfos should return successful response with data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = HistoryTestData.historyJobInfosSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHistoryApiServices()

        val parameters = mapOf("param1" to "value1")
        val response = apiService.getHistoryJobInfos(parameters)

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        assertNotNull(response.data)
        assertEquals(1, response.data?.total)
        assertEquals(1, response.data?.list?.size)
        assertEquals("0081631829", response.data?.list?.first()?.risuid)
        assertEquals("کارمند اداری ۱", response.data?.list?.first()?.jobDesc)
        assertEquals("139810", response.data?.list?.first()?.startDate)
        assertEquals("6393610019", response.data?.list?.first()?.rwshId)
    }

    @Test
    fun `getHistoryJobInfos should return empty list when no data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = HistoryTestData.historyJobInfosEmpty
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHistoryApiServices()

        val parameters = mapOf("param1" to "value1")
        val response = apiService.getHistoryJobInfos(parameters)

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        assertNotNull(response.data)
        assertEquals(0, response.data?.total)
        assertEquals(0, response.data?.list?.size)
    }

    @Test
    fun `getUserInfos should return successful response with data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = HistoryTestData.userInfoSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHistoryApiServices()

        val response = apiService.getUserInfos()

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        assertNotNull(response.data)
        assertEquals("عادل", response.data?.firstName)
        assertEquals("حسين پناهي", response.data?.lastName)
        assertEquals("2062144681", response.data?.socialSecurityNumber)
        assertEquals("0082984639", response.data?.insuranceNumber)
        assertEquals("1361/10/01", response.data?.birthDate)
        assertEquals("5589743451", response.data?.nationalID)
        assertEquals("2782294052", response.data?.id)
        assertNull(response.data?.militaryServiceCode)
        assertNull(response.data?.marriageCode)
    }

    @Test
    fun `sendToInstitution should return successful response with null data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = HistoryTestData.sendToInstitutionSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHistoryApiServices()

        val response = apiService.sendToInstitution(allHistorySelected = true, historyAndWageSelected = true, combineHistorySelected = true)

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        assertNull(response.data)
    }
}
