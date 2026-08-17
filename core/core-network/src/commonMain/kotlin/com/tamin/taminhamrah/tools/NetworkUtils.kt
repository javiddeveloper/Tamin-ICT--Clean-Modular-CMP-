package com.tamin.taminhamrah.tools

import co.touchlab.kermit.Logger
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.HttpStatusErrorMapper
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

/**
 * Arabic-script heuristic used to decide whether a backend string is safe
 * to show to the user. Same Unicode ranges as old_android's
 * `ValidationUtil.isProbablyArabic()` — Persian, Arabic, and related presentation forms.
 */
private val ARABIC_SCRIPT_REGEX = Regex("[\u0600-\u06FF\uFB50-\uFDFF\uFE70-\uFEFF]")

fun String.looksLikeArabicScript(): Boolean = ARABIC_SCRIPT_REGEX.containsMatchIn(this)

/**
 * Runs [block], normalizing every failure through [ErrorParser].
 * Consolidates try/catch boilerplate for remote data sources.
 */
inline fun <T> ErrorParser.safeCall(
    tag: String,
    block: () -> T
): T {
    return try {
        block()
    } catch (e: TaminErrorUriException) {
        throw parseGeneralError(e)
    } catch (e: Exception) {
        Logger.e(tag = tag) { "API call failed: ${e::class.simpleName} - ${e.message}" }
        throw parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
    }
}

/**
 * The same, with every failure a call can actually have told apart rather than called one thing.
 *
 * [safeCall] answers "no connection" to anything it does not recognize, which is right for a dropped
 * network and wrong for everything else: a server that answers 500 in plain text, a reply the app
 * cannot parse, a request that times out — all of them told the user to check their internet while
 * the internet was fine.
 *
 * The cases, in the order they are tested:
 *
 *  - **Cancellation** is not a failure. A screen that was left, or a sibling request torn down when
 *    its partner failed, must not raise a dialog. Rethrown untouched.
 *  - **[TaminErrorUriException]** already carries a verdict — `BaseDTO.extractData` classified the
 *    envelope's own status through `HttpStatusErrorMapper`. Parsed, not reclassified.
 *  - **[TaminApiException]** is already parsed, from a nested call. Passed through.
 *  - **Timeouts** — the request, the socket, or the connect attempt — are `SERVICE_TIMEOUT`, which
 *    tells the user to try again rather than to check a connection that is working.
 *  - **[ResponseException]** only arrives if a client turns `expectSuccess` on; this one does not,
 *    so a status normally reaches the envelope instead. Mapped by status anyway, so the branch does
 *    not become a lie if that setting ever changes.
 *  - **[SerializationException]** means the server answered and the answer was not the contract —
 *    a 500 whose body is a plain sentence, an HTML error page from a proxy. That is a server fault,
 *    not a connectivity one, so it reads as `INTERNAL_ERROR`.
 *  - **Anything else** is the genuine no-connection case that was the original catch-all.
 */
suspend fun <T> ErrorParser.safeApiCall(
    tag: String,
    block: suspend () -> T,
): T {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: TaminErrorUriException) {
        throw parseGeneralError(e)
    } catch (e: TaminApiException) {
        throw e
    } catch (e: HttpRequestTimeoutException) {
        throw timeout(tag, e)
    } catch (e: SocketTimeoutException) {
        throw timeout(tag, e)
    } catch (e: ConnectTimeoutException) {
        throw timeout(tag, e)
    } catch (e: ResponseException) {
        val status = e.response.status.value
        val body = runCatching { e.response.bodyAsText() }.getOrNull()
        Logger.e(tag = tag) { "API call failed: HTTP $status" }
        val mapped = HttpStatusErrorMapper.map(status = status, rawMessage = body, cause = null)
        throw parseGeneralError(
            TaminErrorUriException(
                uri = mapped.uri,
                serverMessage = mapped.userMessage,
                navigateBack = mapped.navigateBack,
            )
        )
    } catch (e: SerializationException) {
        Logger.e(tag = tag) { "API call returned an unreadable body: ${e.message}" }
        throw parseGeneralError(TaminErrorUriException(ErrorUri.INTERNAL_ERROR))
    } catch (e: Exception) {
        Logger.e(tag = tag) { "API call failed: ${e::class.simpleName} - ${e.message}" }
        throw parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
    }
}

private fun ErrorParser.timeout(tag: String, e: Exception): TaminApiException {
    Logger.e(tag = tag) { "API call timed out: ${e::class.simpleName}" }
    return parseGeneralError(TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT))
}
