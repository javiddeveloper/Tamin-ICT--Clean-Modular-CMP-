package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.ContractPaymentHistoryItemDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class GetContractPaymentHistoryUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(contractNumber: String): Flow<List<ContractPaymentHistoryItemDN>> =
        contractsRepository.getContractPaymentHistory(contractNumber)
}
