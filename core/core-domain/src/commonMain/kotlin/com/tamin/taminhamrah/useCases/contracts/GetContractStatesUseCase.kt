package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.ContractStateDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class GetContractStatesUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(): Flow<List<ContractStateDN>> =
        contractsRepository.getContractStates()
}
