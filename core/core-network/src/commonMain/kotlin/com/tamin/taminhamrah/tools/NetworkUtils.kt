package com.tamin.taminhamrah.tools

import co.touchlab.kermit.Logger
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException

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
    fallbackUri: ErrorUri = ErrorUri.NO_CONNECTION_ERROR,
    block: () -> T
): T {
    return try {
        block()
    } catch (e: TaminErrorUriException) {
        throw parseGeneralError(e)
    } catch (e: Exception) {
        Logger.e(tag) { "API call failed: ${e::class.simpleName} - ${e.message}" }
        throw parseGeneralError(TaminErrorUriException(fallbackUri))
    }
}
