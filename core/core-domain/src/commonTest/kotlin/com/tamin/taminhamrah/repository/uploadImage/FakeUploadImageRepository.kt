package com.tamin.taminhamrah.repository.uploadImage

import com.tamin.taminhamrah.model.upload.UploadImageRequestDN
import com.tamin.taminhamrah.repository.upload.UploadImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeUploadImageRepository : UploadImageRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("upload failed")
    var lastRequest: UploadImageRequestDN? = null
    var imageId: String = "a4769aa8-b9af-4183-83b9-367dc9f52511"

    override fun uploadImage(request: UploadImageRequestDN): Flow<String> = flow {
        lastRequest = request
        if (shouldThrowError) throw error
        emit(imageId)
    }
}
