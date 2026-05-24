package com.tamin.taminhamrah.util

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun generateCodeVerifier(length: Int = 64): String {
    val bytes = randomBytes(length)
    return Base64.UrlSafe.encode(bytes).replace("=", "")
}

fun getCodeVerifierChallengeMethod(): String {
    return if (isSha256Supported()) "S256" else "plain"
}

@OptIn(ExperimentalEncodingApi::class)
fun deriveCodeChallenge(codeVerifier: String): String {
    return if (isSha256Supported()) {
        val digest = sha256(toIso88591Bytes(codeVerifier))
        Base64.UrlSafe.encode(digest).replace("=", "")
    } else {
        codeVerifier
    }
}


@OptIn(ExperimentalEncodingApi::class)
fun deriveCodeVerifierChallenge(codeVerifier: String): String? {
    return if (isSha256Supported()) {
        val digest = sha256(toIso88591Bytes(codeVerifier))
        Base64.UrlSafe.encode(digest).replace("=", "")
    } else {
        codeVerifier
    }
}

expect fun sha256(data: ByteArray): ByteArray
expect fun isSha256Supported(): Boolean
expect fun toIso88591Bytes(text: String): ByteArray
expect fun randomBytes(count: Int): ByteArray
