package com.tamin.taminhamrah.data.crypto

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CC_SHA256
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH
import kotlin.random.Random

@OptIn(ExperimentalForeignApi::class)
actual fun sha256(data: ByteArray): ByteArray {
    val digest = ByteArray(CC_SHA256_DIGEST_LENGTH.convert())
    data.usePinned { pinned ->
        digest.usePinned { out ->
            CC_SHA256(pinned.addressOf(0), data.size.convert(), out.addressOf(0))
        }
    }
    return digest
}

actual fun isSha256Supported(): Boolean = true

actual fun toIso88591Bytes(text: String): ByteArray {
    return text.encodeToByteArray()
}

actual fun randomBytes(count: Int): ByteArray {
    val bytes = ByteArray(count)
    Random.Default.nextBytes(bytes)
    return bytes
}
