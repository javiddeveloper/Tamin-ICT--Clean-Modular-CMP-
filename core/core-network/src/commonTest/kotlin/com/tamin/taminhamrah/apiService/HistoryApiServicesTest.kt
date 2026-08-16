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
}

object HistoryTestData {
    val dastmozdInfosSuccess = """
        {
            "list": [
                {
                    "hismon1": null,
                    "hismon2": null,
                    "hismon3": null,
                    "hismon4": null,
                    "hismon5": null,
                    "hismon6": null,
                    "hismon7": null,
                    "hismon8": null,
                    "hismon9": null,
                    "hismon10": null,
                    "hismon11": null,
                    "hismon12": null,
                    "hiswage1": "1000",
                    "hiswage2": null,
                    "hiswage3": null,
                    "hiswage4": null,
                    "hiswage5": null,
                    "hiswage6": null,
                    "hiswage7": null,
                    "hiswage8": null,
                    "hiswage9": null,
                    "hiswage10": null,
                    "hiswage11": null,
                    "hiswage12": null,
                    "hisyear": "1402",
                    "id": 1,
                    "risufname": null,
                    "risubirthdate": null,
                    "risuidserial2": null,
                    "risuidserial1": null,
                    "rwshname": null,
                    "expcitycode": null,
                    "brhcode": null,
                    "risuidno": null,
                    "risudname": null,
                    "risuid": "123456",
                    "risulname": null,
                    "risunatcode": null,
                    "brhname": null,
                    "historytypedesc": null,
                    "rwshid": null
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
                    "id": 1,
                    "hisMonth1": "30",
                    "hisMonth2": null,
                    "hisMonth3": null,
                    "hisMonth4": null,
                    "hisMonth5": null,
                    "hisMonth6": null,
                    "hisMonth7": null,
                    "hisMonth8": null,
                    "hisMonth9": null,
                    "hisMonth10": null,
                    "hisMonth11": null,
                    "hisMonth12": null,
                    "hisYear": "1402",
                    "historyDays": null,
                    "historyMonths": null,
                    "historyYears": 10,
                    "risuid": null,
                    "sumHistoryYears": null,
                    "sumYear": null
                },
                {
                    "id": 2,
                    "hisMonth1": "31",
                    "hisMonth2": null,
                    "hisMonth3": null,
                    "hisMonth4": null,
                    "hisMonth5": null,
                    "hisMonth6": null,
                    "hisMonth7": null,
                    "hisMonth8": null,
                    "hisMonth9": null,
                    "hisMonth10": null,
                    "hisMonth11": null,
                    "hisMonth12": null,
                    "hisYear": "1401",
                    "historyDays": null,
                    "historyMonths": null,
                    "historyYears": 5,
                    "risuid": null,
                    "sumHistoryYears": null,
                    "sumYear": null
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

    val historyJobInfosSuccess = """
        {
            "list": [
                {
                    "risuid": "0081631829",
                    "rwshName": "شرکت صنایع دما بخار مشهد",
                    "brhcode": "6400",
                    "id": 1,
                    "jobDesc": "کارمند اداری ۱",
                    "startDate": "139810",
                    "rwshId": "6393610019"
                }
            ],
            "total": 1
        }
    """.trimIndent()

    val historyJobInfosEmpty = """
        {
            "list": [],
            "total": 0
        }
    """.trimIndent()
}
