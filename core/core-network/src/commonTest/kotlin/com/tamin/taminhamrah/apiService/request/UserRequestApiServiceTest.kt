package com.tamin.taminhamrah.apiService.request

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.apiService.userRequest.UserRequestApiService
import com.tamin.taminhamrah.apiService.userRequest.createUserRequestApiService
import com.tamin.taminhamrah.model.userRequest.UserRequestDTO
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.UserRequestTestData
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserRequestApiServiceTest : BaseApiTest() {

    @Test
    fun `getUserRequests should return user request list`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = UserRequestTestData.userRequestsSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createUserRequestApiService()

        val response = apiService.getUserRequests(emptyMap())

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)

        val listData = response.data
        assertNotNull(listData)
        assertEquals(1, listData.total)

        val requests: List<UserRequestDTO> = listData.list.orEmpty()
        assertEquals(1, requests.size)

        val firstRequest = requests.first()
        assertEquals(478176975L, firstRequest.id)
        assertEquals("1073555545", firstRequest.refCode)
        assertEquals("انعقاد قرارداد بيمه اختياري", firstRequest.title)
        assertEquals("2903", firstRequest.status?.requestCode)
        assertEquals("انعقاد قرارداد", firstRequest.status?.requestDesc)
        assertEquals(35L, firstRequest.requestType?.id)
        assertEquals("478176974", firstRequest.referenceId)
    }

    @Test
    fun `getRequestTypes should return request type list`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = UserRequestTestData.requestTypesSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createUserRequestApiService()

        val response = apiService.getRequestTypes(emptyMap())

        assertEquals(200, response.status)
        val listData = response.data
        assertNotNull(listData)
        assertEquals(64, listData.total)

        val types = listData.list.orEmpty()
        assertEquals(1, types.size)
        assertEquals(67L, types.first().id)
        assertEquals("خاتمه کفالت", types.first().description)
    }

    @Test
    fun `getMyRequestErrorList should return request errors`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = UserRequestTestData.requestErrorsSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createUserRequestApiService()

        val response = apiService.getMyRequestErrorList(mapOf("filter" to "[{\"property\":\"request.id\",\"value\":123}]"))

        assertEquals(200, response.status)
        val listData = response.data
        assertNotNull(listData)
        assertEquals(1, listData.total)

        val errors = listData.list.orEmpty()
        assertEquals(1, errors.size)
        assertEquals(101L, errors.first().id)
        assertEquals("نقص مدارک شناسایی", errors.first().errorMessage)
    }

    @Test
    fun `getSmartGuideList should return smart guide FAQ items`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = UserRequestTestData.smartGuideSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createUserRequestApiService()

        val response = apiService.getSmartGuideList(mapOf("requestType" to "3", "isPublic" to "1"))

        assertEquals(200, response.status)
        val listData = response.data
        assertNotNull(listData)
        assertEquals(1, listData.total)

        val items = listData.list.orEmpty()
        assertEquals(1, items.size)
        assertEquals(201L, items.first().id)
        assertEquals("شرایط ثبت درخواست چیست؟", items.first().question)
        assertEquals(true, items.first().isPublic)
    }

    @Test
    fun `getUserRequests should parse salary deduction certificate request correctly`() = runTest {
        val jsonResponse = UserRequestTestData.salaryDeductionCertificateSuccess

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createUserRequestApiService()

        val response = apiService.getUserRequests(mapOf("filter" to "[{\"value\":\"03\",\"operator\":\"EQUAL\",\"property\":\"operation\"},{\"property\":\"refCode\",\"value\":\"1075558440\",\"operator\":\"EQ\"},{\"property\":\"requestType.id\",\"value\":22,\"operator\":\"EQ\"}]"))

        assertEquals(200, response.status)
        val listData = response.data
        assertNotNull(listData)
        assertEquals(1, listData.total)

        val item = listData.list?.first()
        assertNotNull(item)
        assertEquals(491371155L, item.id)
        assertEquals("1075558440", item.refCode)
        assertEquals("درخواست گواهي کسر از حقوق", item.title)
        assertEquals("0018", item.status?.requestCode)
        assertEquals("مختومه-تاييد نهايي", item.status?.requestDesc)
        assertEquals(22L, item.requestType?.id)
        assertEquals("سيدرحمت اله ميرفضلي", item.createByName)
    }

    @Test
    fun `getUserRequestDetail should return a single request`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = """
                {
                    "id": 491371155,
                    "refCode": "1075558440",
                    "title": "درخواست گواهي کسر از حقوق",
                    "createByName": "سيدرحمت اله ميرفضلي",
                    "refrenceid": "491371155",
                    "requestType": { "id": 22, "title": "درخواست گواهي کسر از حقوق" },
                    "status": { "requestCode": "0018", "requestDesc": "مختومه-تاييد نهايي" }
                }
            """.trimIndent()
        )
        val apiService = createMockKtorfit(jsonResponse).createUserRequestApiService()

        val response = apiService.getUserRequestDetail(491371155L)

        assertEquals(200, response.status)
        assertEquals(491371155L, response.data?.id)
        assertEquals("1075558440", response.data?.refCode)
        assertEquals("491371155", response.data?.referenceId)
    }

    @Test
    fun `getShortTermRequestStatus should parse process data list`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = """
                {
                    "total": 1,
                    "list": [
                        { "date_acc": 1717000000000, "process_result": "تایید شد", "rejectReson": null }
                    ]
                }
            """.trimIndent()
        )
        val apiService = createMockKtorfit(jsonResponse).createUserRequestApiService()

        val response = apiService.getShortTermRequestStatus("ref-1")

        assertEquals(200, response.status)
        assertEquals(1, response.data?.total)
        assertEquals("تایید شد", response.data?.list?.first()?.processResult)
    }

    @Test
    fun `getDeferredInstallmentInfo should parse wage assignment request`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = """
                {
                    "firstName": "نام",
                    "lastName": "نام خانوادگی",
                    "nationalId": "0010000000",
                    "birthDate": 1716000000000,
                    "pensionerNationalId": "0020000000",
                    "userFirstName": "کاربر",
                    "userLastName": "کاربری",
                    "pensionerId": "123456",
                    "bank": { "bankName": "بانک ملی" },
                    "bankBranch": "شعبه مرکزی",
                    "installmentAmount": 15000000,
                    "installmentCount": 12,
                    "loanAmount": 180000000,
                    "guaranteeAmount": 20000000
                }
            """.trimIndent()
        )
        val apiService = createMockKtorfit(jsonResponse).createUserRequestApiService()

        val response = apiService.getDeferredInstallmentInfo("req-1")

        assertEquals(200, response.status)
        assertEquals("نام", response.data?.firstName)
        assertEquals("بانک ملی", response.data?.bank?.bankName)
        assertEquals(12, response.data?.installmentCount)
    }
}


