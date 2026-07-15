package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.UserTestData
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UserApiServiceTest : BaseApiTest() {

    @Test
    fun `getIdentityInfo should return identity info`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = UserTestData.identityInfoSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.create<UserApiService>()

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
        val apiService = ktorfit.create<UserApiService>()

        val response = apiService.changeMobile(
            referer = "https://profile.tamin.ir/main/change-phone-number",
            url = "https://profile.tamin.ir/api/v2.0/users/data/request-otp",
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
        val apiService = ktorfit.create<UserApiService>()

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
        val apiService = ktorfit.create<UserApiService>()

        val response = apiService.checkUserIsNew("0000000000")

        assertEquals(200, response.status)
        assertEquals(true, response.data)
    }

}
