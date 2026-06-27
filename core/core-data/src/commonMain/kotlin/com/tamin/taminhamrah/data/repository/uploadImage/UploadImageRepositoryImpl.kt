package com.tamin.taminhamrah.data.repository.uploadImage

import com.tamin.taminhamrah.dataSource.upload.UploadImageRemoteDataSource
import com.tamin.taminhamrah.model.upload.UploadImageRequestDN
import com.tamin.taminhamrah.repository.upload.UploadImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UploadImageRepositoryImpl(
    private val uploadRemoteDataSource: UploadImageRemoteDataSource,
) : UploadImageRepository {
    override fun uploadImage(request: UploadImageRequestDN): Flow<String> = flow {
        val imageId = uploadRemoteDataSource.uploadImage(request)
        imageId?.let { emit(it) }
    }
}
