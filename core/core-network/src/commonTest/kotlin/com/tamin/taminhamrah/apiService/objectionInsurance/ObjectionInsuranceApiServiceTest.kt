package com.tamin.taminhamrah.apiService.objectionInsurance

import com.tamin.taminhamrah.apiService.BaseApiTest
import de.jensklingenberg.ktorfit.Ktorfit
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ObjectionInsuranceApiServiceTest : BaseApiTest() {

    @Test
    fun checkStatusConflict_returnsBoolean() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": true
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createObjectionInsuranceApiService()

        val response = apiService.checkStatusConflict()

        assertEquals(200, response.status)
        assertEquals(true, response.data)
    }

    @Test
    fun getConflictHistories_returnsList() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": {
                    "total": 1,
                    "list": [
                        {
                            "branchCode": "0950",
                            "branchname": "شعبه نمونه",
                            "year": "1402",
                            "rwshid": "12345",
                            "workShopName": "کارگاه تست",
                            "om1": "30",
                            "mm1": "20"
                        }
                    ]
                }
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createObjectionInsuranceApiService()

        val response = apiService.getConflictHistories(
            mapOf("page" to "0", "start" to "0", "limit" to "60", "filter" to "[]", "sort" to "[]")
        )

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertEquals(1, response.data?.total)
        val item = response.data?.list?.first()
        assertEquals("0950", item?.branchCode)
        assertEquals("شعبه نمونه", item?.branchName)
        assertEquals("1402", item?.year)
        assertEquals("12345", item?.workshopId)
        assertEquals("کارگاه تست", item?.workshopName)
        assertEquals("30", item?.oldMonth1)
        assertEquals("20", item?.newMonth1)
    }

    @Test
    fun saveConflict_returnsMessage() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": "saved"
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createObjectionInsuranceApiService()

        val response = apiService.saveConflict(emptyList())

        assertEquals(200, response.status)
        assertEquals("saved", response.data)
    }

    @Test
    fun saveConflict_nullData_isAllowed() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": null
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createObjectionInsuranceApiService()

        val response = apiService.saveConflict(emptyList())

        assertEquals(200, response.status)
        assertNull(response.data)
    }

    @Test
    fun confirmConflict_returnsBoolean() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": true
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createObjectionInsuranceApiService()

        val response = apiService.confirmConflict(emptyList())

        assertEquals(200, response.status)
        assertTrue(response.data == true)
    }

    @Test
    fun finalConfirmConflict_returnsTrackingNumber() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": "TRK-123"
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createObjectionInsuranceApiService()

        val response = apiService.finalConfirmConflict()

        assertEquals(200, response.status)
        assertEquals("TRK-123", response.data)
    }
}
