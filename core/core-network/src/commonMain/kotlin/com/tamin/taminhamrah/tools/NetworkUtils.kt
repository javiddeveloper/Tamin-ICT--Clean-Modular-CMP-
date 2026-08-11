package com.tamin.taminhamrah.tools

import co.touchlab.kermit.Logger
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException

/**
 * Checks if the string contains at least one Persian/Arabic character.
 * Used to determine if a server error message (like the 'reason' field)
 * contains human-readable Persian text that can be shown to the user.
 */
fun String.isPersian(): Boolean {
    val persianRegex = Regex("[\u0600-\u06FF\uFB50-\uFDFF\uFE70-\uFEFF]")
    return persianRegex.containsMatchIn(this)
}

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
        Logger.e(tag) { "API call failed: ${e::class.simpleName} - ${e.message}" }
        throw parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
    }
}
