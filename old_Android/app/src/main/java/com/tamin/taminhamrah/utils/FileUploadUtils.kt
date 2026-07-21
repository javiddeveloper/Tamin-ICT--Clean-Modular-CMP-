package com.tamin.taminhamrah.utils

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object FileUploadUtils {

    fun getFileBody(path: String): MultipartBody.Part {
        val file = File(path)

        val requestFile = file.asRequestBody(
            "audio/wav".toMediaType()
        )

        return MultipartBody.Part.createFormData(
            "file",
            file.name,
            requestFile
        )
    }
}