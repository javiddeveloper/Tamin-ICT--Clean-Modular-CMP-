package com.tamin.taminhamrah.useCases.addDependent

import com.tamin.taminhamrah.model.addDependent.UploadImageDN
import com.tamin.taminhamrah.repository.AddDependentRepository
import kotlinx.coroutines.flow.Flow

class UploadDependentImageUseCase(
    private val repository: AddDependentRepository
) {
    operator fun invoke(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String
    ): Flow<UploadImageDN> = repository.uploadImage(
        imageBytes = imageBytes,
        fileName = fileName,
        mimeType = mimeType
    )
}
