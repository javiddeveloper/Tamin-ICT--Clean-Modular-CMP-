package com.tamin.taminhamrah.tools

import co.touchlab.kermit.Logger
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
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
    } catch (e: CancellationException) {
        // A canceled call is not a failed one. Turning it into an error made a screen the user had
        // already left report a problem, and hid genuine cancellation from the caller.
        throw e
    } catch (e: Exception) {
        Logger.e(tag = tag) { "API call failed: ${e::class.simpleName} - ${e.message}" }
        throw parseGeneralError(TaminErrorUriException(e.toErrorUri()))
    }
}

/**
 * Which failure this actually was.
 *
 * Everything unrecognized used to be reported as [ErrorUri.NO_CONNECTION_ERROR], so a server that
 * answered — with a rejection whose body would not deserialize — told the user to check their
 * internet. The two are not the same thing and must not read the same: only a transport failure is
 * a connection problem; a reply we could not read is the server's answer, however unhelpful.
 */
fun Throwable.toErrorUri(): ErrorUri = when {
    // The old app told these apart: a timeout is "زمان درخواست به پایان رسید", not "no internet".
    causes().any { it::class.simpleName.orEmpty().contains("Timeout", ignoreCase = true) } -> ErrorUri.SERVICE_TIMEOUT
    isConnectivityFailure() -> ErrorUri.NO_CONNECTION_ERROR
    // The request arrived and something came back — it just was not what the contract promised.
    // Ktor wraps it (JsonConvertException), so look down the cause chain, not at this alone.
    causes().any { it is SerializationException } -> ErrorUri.INTERNAL_ERROR
    else -> ErrorUri.UNKNOWN
}

private fun Throwable.causes(): Sequence<Throwable> =
    generateSequence(this) { it.cause?.takeIf { cause -> cause !== it } }

/**
 * Whether this failure happened on the wire rather than after it.
 *
 * Matched on the exception's own name because the transport types differ per platform and per ktor
 * engine — an `IOException` on Android, a `SocketException`/`NSURLError`-backed failure on iOS —
 * and a common-code `when` on the class cannot see all of them.
 */
fun Throwable.isConnectivityFailure(): Boolean = causes().any { cause ->
    val name = cause::class.simpleName.orEmpty()
    CONNECTIVITY_MARKERS.any { name.contains(it, ignoreCase = true) }
}

private val CONNECTIVITY_MARKERS = listOf(
    "IOException",
    "UnresolvedAddress",
    "ConnectTimeout",
    "SocketTimeout",
    "HttpRequestTimeout",
    "UnknownHost",
    "SSL",
)
