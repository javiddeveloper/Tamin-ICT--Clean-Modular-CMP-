package com.tamin.taminhamrah.useCases.contractAffair

import com.tamin.taminhamrah.model.contractAffair.ContractLastPaymentDN
import com.tamin.taminhamrah.model.contractAffair.ContractPremiumType
import com.tamin.taminhamrah.repository.contractAffair.ContractAffairRepository
import kotlinx.coroutines.flow.Flow

class GetContractLastPaymentUseCase(
    private val contractAffairRepository: ContractAffairRepository,
) {
    operator fun invoke(premiumType: ContractPremiumType): Flow<ContractLastPaymentDN> =
        contractAffairRepository.getContractLastPayment(premiumType)
}
