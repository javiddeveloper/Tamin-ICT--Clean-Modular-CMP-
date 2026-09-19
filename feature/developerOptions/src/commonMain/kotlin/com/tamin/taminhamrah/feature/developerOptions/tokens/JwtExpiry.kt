package com.tamin.taminhamrah.feature.developerOptions.tokens

import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * What the token screen can say about how long a token has left.
 *
 * [Unknown] covers anything that is not a readable JWT — including the tokens some services hand
 * back as opaque strings — so an unreadable token is reported as such rather than as expired.
 */
sealed interface JwtExpiry {
    data object Unknown : JwtExpiry
    data class Valid(val remaining: Duration) : JwtExpiry
    data class Expired(val since: Duration) : JwtExpiry
}

private val json = Json { ignoreUnknownKeys = true }

/**
 * Reads the `exp` claim out of a JWT **without verifying the signature** — this only ever drives
 * a debug screen's "expired / N minutes left" line, never an authorization decision.
 */
@OptIn(ExperimentalEncodingApi::class)
fun readJwtExpiry(token: String?): JwtExpiry {
    val payload = token?.split(".")?.takeIf { it.size >= 2 }?.get(1) ?: return JwtExpiry.Unknown

    val expiresAtSeconds = try {
        // JWTs are base64url without padding; the decoder wants the padding back.
        val padded = payload.padEnd(payload.length + (4 - payload.length % 4) % 4, '=')
        val claims = json.parseToJsonElement(Base64.UrlSafe.decode(padded).decodeToString())
        claims.jsonObject["exp"]?.jsonPrimitive?.content?.toLongOrNull()
    } catch (_: Exception) {
        null
    } ?: return JwtExpiry.Unknown

    val delta = expiresAtSeconds.seconds - Clock.System.now().epochSeconds.seconds
    return if (delta.isNegative()) JwtExpiry.Expired(since = -delta) else JwtExpiry.Valid(remaining = delta)
}
