package com.tamin.taminhamrah.tools.errorHandling

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PlainTextHttpErrorTest {

    @Test
    fun `maps bare Persian 500 body to TaminErrorUriException`() {
        val message = "اطلاعاتی از حکم مستمری یا فوت فرد مورد نظر شما یافت نشد."

        val error = plainTextErrorFromHttpBody(status = 500, bodyText = message)

        assertEquals(ErrorUri.INTERNAL_ERROR, error?.uri)
        assertEquals(message, error?.serverMessage)
    }

    @Test
    fun `returns null for JSON error envelope`() {
        val body = """
            {"status":400,"family":"CLIENT_ERROR","reason":"Bad Request","data":null}
        """.trimIndent()

        assertNull(plainTextErrorFromHttpBody(400, body))
    }

    @Test
    fun `returns null for English plain text`() {
        assertNull(plainTextErrorFromHttpBody(500, "Internal Server Error"))
    }

    @Test
    fun `returns null for empty body`() {
        assertNull(plainTextErrorFromHttpBody(500, "   "))
    }
}
