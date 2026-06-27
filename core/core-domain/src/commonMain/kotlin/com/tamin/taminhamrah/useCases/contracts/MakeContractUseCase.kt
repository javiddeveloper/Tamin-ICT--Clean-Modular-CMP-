package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractParams
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class MakeContractUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(
        isOptionalInsurance: Boolean,
        params: FreelanceMakeContractParams,
    ): Flow<FreelanceContractResultDN> = if (isOptionalInsurance) {
        contractsRepository.makeContract(params)
    } else {
        contractsRepository.makeFreelanceContract(params)
    }
}
