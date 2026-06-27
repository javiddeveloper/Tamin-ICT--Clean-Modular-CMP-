package com.tamin.taminhamrah.useCases.upload

import com.tamin.taminhamrah.model.upload.UploadImageRequestDN
import com.tamin.taminhamrah.repository.upload.UploadImageRepository
import kotlinx.coroutines.flow.Flow

class UploadImageUseCase(
    private val uploadRepository: UploadImageRepository,
) {
    operator fun invoke(request: UploadImageRequestDN): Flow<String> =
        uploadRepository.uploadImage(request)
}
