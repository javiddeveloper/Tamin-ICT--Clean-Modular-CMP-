package com.tamin.taminhamrah.apiService.weddingPresent

import com.tamin.taminhamrah.apiService.BaseApiTest
import de.jensklingenberg.ktorfit.Ktorfit
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class WeddingPresentApiServiceTest : BaseApiTest() {

    @Test
    fun getWeddingPresentInfo_returnsInfo() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": {
                    "risuid": "1234567890",
                    "nationalCode": "0012345678",
                    "insuranceFirstName": "علی",
                    "insuranceLastName": "رضایی",
                    "mobilNumber": "09121234567",
                    "insuranceTypeDesc": "اجباری",
                    "insuranceStatusDesc": "فعال",
                    "bankAccount": "123456",
                    "bankName": "ملی",
                    "branchCode": "01",
                    "branchName": "تهران",
                    "requestHelpType": "01",
                    "serviceDateTimeStamp": "1700000000000"
                }
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createWeddingPresentApiService()

        val response = apiService.getWeddingPresentInfo()

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertEquals("1234567890", response.data?.risuid)
        assertEquals("علی", response.data?.insuranceFirstName)
        assertEquals("رضایی", response.data?.insuranceLastName)
        assertEquals("09121234567", response.data?.mobileNumber)
    }

    @Test
    fun submitWeddingPresent_nullData_isAllowed() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": null
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createWeddingPresentApiService()

        val response = apiService.submitWeddingPresent(
            com.tamin.taminhamrah.model.weddingPresent.ShortTermMarriageRequestDTO(
                partnerNationalId = "0098765432",
                shortTermRequest = com.tamin.taminhamrah.model.weddingPresent.MarriageGiftRequestDTO(
                    risuid = "1234567890",
                ),
                weddingDateTimeStamp = 1700000000000L,
            )
        )

        assertEquals(200, response.status)
        assertNull(response.data)
    }
}
