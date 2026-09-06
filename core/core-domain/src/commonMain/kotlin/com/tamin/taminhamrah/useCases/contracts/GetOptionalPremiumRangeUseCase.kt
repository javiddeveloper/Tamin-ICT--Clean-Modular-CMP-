package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class GetOptionalPremiumRangeUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(): Flow<FreelancePremiumRangeDN> =
        contractsRepository.getOptionalPremiumRange()
}
