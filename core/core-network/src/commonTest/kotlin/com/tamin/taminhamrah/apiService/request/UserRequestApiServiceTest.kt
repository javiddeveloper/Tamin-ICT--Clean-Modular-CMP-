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
}
