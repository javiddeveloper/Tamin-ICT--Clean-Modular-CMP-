package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class CalculateFreelanceSalaryUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(params: FreelanceCalculateSalaryParams): Flow<Long> =
        contractsRepository.calculateFreelanceSalary(params)
}
