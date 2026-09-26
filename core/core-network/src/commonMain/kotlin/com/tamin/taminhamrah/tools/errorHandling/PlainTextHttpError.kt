package com.tamin.taminhamrah.tools.errorHandling

import com.tamin.taminhamrah.model.error.ErrorDataEnvelopeDTO
import com.tamin.taminhamrah.model.error.ReplyEnvelopeDTO
import com.tamin.taminhamrah.model.error.TextDataEnvelopeDTO
import com.tamin.taminhamrah.model.error.ViolationDTO
import com.tamin.taminhamrah.model.error.ViolationsEnvelopeDTO
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json

/**
 * The failure a non-2xx body describes, unless it is a Tamin envelope — that one is read later,
 * where the expected type is known (see `LenientReplyConverter`).
 *
 * Anything else — a bare Persian sentence, a backend code, an HTML gateway page, English text, a
 * framework's `{timestamp,status,error,path}`, nothing at all — could only fail JSON decoding and
 * surface as a generic error. The old app chose the copy by HTTP status for all of these, keeping
 * the server's words when they were Persian; so does this.
 */
internal fun plainTextErrorFromHttpBody(status: Int, bodyText: String): TaminErrorUriException? =
    if (taminEnvelopeOrNull(bodyText) != null) null else errorFromHttpBody(status, bodyText)

/**
 * The failure any non-2xx body describes, read the way the old app's `getErrorResult` read it:
 * an envelope's `data.message` (and `data.cause`), its validation `propertyViolations`, a bare
 * string `data`, its `problems`, else `reason`; any other body is the message itself.
 * [HttpStatusErrorMapper] turns that into the user's copy for the status.
 */
internal fun errorFromHttpBody(status: Int, bodyText: String): TaminErrorUriException {
    val trimmed = bodyText.trim()
    val envelope = decodeOrNull(ReplyEnvelopeDTO.serializer(), trimmed)
    val (message, cause) = when {
        envelope != null -> envelopeErrorText(trimmed).let { (message, cause) ->
            (message ?: envelope.message ?: envelope.reason) to cause
        }
        trimmed.startsWith("[") -> null to null
        else -> trimmed.ifEmpty { null } to null
    }
    val mapped = HttpStatusErrorMapper.map(status = status, rawMessage = message, cause = cause)
    return TaminErrorUriException(
        uri = mapped.uri,
        serverMessage = mapped.userMessage,
        navigateBack = mapped.navigateBack,
    )
}

/**
 * `(message, cause)` a failed reply carries in `data` or `problems`, without the `reason` fallback —
 * the caller decides whether English `reason` text is worth showing.
 */
internal fun envelopeErrorText(bodyText: String): Pair<String?, String?> {
    val data = decodeOrNull(ErrorDataEnvelopeDTO.serializer(), bodyText)?.data
    val message = data?.message
        ?: decodeOrNull(ViolationsEnvelopeDTO.serializer(), bodyText)?.data?.let(::violationMessages)
        ?: decodeOrNull(TextDataEnvelopeDTO.serializer(), bodyText)?.data
        ?: decodeOrNull(ReplyEnvelopeDTO.serializer(), bodyText)?.problems
            ?.mapNotNull { it.errorMsg?.takeIf(String::isNotBlank) }
            ?.takeIf { it.isNotEmpty() }
            ?.joinToString("\n")
    return message?.takeIf { it.isNotBlank() } to data?.cause
}

/** A Tamin reply envelope — `status` plus `family`/`reason` — or null for any other body. */
internal fun taminEnvelopeOrNull(bodyText: String): ReplyEnvelopeDTO? =
    decodeOrNull(ReplyEnvelopeDTO.serializer(), bodyText.trim())
        ?.takeIf { it.status != null && (it.family != null || it.reason != null) }

/** An envelope whose `status` says the call failed. */
internal val ReplyEnvelopeDTO.isFailed: Boolean
    get() = status != null && status !in 200..299

/** First message per field, one per line — the old app's reading of `propertyViolations`. */
private fun violationMessages(violations: List<ViolationDTO>): String? =
    violations.firstOrNull()?.propertyViolations
        ?.values
        ?.mapNotNull { it.firstOrNull()?.takeIf(String::isNotBlank) }
        ?.takeIf { it.isNotEmpty() }
        ?.joinToString("\n")

private fun <T> decodeOrNull(strategy: DeserializationStrategy<T>, text: String): T? =
    if (!text.startsWith("{")) null else runCatching { errorJson.decodeFromString(strategy, text) }.getOrNull()

private val errorJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
}
