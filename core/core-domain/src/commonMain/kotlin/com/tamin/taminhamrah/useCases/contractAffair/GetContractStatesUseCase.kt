package com.tamin.taminhamrah.useCases.contractAffair

import com.tamin.taminhamrah.model.contractAffair.ContractStateDN
import com.tamin.taminhamrah.repository.contractAffair.ContractAffairRepository
import kotlinx.coroutines.flow.Flow

class GetContractStatesUseCase(
    private val contractAffairRepository: ContractAffairRepository,
) {
    operator fun invoke(): Flow<List<ContractStateDN>> =
        contractAffairRepository.getContractStates()
}
