package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class GetFreelancePremiumRangeUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(params: FreelancePremiumRangeParams): Flow<FreelancePremiumRangeDN> =
        contractsRepository.getFreelancePremiumRange(params)
}
