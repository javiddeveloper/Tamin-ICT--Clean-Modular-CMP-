/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/

package com.tamin.taminhamrah.tools

import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.HttpStatusErrorMapper
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * Common interface for DTOs that might carry an error message from the backend
 * inside the 'data' field (especially for 4xx/5xx responses).
 */
interface ErrorCarrier {
    val message: String?
    val cause: String? get() = null
}

/**
 * A single business-level problem reported by the backend, e.g.:
 * {"error_Code":9001,"error_Msg":"..."}
 * Seen mainly on the health-profile services, alongside a `hasError` flag
 * and a `status`/`family`/`reason` combo that doesn't fit the normal
 * 2xx/4xx/5xx ranges (e.g. status=801, family="System").
 */
@Serializable
data class ProblemDTO(
    @SerialName("error_Code") val errorCode: Int? = null,
    @SerialName("error_Msg") val errorMsg: String? = null
)

@Serializable
data class BaseDTO<out T>(
    @SerialName("status") val status: Int,
    @SerialName("family") val family: String,
    @SerialName("reason") val reason: String,
    @SerialName("data") val data: T? = null,
    @SerialName("hasError") val hasError: Boolean? = null,
    @SerialName("problems") val problems: List<ProblemDTO>? = null
) {
    /**
     * True when the backend flagged this response as failed via the
     * `hasError`/`problems` envelope, independent of whatever `status` says.
     * Some services (health profile in particular) use domain-specific status
     * codes like 801 that don't fall into the usual 2xx/4xx/5xx buckets, so
     * this must be checked *before* any range-based status check.
     */
    val hasProblems: Boolean
        get() = hasError == true || !problems.isNullOrEmpty()

    /**
     * All problem messages joined into one human-readable string, in order.
     * Null when there is nothing usable to show (falls back to [reason] at the call site).
     */
    val problemMessage: String?
        get() = problems
            ?.mapNotNull { it.errorMsg?.takeIf { msg -> msg.isNotBlank() } }
            ?.takeIf { it.isNotEmpty() }
            ?.joinToString(separator = "\n")
}

/**
 * Extracts data from BaseDTO. Throws exception if status is not 2xx or data is null.
 * Use this when you expect a specific DTO as data.
 */
fun <T> BaseDTO<T>.extractData(): T {
    println("BaseDTO: Extracting data from BaseDTO: status=$status, family=$family, reason=$reason, hasData=${data != null}, hasProblems=$hasProblems")

    return when {
        hasProblems -> throwProblemError()
        status in 200..299 && data != null -> {
            println("BaseDTO: Data extraction successful")
            data
        }
        else -> handleCommonErrors()
    }
}

/**
 * Extracts a success message from the response.
 * If data is a primitive (like String), it returns its content.
 * If data is null or an object, it returns the 'reason' field as the message.
 */
fun BaseDTO<JsonElement?>.extractMessage(): String {
    println("BaseDTO: Extracting message: status=$status, family=$family, reason=$reason, hasProblems=$hasProblems")

    return when {
        hasProblems -> throwProblemError()
        status in 200..299 -> {
            val msg = when (val d = data) {
                is JsonPrimitive -> d.contentOrNull
                is JsonObject -> d["message"]?.jsonPrimitive?.contentOrNull
                else -> null
            } ?: reason
            println("BaseDTO: Success message extracted: $msg")
            msg
        }
        else -> handleCommonErrors()
    }
}

/**
 * Handles the `hasError`/`problems` envelope. Carries the backend's own
 * (already localized) message through to ErrorParser instead of collapsing
 * it into a generic "something went wrong" string.
 */
private fun <T> BaseDTO<T>.throwProblemError(): Nothing {
    val firstProblem = problems?.firstOrNull()
    println("BaseDTO: Business error: family=$family reason=$reason problems=$problems")
    throw TaminErrorUriException(
        uri = ErrorUri.SERVER_PROBLEM,
        serverMessage = getServerMessage(),
        errorCode = firstProblem?.errorCode
    )
}

/**
 * Handles error statuses and throws appropriate TaminErrorUriException.
 */
private fun <T> BaseDTO<T>.handleCommonErrors(): Nothing {
    val mapped = HttpStatusErrorMapper.map(
        status = status,
        rawMessage = rawErrorText(),
        cause = errorCause()
    )
    println("BaseDTO: HTTP $status -> ${mapped.uri}: $reason")
    throw TaminErrorUriException(
        uri = mapped.uri,
        serverMessage = mapped.userMessage,
        navigateBack = mapped.navigateBack
    )
}

/**
 * Probes 'data', 'problems', and 'reason' for a user-facing backend message.
 * Priority: 1. data.message (if data is ErrorCarrier or JsonObject)
 *           2. problems envelope
 *           3. reason field
 */
private fun <T> BaseDTO<T>.rawErrorText(): String? {
    val fromData = when (val d = data) {
        is ErrorCarrier -> d.message
        is JsonObject -> d["message"]?.jsonPrimitive?.contentOrNull
        is JsonPrimitive -> d.contentOrNull
        else -> null
    }
    return fromData ?: problemMessage ?: reason
}

private fun <T> BaseDTO<T>.errorCause(): String? = when (val d = data) {
    is ErrorCarrier -> d.cause
    is JsonObject -> d["cause"]?.jsonPrimitive?.contentOrNull
    else -> null
}

/**
 * Same probe as [rawErrorText], but only keeps copy that looks like Arabic script.
 * Used by the `hasError`/`problems` envelope, which is already localized when present.
 */
private fun <T> BaseDTO<T>.getServerMessage(): String? =
    rawErrorText()?.takeIf { it.looksLikeArabicScript() }

/**
 * Outcome of a [BaseDTO] extraction that does not throw when the backend
 * responded with a non-empty `problems` list. [data] is non-null on success;
 * when [problems] is non-empty [data] is null and the caller decides how to
 * surface the individual business/validation problems instead of collapsing
 * them into a single generic exception.
 */
data class ApiOutcome<out T>(
    val data: T?,
    val problems: List<ProblemDTO> = emptyList()
)

/**
 * Like [extractData], but when the backend sent a non-empty `problems` list
 * this returns them via [ApiOutcome] instead of throwing. Every other failure
 * mode (network errors, generic status-range client/server errors, or
 * `hasError=true` with no `problems` detail) still throws exactly like
 * [extractData], since there is nothing structured to hand back in those cases.
 */
fun <T> BaseDTO<T>.extractDataOrProblems(): ApiOutcome<T> {
    if (!problems.isNullOrEmpty()) {
        println("BaseDTO: Returning ${problems.size} problem(s) instead of throwing: $problems")
        return ApiOutcome(data = null, problems = problems)
    }
    return ApiOutcome(data = extractData(), problems = emptyList())
}

/**
 * [extractMessageOrProblems] is the non-throwing counterpart of [extractMessage],
 * used by the same "bare success message" endpoints (see [extractMessage] docs).
 */
fun BaseDTO<JsonElement?>.extractMessageOrProblems(): ApiOutcome<String> {
    if (!problems.isNullOrEmpty()) {
        println("BaseDTO: Returning ${problems.size} problem(s) instead of throwing: $problems")
        return ApiOutcome(data = null, problems = problems)
    }
    return ApiOutcome(data = extractMessage(), problems = emptyList())
}
