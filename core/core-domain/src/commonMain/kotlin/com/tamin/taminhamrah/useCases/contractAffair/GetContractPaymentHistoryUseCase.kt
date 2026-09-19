package com.tamin.taminhamrah.useCases.contractAffair

import com.tamin.taminhamrah.model.contractAffair.ContractPaymentHistoryItemDN
import com.tamin.taminhamrah.repository.contractAffair.ContractAffairRepository
import kotlinx.coroutines.flow.Flow

class GetContractPaymentHistoryUseCase(
    private val contractAffairRepository: ContractAffairRepository,
) {
    operator fun invoke(contractNumber: String): Flow<List<ContractPaymentHistoryItemDN>> =
        contractAffairRepository.getContractPaymentHistory(contractNumber)
}
