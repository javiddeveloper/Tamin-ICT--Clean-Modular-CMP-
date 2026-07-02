package com.tamin.taminhamrah.apiService.personal

import com.tamin.taminhamrah.apiService.BaseApiTest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PersonalApiServiceTest : BaseApiTest() {

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
