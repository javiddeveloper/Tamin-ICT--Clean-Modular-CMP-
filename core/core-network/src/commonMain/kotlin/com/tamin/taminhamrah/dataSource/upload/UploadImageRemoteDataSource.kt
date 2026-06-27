package com.tamin.taminhamrah.dataSource.upload

import com.tamin.taminhamrah.model.upload.UploadImageRequestDN

interface UploadImageRemoteDataSource {
    suspend fun uploadImage(request: UploadImageRequestDN): String?
}
