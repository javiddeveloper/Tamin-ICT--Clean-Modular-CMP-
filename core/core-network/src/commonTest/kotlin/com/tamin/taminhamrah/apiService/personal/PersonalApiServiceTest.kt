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
    fun `getFinalSurvivorPensionPDF should return http statement`() = runTest {
        val ktorfit = createMockKtorfit("")
        val apiService = ktorfit.createPersonalApiService()
    fun `putInsuredRegistrationDocList should return success string`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PersonalTestData.insuredDocSuccess
        )

        val statement = apiService.getFinalSurvivorPensionPDF()
        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.create<PersonalApiService>()

        statement.execute { response ->
            assertEquals(200, response.status.value)
        }
        val response = apiService.putInsuredRegistrationDocList("123", listOf(InsuredDocDTO(null, null, null)))

        assertEquals(200, response.status)
        assertEquals("Success", response.data)
    }

    @Test
    fun `getFinalSurvivorPensionPDF should return error status`() = runTest {
        val ktorfit = createMockKtorfit("", status = io.ktor.http.HttpStatusCode.InternalServerError)
        val apiService = ktorfit.createPersonalApiService()
    fun `getRequestSummary should return new insured summary`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PersonalTestData.requestSummarySuccess
        )

        val statement = apiService.getFinalSurvivorPensionPDF()
        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.create<PersonalApiService>()

        statement.execute { response ->
            assertEquals(500, response.status.value)
        }
        val response = apiService.getRequestSummary("req_123")

        assertEquals(200, response.status)
        assertEquals("REF123", response.data?.refCode)
        assertEquals("0000000000", response.data?.nationalId)
    }
}
