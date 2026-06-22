package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class GetSpcPremiumRatesUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(): Flow<List<PremiumRateDN>> =
        contractsRepository.getSpcPremiumRates()
}
