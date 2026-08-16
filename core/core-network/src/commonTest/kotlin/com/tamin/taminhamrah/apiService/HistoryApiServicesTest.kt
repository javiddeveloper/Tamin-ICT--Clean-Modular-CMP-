package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.util.ApiTestUtils
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

        val response = apiService.sendToInstitution(type1 = true, type2 = true, type3 = true)

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        assertNull(response.data)
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

    val userInfoSuccess = """
        {
            "serial1": "ا19",
            "militaryServiceCode": null,
            "fatherName": "فرج اله",
            "lastName": "حسين پناهي",
            "serial2": "632661",
            "creationTime": 1470332948237,
            "lastModificationTime": 1771136020155,
            "cityCode": "1514",
            "socialSecurityNumber": "2062144681",
            "lastModifiedBy": "3790166227",
            "issueplaceName": "دهگلان",
            "birthDate": "1361/10/01",
            "firstName": "عادل",
            "insuranceNumber": "0082984639",
            "genderCode": "01",
            "nationalID": "5589743451",
            "marriageCode": null,
            "createdBy": "5589127671",
            "identityNumber": "5",
            "countryCode": "0001",
            "id": "2782294052",
            "birthDateTimestamp": 409350600000,
            "issueplace": "1514",
            "nationCode": "01"
        }
    """.trimIndent()

    val sendToInstitutionSuccess = """null"""
}
