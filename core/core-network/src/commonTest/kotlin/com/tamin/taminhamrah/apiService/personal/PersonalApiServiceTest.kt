package com.tamin.taminhamrah.apiService.personal

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.model.personal.submitFinalSurvivorPension.SubmitFinalSurvivorPensionRequest
import com.tamin.taminhamrah.tools.extractMessage
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PersonalApiServiceTest : BaseApiTest() {

    @Test
    fun `checkGirlSurvivorConditions should handle null data and return success status`() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESSFUL",
                "reason": "OK",
                "data": null
            }
        """.trimIndent()
        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPersonalApiService()

        val response = apiService.checkGirlSurvivorConditions("123", "04", "456")

        assertEquals(200, response.status)
        assertEquals("OK", response.extractMessage())
    }

    @Test
    fun `submitFinalSurvivorPension should handle string data`() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESSFUL",
                "reason": "OK",
                "data": "Operation successful"
            }
        """.trimIndent()
        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPersonalApiService()

        val response = apiService.submitFinalSurvivorPension(1, SubmitFinalSurvivorPensionRequest(1))

        assertEquals(200, response.status)
        assertEquals("Operation successful", response.extractMessage())
    }

    @Test
    fun `getFinalSurvivorPensionPDF should return http statement`() = runTest {
        val ktorfit = createMockKtorfit("")
        val apiService = ktorfit.createPersonalApiService()

        val statement = apiService.getFinalSurvivorPensionPDF()

        statement.execute { response ->
            assertEquals(200, response.status.value)
        }
    }

    @Test
    fun `getFinalSurvivorPensionPDF should return error status`() = runTest {
        val ktorfit = createMockKtorfit("", status = io.ktor.http.HttpStatusCode.InternalServerError)
        val apiService = ktorfit.createPersonalApiService()

        val statement = apiService.getFinalSurvivorPensionPDF()

        statement.execute { response ->
            assertEquals(500, response.status.value)
        }
    }
}
