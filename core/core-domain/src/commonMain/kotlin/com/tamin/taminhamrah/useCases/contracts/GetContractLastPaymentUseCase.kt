package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.ContractLastPaymentDN
import com.tamin.taminhamrah.model.contracts.ContractPremiumType
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class GetContractLastPaymentUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(premiumType: ContractPremiumType): Flow<ContractLastPaymentDN> =
        contractsRepository.getContractLastPayment(premiumType)
}
