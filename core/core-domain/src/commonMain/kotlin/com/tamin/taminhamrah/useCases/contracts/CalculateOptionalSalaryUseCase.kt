package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class CalculateOptionalSalaryUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(premiumRateCode: String): Flow<Long> =
        contractsRepository.calculateOptionalSalary(premiumRateCode)
}
