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

}
