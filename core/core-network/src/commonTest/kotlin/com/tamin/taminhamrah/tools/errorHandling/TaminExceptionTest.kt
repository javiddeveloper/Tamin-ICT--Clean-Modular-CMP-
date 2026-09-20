package com.tamin.taminhamrah.tools.errorHandling

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TaminExceptionTest {

    /** What a data source actually throws: the parsed [TaminApiException], carrying the uri as cause. */
    @Test
    fun `a parsed api failure reports the uri it was classified as`() {
        val parsed = ErrorUri.NO_CONNECTION_ERROR.toApiException()

        assertEquals(ErrorUri.NO_CONNECTION_ERROR, parsed.taminErrorUriOrNull())
    }

    /** The unparsed form, which `safeCall` rethrows straight through for a known uri. */
    @Test
    fun `a bare uri exception reports its own uri`() {
        val bare = TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)

        assertEquals(ErrorUri.SERVICE_TIMEOUT, bare.taminErrorUriOrNull())
    }

    /**
     * Anything else is null rather than a throw — this is the difference from [getTaminErrorUri],
     * which casts the cause and dies on a failure that never reached the parser.
     */
    @Test
    fun `a throwable that never reached the parser has no uri`() {
        assertNull(RuntimeException("boom").taminErrorUriOrNull())
        assertNull(TaminApiException(title = "خطا").taminErrorUriOrNull())
    }

    /** A server message wins the copy, but the classification underneath it still comes through. */
    @Test
    fun `a server-worded failure keeps the uri under its own message`() {
        val parsed = ErrorParserImpl().parseGeneralError(
            TaminErrorUriException(
                uri = ErrorUri.INVALID_REQUEST,
                serverMessage = "کد ملی نامعتبر است",
            )
        )

        assertEquals("خطا, کد ملی نامعتبر است", parsed.toSingleLineMessage())
        assertEquals(ErrorUri.INVALID_REQUEST, parsed.taminErrorUriOrNull())
    }
}
