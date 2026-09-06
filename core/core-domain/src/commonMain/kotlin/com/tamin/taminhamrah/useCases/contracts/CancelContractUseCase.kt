package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.CancelContractParamsDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class CancelContractUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(params: CancelContractParamsDN): Flow<Unit> =
        contractsRepository.cancelContract(params)
}
