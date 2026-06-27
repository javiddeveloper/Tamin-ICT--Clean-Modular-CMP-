package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class UploadImageUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(request: UploadImageRequestDN): Flow<String> =
        contractsRepository.uploadImage(request)
}
