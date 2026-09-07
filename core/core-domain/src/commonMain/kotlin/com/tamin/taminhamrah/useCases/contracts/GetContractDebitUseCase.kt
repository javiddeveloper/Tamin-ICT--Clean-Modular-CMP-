package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.ContractDebitDN
import com.tamin.taminhamrah.model.contracts.ContractPremiumType
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class GetContractDebitUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(premiumType: ContractPremiumType, month: Int): Flow<ContractDebitDN> =
        contractsRepository.getContractDebit(premiumType, month)
}
