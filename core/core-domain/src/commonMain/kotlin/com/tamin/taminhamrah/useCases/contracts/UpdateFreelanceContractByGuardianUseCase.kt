package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.FreelanceContractByGuardianParams
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class UpdateFreelanceContractByGuardianUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(params: FreelanceContractByGuardianParams): Flow<Unit> =
        contractsRepository.updateFreelanceContractByGuardian(params)
}
