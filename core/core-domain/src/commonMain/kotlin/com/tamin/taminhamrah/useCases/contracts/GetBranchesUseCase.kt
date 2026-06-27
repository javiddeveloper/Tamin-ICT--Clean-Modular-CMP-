package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class GetBranchesUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(cityCode: String): Flow<List<BranchDN>> =
        contractsRepository.getBranches(cityCode)
}
