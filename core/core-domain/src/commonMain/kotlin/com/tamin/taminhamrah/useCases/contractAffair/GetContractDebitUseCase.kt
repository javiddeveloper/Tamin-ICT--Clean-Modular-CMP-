package com.tamin.taminhamrah.useCases.contractAffair

import com.tamin.taminhamrah.model.contractAffair.ContractDebitDN
import com.tamin.taminhamrah.model.contractAffair.ContractPremiumType
import com.tamin.taminhamrah.repository.contractAffair.ContractAffairRepository
import kotlinx.coroutines.flow.Flow

class GetContractDebitUseCase(
    private val contractAffairRepository: ContractAffairRepository,
) {
    operator fun invoke(premiumType: ContractPremiumType, month: Int): Flow<ContractDebitDN> =
        contractAffairRepository.getContractDebit(premiumType, month)
}
