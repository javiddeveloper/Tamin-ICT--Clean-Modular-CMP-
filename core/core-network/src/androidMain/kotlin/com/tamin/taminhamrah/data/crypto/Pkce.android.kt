package template.core.base.common.crypto

import java.security.MessageDigest

actual fun sha256(data: ByteArray): ByteArray {
    val md = MessageDigest.getInstance("SHA-256")
    return md.digest(data)
}

actual fun isSha256Supported(): Boolean {
    return try {
        MessageDigest.getInstance("SHA-256")
        true
    } catch (e: Exception) {
        false
    }
}

actual fun toIso88591Bytes(text: String): ByteArray {
    return text.toByteArray(charset("ISO_8859_1"))
}

actual fun randomBytes(count: Int): ByteArray {
    val bytes = ByteArray(count)
    java.security.SecureRandom().nextBytes(bytes)
    return bytes
}
