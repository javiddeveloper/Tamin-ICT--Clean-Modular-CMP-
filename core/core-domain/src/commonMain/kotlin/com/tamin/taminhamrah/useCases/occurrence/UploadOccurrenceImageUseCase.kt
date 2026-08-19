package com.tamin.taminhamrah.useCases.occurrence

import com.tamin.taminhamrah.repository.occurrence.OccurrenceRepository

class UploadOccurrenceImageUseCase(
    private val repository: OccurrenceRepository
) {
    suspend operator fun invoke(fileName: String, fileBytes: ByteArray): String =
        repository.uploadImage(fileName, fileBytes)
}
