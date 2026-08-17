package com.tamin.taminhamrah.tools

import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.errorHandling.getTaminErrorUri
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * What each way a call can fail is reported as.
 *
 * The point of these is that they are *different*: the previous helper answered NO_CONNECTION_ERROR
 * to everything it did not recognize, so a 500 and a parse failure both told the user to check
 * their internet.
 */
class SafeApiCallTest {

    private val parser = ErrorParserImpl()

    @Test
    fun returnsTheValueWhenNothingFails() = runTest {
        val result = parser.safeApiCall("ok") { "data" }

        assertEquals("data", result)
    }

    @Test
    fun cancellationIsNotAnError() = runTest {
        // A screen that was left, or a sibling request torn down when its partner failed, must not
        // raise a dialog — so this one type has to come back out untouched.
        assertFailsWith<CancellationException> {
            parser.safeApiCall<Unit>("cancelled") { throw CancellationException("left the screen") }
        }
    }

    @Test
    fun anAlreadyClassifiedEnvelopeErrorKeepsItsVerdict() = runTest {
        val thrown = assertFailsWith<TaminApiException> {
            parser.safeApiCall<Unit>("envelope") {
                throw TaminErrorUriException(ErrorUri.RESOURCE_NOT_FOUND)
            }
        }

        assertEquals(ErrorUri.RESOURCE_NOT_FOUND, thrown.getTaminErrorUri())
    }

    @Test
    fun anAlreadyParsedExceptionPassesThrough() = runTest {
        val original = TaminApiException(title = "قبلا تفسیر شده")

        val thrown = assertFailsWith<TaminApiException> {
            parser.safeApiCall<Unit>("nested") { throw original }
        }

        assertEquals(original, thrown)
    }

    @Test
    fun aTimeoutSaysSoRatherThanBlamingTheConnection() = runTest {
        val thrown = assertFailsWith<TaminApiException> {
            parser.safeApiCall<Unit>("timeout") { throw HttpRequestTimeoutException("url", 1_000) }
        }

        assertEquals(ErrorUri.SERVICE_TIMEOUT, thrown.getTaminErrorUri())
    }

    /** The 500-with-a-plain-text-body case that started this: the body cannot be deserialized. */
    @Test
    fun anUnreadableBodyIsAServerFaultNotAConnectionOne() = runTest {
        val thrown = assertFailsWith<TaminApiException> {
            parser.safeApiCall<Unit>("garbage") {
                throw SerializationException("Unexpected JSON token at offset 0")
            }
        }

        assertEquals(ErrorUri.INTERNAL_ERROR, thrown.getTaminErrorUri())
        assertTrue(
            thrown.getTaminErrorUri() != ErrorUri.NO_CONNECTION_ERROR,
            "an answering server is not a missing network",
        )
    }

    @Test
    fun anythingElseIsStillTheConnection() = runTest {
        val thrown = assertFailsWith<TaminApiException> {
            parser.safeApiCall<Unit>("offline") { throw IllegalStateException("no route to host") }
        }

        assertEquals(ErrorUri.NO_CONNECTION_ERROR, thrown.getTaminErrorUri())
    }
}
