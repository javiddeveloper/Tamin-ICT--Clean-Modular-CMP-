package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.SaveContactRequestDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class SaveContactUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(request: SaveContactRequestDN): Flow<Any?> =
        contractsRepository.saveContact(request)
}
