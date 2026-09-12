package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.OptionalContractByGuardianParams
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class UpdateOptionalContractByGuardianUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(params: OptionalContractByGuardianParams): Flow<Unit> =
        contractsRepository.updateOptionalContractByGuardian(params)
}
