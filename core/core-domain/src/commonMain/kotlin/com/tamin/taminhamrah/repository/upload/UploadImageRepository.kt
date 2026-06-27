package com.tamin.taminhamrah.repository.upload

import com.tamin.taminhamrah.model.upload.UploadImageRequestDN
import kotlinx.coroutines.flow.Flow

interface UploadImageRepository {
    fun uploadImage(request: UploadImageRequestDN): Flow<String>
}
