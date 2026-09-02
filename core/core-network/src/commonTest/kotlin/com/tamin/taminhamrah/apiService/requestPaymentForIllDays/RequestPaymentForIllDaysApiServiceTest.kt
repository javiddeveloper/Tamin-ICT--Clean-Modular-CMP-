package com.tamin.taminhamrah.apiService.requestPaymentForIllDays

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.util.ApiTestUtils
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RequestPaymentForIllDaysApiServiceTest : BaseApiTest() {

    @Test
    fun getLatestInsuranceInfo_returnsInsuredMainInfo() = runTest {
        val ktorfit = createMockKtorfit(
            ApiTestUtils.createJsonResponse(RequestPaymentForIllDaysTestData.insuredMainInfoSuccess)
        )
        val apiService = ktorfit.createRequestPaymentForIllDaysApiService()

        val response = apiService.getLatestInsuranceInfo()

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertEquals("1234567890", response.data?.risuid)
        assertEquals("0012345678", response.data?.nationalCode)
        assertEquals(1, response.data?.branchWorkshop?.size)
    }

    @Test
    fun getCovidResult_returnsTimestampsList() = runTest {
        val ktorfit = createMockKtorfit(
            ApiTestUtils.createJsonResponse(RequestPaymentForIllDaysTestData.covidResultSuccess)
        )
        val apiService = ktorfit.createRequestPaymentForIllDaysApiService()

        val response = apiService.getCovidResult()

        assertEquals(200, response.status)
        assertEquals(listOf("1700000000", "1700086400"), response.data?.list)
    }

    @Test
    fun sendRequestForIllDay_returnsResultMessage() = runTest {
        val ktorfit = createMockKtorfit(
            ApiTestUtils.createJsonResponse(RequestPaymentForIllDaysTestData.saveIllnessSuccess)
        )
        val apiService = ktorfit.createRequestPaymentForIllDaysApiService()

        val response = apiService.sendRequestForIllDay(
            com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessRequestDTO(
                illnessKind = "2",
                workStatus = "2",
            )
        )

        assertEquals(200, response.status)
        assertEquals("درخواست با موفقیت ثبت شد", response.data?.shorttermRequest?.resultMessage)
    }
}

object RequestPaymentForIllDaysTestData {
    val insuredMainInfoSuccess = """
        {
            "risuid": "1234567890",
            "nationalCode": "0012345678",
            "insuranceFirstName": "علی",
            "insuranceLastName": "رضایی",
            "mobilNumber": "09120000000",
            "branchCode": "0100",
            "branchName": "شعبه مرکزی",
            "bankAccount": "123",
            "bankName": "ملی",
            "serviceDateTimeStamp": 1700000000,
            "branchWorkshop": [
                {
                    "branchCode": "0100",
                    "branchName": "شعبه مرکزی",
                    "workshopCode": "W1",
                    "workshopName": "کارگاه یک"
                }
            ]
        }
    """.trimIndent()

    val covidResultSuccess = """
        {
            "total": 2,
            "list": ["1700000000", "1700086400"]
        }
    """.trimIndent()

    val saveIllnessSuccess = """
        {
            "shorttermRequest": {
                "resultMessage": "درخواست با موفقیت ثبت شد"
            }
        }
    """.trimIndent()
}
