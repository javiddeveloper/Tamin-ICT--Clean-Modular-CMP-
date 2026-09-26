package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.HttpErrorCopy
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.readByteChannel
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.CommonTestData
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CommonApiServiceTest : BaseApiTest() {

    @Test
    fun `getJobTitle should return job title list`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = CommonTestData.jobTitleSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createCommonApiService()

        val response = apiService.getJobTitle(emptyMap())

        assertEquals(200, response.status)
        val list = response.data?.list.orEmpty()
        assertEquals(1, list.size)
        assertEquals("123", list.first().jobCode)
    }

    @Test
    fun `getRegistrationDeclarationForm should return HttpStatement`() = runTest {
        val ktorfit = createMockKtorfit(ApiTestUtils.createJsonResponse(dataJson = ""))
        val apiService = ktorfit.createCommonApiService()

        val response = apiService.getRegistrationDeclarationForm()
        assertNotNull(response)
    }

    /** A failed download used to hand the error body to the PDF viewer as if it were the file. */
    @Test
    fun `a failed download is an error, not the file`() = runTest {
        val body = """{"status":500,"family":"SERVER_ERROR","reason":"Internal Server Error","data":{"message":"گواهی یافت نشد"}}"""
        val apiService = createMockKtorfit(body, status = HttpStatusCode.InternalServerError).createCommonApiService()

        val error = assertFailsWith<TaminErrorUriException> {
            apiService.getRegistrationDeclarationForm().readByteChannel()
        }
        assertEquals("گواهی یافت نشد", error.serverMessage)
    }

    /** Seen live: a WAF answers the static form with a 403 HTML page. */
    @Test
    fun `an HTML error page on a download maps by status`() = runTest {
        val apiService = createMockKtorfit(
            "<html>403 Forbidden</html>",
            status = HttpStatusCode.Forbidden,
            contentType = ContentType.Text.Html,
        ).createCommonApiService()

        val error = assertFailsWith<TaminErrorUriException> {
            apiService.getRegistrationDeclarationForm().readByteChannel()
        }
        assertEquals(ErrorUri.FORBIDDEN, error.uri)
    }

    /** The old app read an empty 204 as "محتوای مورد نظر شما قابل دسترس نیست" rather than a parse failure. */
    @Test
    fun `a 204 reply has its own message`() = runTest {
        val apiService = createMockKtorfit("", status = HttpStatusCode.NoContent).createCommonApiService()

        val error = assertFailsWith<TaminErrorUriException> { apiService.getJobTitle(emptyMap()) }
        assertEquals(HttpErrorCopy.NO_CONTENT, error.serverMessage)
    }

    @Test
    fun `checkInsuredInfo should return the pensioner-detection list`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = """{ "total": 2, "list": ["05", "این سرویس برای شما فعال نیست"], "typeUser": null }"""
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createCommonApiService()

        val response = apiService.checkInsuredInfo()

        assertEquals(200, response.status)
        assertEquals(listOf("05", "این سرویس برای شما فعال نیست"), response.data?.list)
    }
}
