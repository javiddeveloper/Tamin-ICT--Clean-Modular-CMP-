package com.tamin.taminhamrah.tools.errorHandling

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlainTextHttpErrorTest {

    @Test
    fun `maps bare Persian 500 body to TaminErrorUriException`() {
        val message = "اطلاعاتی از حکم مستمری یا فوت فرد مورد نظر شما یافت نشد."

        val error = plainTextErrorFromHttpBody(status = 500, bodyText = message)

        assertEquals(ErrorUri.INTERNAL_ERROR, error?.uri)
        assertEquals(message, error?.serverMessage)
    }

    /** Seen live on `disability-request/personal`: a bare backend code, not a sentence. */
    @Test
    fun `maps a bare backend error code to its copy`() {
        val error = plainTextErrorFromHttpBody(
            status = 400,
            bodyText = "pension.disability.commission.not.possible.exception",
        )

        assertEquals(ErrorUri.INVALID_REQUEST, error?.uri)
        assertEquals(HttpErrorCopy.DISABILITY_COMMISSION_NOT_POSSIBLE, error?.serverMessage)
    }

    /** A Tamin envelope is read where the expected type is known, not here. */
    @Test
    fun `returns null for JSON error envelope`() {
        val body = """
            {"status":400,"family":"CLIENT_ERROR","reason":"Bad Request","data":null}
        """.trimIndent()

        assertNull(plainTextErrorFromHttpBody(400, body))
    }

    /** The old app chose the copy by status for anything that was not an envelope. */
    @Test
    fun `maps English plain text by its status`() {
        val error = plainTextErrorFromHttpBody(500, "Internal Server Error")

        assertEquals(ErrorUri.INTERNAL_ERROR, error?.uri)
        assertEquals(HttpErrorCopy.GENERIC_SERVER, error?.serverMessage)
    }

    @Test
    fun `maps an empty body by its status`() {
        val error = plainTextErrorFromHttpBody(502, "   ")

        assertEquals(ErrorUri.SERVER_SERVICE_UNAVAILABLE, error?.uri)
        assertEquals(HttpErrorCopy.BAD_GATEWAY, error?.serverMessage)
    }

    /** A gateway/WAF page (seen live as a 403 text/html on `view/assets/pdfs/…`). */
    @Test
    fun `maps an HTML error page by its status`() {
        val error = plainTextErrorFromHttpBody(403, "<html><body><h1>403 Forbidden</h1></body></html>")

        assertEquals(ErrorUri.FORBIDDEN, error?.uri)
        assertEquals(HttpErrorCopy.FORBIDDEN_VPN, error?.serverMessage)
        assertTrue(error?.navigateBack == true)
    }

    /** A framework error body (`{timestamp,status,error,path}`) is JSON but no Tamin envelope. */
    @Test
    fun `maps a non-envelope JSON body by its status`() {
        val body = """{"timestamp":"2026-09-26T08:00:00Z","status":404,"error":"Not Found","path":"/api/x"}"""

        val error = plainTextErrorFromHttpBody(404, body)

        assertEquals(ErrorUri.RESOURCE_NOT_FOUND, error?.uri)
        assertEquals(HttpErrorCopy.NOT_FOUND, error?.serverMessage)
    }

    @Test
    fun `an error envelope's own message wins`() {
        val body = """{"status":500,"family":"SERVER_ERROR","reason":"Internal Server Error",
            "data":{"cause":"ProxyProcessingException","message":"خطا در واکشی اطلاعات از سامانه ی وزارت بهداشت"}}"""

        val error = errorFromHttpBody(500, body)

        assertEquals(ErrorUri.INTERNAL_ERROR, error.uri)
        assertEquals("خطا در واکشی اطلاعات از سامانه ی وزارت بهداشت", error.serverMessage)
    }

    /** Validation failures arrive as `data: [{propertyViolations: {field: [msg, …]}}]`. */
    @Test
    fun `validation violations are joined, first message per field`() {
        val body = """{"status":400,"family":"CLIENT_ERROR","reason":"Bad Request",
            "data":[{"propertyViolations":{"mobile":["شماره موبایل نامعتبر است","x"],"zip":["کد پستی الزامی است"]}}]}"""

        val error = errorFromHttpBody(400, body)

        assertEquals(ErrorUri.INVALID_REQUEST, error.uri)
        assertEquals("شماره موبایل نامعتبر است\nکد پستی الزامی است", error.serverMessage)
    }

    @Test
    fun `a 204 has its own copy`() {
        val error = errorFromHttpBody(204, "")

        assertEquals(HttpErrorCopy.NO_CONTENT, error.serverMessage)
    }
}
