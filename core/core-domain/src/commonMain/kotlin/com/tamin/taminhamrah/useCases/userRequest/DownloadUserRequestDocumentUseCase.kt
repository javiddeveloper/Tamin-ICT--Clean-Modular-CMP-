package com.tamin.taminhamrah.useCases.userRequest

import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository

class DownloadUserRequestDocumentUseCase(
    private val repository: UserRequestRepository
) {
    suspend operator fun invoke(guid: String): String {
        return repository.downloadUserRequestDocument(guid)
    }
}
