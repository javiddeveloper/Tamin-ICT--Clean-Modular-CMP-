package com.tamin.taminhamrah.model.contracts

data class UploadImageRequestDN(
    val fileName: String,
    val bytes: ByteArray,
    val contentType: String = JPEG_CONTENT_TYPE,
    val description: String? = null,
) {
    companion object {
        const val JPEG_CONTENT_TYPE = "image/jpeg"
        const val MAX_FILE_SIZE_BYTES = 2 * 1024 * 1024
    }

    init {
        require(bytes.isNotEmpty()) { "Image bytes must not be empty" }
        require(bytes.size <= MAX_FILE_SIZE_BYTES) { "Image size must be at most 2 MB" }
        require(contentType.equals(JPEG_CONTENT_TYPE, ignoreCase = true)) {
            "Only JPEG images are supported"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as UploadImageRequestDN
        return fileName == other.fileName &&
            bytes.contentEquals(other.bytes) &&
            contentType == other.contentType &&
            description == other.description
    }

    override fun hashCode(): Int {
        var result = fileName.hashCode()
        result = 31 * result + bytes.contentHashCode()
        result = 31 * result + contentType.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }
}
