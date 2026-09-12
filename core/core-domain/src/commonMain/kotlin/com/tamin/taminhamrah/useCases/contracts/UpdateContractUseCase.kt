package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractParams
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class UpdateContractUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(
        isOptionalInsurance: Boolean,
        params: FreelanceMakeContractParams,
    ): Flow<Unit> = if (isOptionalInsurance) {
        contractsRepository.updateOptionalContract(params.monthlyPremium)
    } else {
        contractsRepository.updateFreelanceContract(params)
    }
}
