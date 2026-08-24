package com.tamin.taminhamrah.tools.errorHandling

import com.tamin.taminhamrah.tools.looksLikeArabicScript

/**
 * Some legacy Tamin endpoints answer failed requests with a bare Persian string
 * instead of the usual BaseDTO JSON envelope. When that happens, ContentNegotiation
 * cannot deserialize the body and the caller would otherwise fall back to a generic
 * connection error. This helper detects that shape and maps it through
 * [HttpStatusErrorMapper] so [ErrorParser] can show the backend copy verbatim.
 */
internal fun plainTextErrorFromHttpBody(status: Int, bodyText: String): TaminErrorUriException? {
    val trimmed = bodyText.trim()
    if (trimmed.isEmpty()) return null
    if (trimmed.startsWith("{") || trimmed.startsWith("[")) return null
    if (!trimmed.looksLikeArabicScript()) return null

    val mapped = HttpStatusErrorMapper.map(
        status = status,
        rawMessage = trimmed,
        cause = null,
    )
    return TaminErrorUriException(
        uri = mapped.uri,
        serverMessage = mapped.userMessage,
        navigateBack = mapped.navigateBack,
    )
}
