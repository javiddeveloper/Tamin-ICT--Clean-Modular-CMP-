package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractParams
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class MakeFreelanceContractUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> =
        contractsRepository.makeFreelanceContract(params)
}
