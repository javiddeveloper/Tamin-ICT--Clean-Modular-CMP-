package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class CheckInsurancePaymentStatusUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(systemType: String): Flow<Any?> =
        contractsRepository.checkInsurancePaymentStatus(systemType)
}
