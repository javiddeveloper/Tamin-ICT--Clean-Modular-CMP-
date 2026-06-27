package com.tamin.taminhamrah.apiService.personal

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.PersonalTestData
import com.tamin.taminhamrah.model.personal.InsuredDocDTO
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PersonalApiServiceTest : BaseApiTest() {

    @Test
    fun `putInsuredRegistrationDocList should return success string`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PersonalTestData.insuredDocSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.create<PersonalApiService>()

        val response = apiService.putInsuredRegistrationDocList("123", listOf(InsuredDocDTO(null, null, null)))

        assertEquals(200, response.status)
        assertEquals("Success", response.data)
    }

    @Test
    fun `getRequestSummary should return new insured summary`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PersonalTestData.requestSummarySuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.create<PersonalApiService>()

        val response = apiService.getRequestSummary("req_123")

        assertEquals(200, response.status)
        assertEquals("REF123", response.data?.refCode)
        assertEquals("0000000000", response.data?.nationalId)
    }
}
