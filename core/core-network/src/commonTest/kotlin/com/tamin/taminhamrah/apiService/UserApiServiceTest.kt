package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.UserTestData
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserApiServiceTest : BaseApiTest() {

    @Test
    fun `getIdentityInfo should return identity info`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = UserTestData.identityInfoSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createUserApiService()

        val response = apiService.getIdentityInfo()

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        assertEquals("Javid", response.data?.firstName)
        assertEquals("Sattar", response.data?.lastName)
        assertEquals("0080000800", response.data?.nationalId)
    }

    @Test
    fun `changeMobile should return edit mobile response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = UserTestData.changeMobileSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createUserApiService()

        val response = apiService.changeMobile(
//            referer = "https://profile.tamin.ir/main/change-phone-number",
            url = "https://apim.tamin.ir/t/um-mobile-api.tamin.ir/change-mobile-number/request/v1",
            mobile = "09123456789"
        )

        assertEquals(200, response.status)
        assertEquals("test-trace-id-123", response.data?.traceId)
        assertEquals("test-hash-456", response.data?.data?.hash)
    }

    @Test
    fun `getUserProfile should return user profile`() = runTest {
        val jsonResponse = """{"status":200,"family":"SUCCESS","reason":"OK","data":{"entityId":"1","login":"user","firstName":"John","lastName":"Doe","email":"john@example.com","nationalCode":"1234567890","mobile":"09123456789"}}"""
        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createUserApiService()

        val response = apiService.getUserProfile()

        assertEquals(200, response.status)
        assertEquals("John", response.data?.firstName)
        assertEquals("Doe", response.data?.lastName)
        assertEquals("1234567890", response.data?.nationalCode)
    }
    @Test
    fun `checkUserIsNew should return boolean flag`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = UserTestData.checkUserIsNewSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createUserApiService()

        val response = apiService.checkUserIsNew("0000000000")

        assertEquals(200, response.status)
        assertEquals(true, response.data)
    }

    @Test
    fun `getRecipients should return recipient list with provided data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = UserTestData.recipientsSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createUserApiService()

        val response = apiService.getRecipients(emptyMap())

        assertEquals(200, response.status)
        assertEquals(10, response.data?.list?.size)
        assertEquals("001", response.data?.list?.get(0)?.recipientCode)
        assertEquals("دادگاه عمومي", response.data?.list?.get(0)?.recipientName)
        assertEquals("01", response.data?.list?.get(9)?.recipientCode)
        assertEquals("بانک رفاه کارگران", response.data?.list?.get(9)?.recipientName)
    }

    @Test
    fun `getStatusCertificateReport should return success with null data`() = runTest {
        val jsonResponse = UserTestData.certificateReportSuccess

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createUserApiService()

        val response = apiService.getStatusCertificateReport("[]")

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        assertNull(response.data)
    }

}
