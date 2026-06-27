package com.tamin.taminhamrah.model.upload

data class UploadImageRequestDN(
    val fileName: String,
    val bytes: ByteArray,
    val contentType: String = "image/jpeg",
    val description: String? = null,
)
