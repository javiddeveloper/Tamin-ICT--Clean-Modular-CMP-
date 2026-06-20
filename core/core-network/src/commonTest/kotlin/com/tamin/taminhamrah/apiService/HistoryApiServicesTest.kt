package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.util.ApiTestUtils
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HistoryApiServicesTest : BaseApiTest() {

    @Test
    fun `getDastmozdInfos should return successful response with data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = HistoryTestData.dastmozdInfosSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.create<HistoryApiServices>()

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
        val apiService = ktorfit.create<HistoryApiServices>()

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
        val apiService = ktorfit.create<HistoryApiServices>()

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
        val apiService = ktorfit.create<HistoryApiServices>()

        val parameters = mapOf("param1" to "value1")
        val response = apiService.getTalfighInfos(parameters)

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        assertNotNull(response.data)
        assertEquals(0, response.data?.total)
        assertEquals(0, response.data?.list?.size)
    }
}

object HistoryTestData {
    val dastmozdInfosSuccess = """
        {
            "list": [
                {
                    "hiswage1": "1000",
                    "hisyear": "1402",
                    "id": 1,
                    "risuid": "123456"
                }
            ],
            "total": 1
        }
    """.trimIndent()

    val dastmozdInfosEmpty = """
        {
            "list": [],
            "total": 0
        }
    """.trimIndent()

    val talfighInfosSuccess = """
        {
            "list": [
                {
                    "hisMonth1": "30",
                    "historyYears": 10,
                    "id": 1,
                    "hisYear": "1402"
                },
                {
                    "hisMonth1": "31",
                    "historyYears": 5,
                    "id": 2,
                    "hisYear": "1401"
                }
            ],
            "total": 2
        }
    """.trimIndent()

    val talfighInfosEmpty = """
        {
            "list": [],
            "total": 0
        }
    """.trimIndent()
}
