package com.tamin.taminhamrah.tools

import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.tools.errorHandling.taminErrorUriOrNull
import io.ktor.serialization.JsonConvertException
import kotlinx.serialization.SerializationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NetworkUtilsTest {

    @Test
    fun `looksLikeArabicScript is true for Persian copy`() {
        assertTrue("خطایی رخ داده است".looksLikeArabicScript())
        assertTrue("شما فاقد شماره حساب بانکی می باشید".looksLikeArabicScript())
    }

    @Test
    fun `looksLikeArabicScript is true for mixed text that contains Arabic-script characters`() {
        assertTrue("Error: خطای سرور".looksLikeArabicScript())
    }

    @Test
    fun `looksLikeArabicScript is false for English and technical tokens`() {
        assertFalse("Internal Server Error".looksLikeArabicScript())
        assertFalse("sso.to.sa.connection.exception".looksLikeArabicScript())
        assertFalse("invalid_grant".looksLikeArabicScript())
        assertFalse("".looksLikeArabicScript())
    }

    /**
     * Ktor never throws the SerializationException itself — it wraps it. EM-2716: a 200 reply that
     * would not decode read as UNKNOWN, "مشکلی پیش آمده است", on a screen whose call had succeeded.
     */
    @Test
    fun `a reply ktor could not decode is the server's answer, not an unknown failure`() {
        val wrapped = JsonConvertException("Illegal input", SerializationException("missing field"))

        assertEquals(ErrorUri.INTERNAL_ERROR, wrapped.toErrorUri())
    }

    @Test
    fun `a transport failure is a connection problem`() {
        assertEquals(ErrorUri.NO_CONNECTION_ERROR, FakeIOException().toErrorUri())
    }

    @Test
    fun `anything else stays unknown`() {
        assertEquals(ErrorUri.UNKNOWN, IllegalStateException("boom").toErrorUri())
    }

    @Test
    fun `safeCall reports an undecodable reply as a server error`() {
        val error = assertFailsWith<TaminApiException> {
            ErrorParserImpl().safeCall("test") {
                throw JsonConvertException("Illegal input", SerializationException("missing field"))
            }
        }

        assertEquals(ErrorUri.INTERNAL_ERROR, error.taminErrorUriOrNull())
    }
}
