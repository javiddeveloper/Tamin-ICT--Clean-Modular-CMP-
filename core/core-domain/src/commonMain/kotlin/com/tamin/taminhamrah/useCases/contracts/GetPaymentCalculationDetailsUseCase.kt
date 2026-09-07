package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.ContractPremiumType
import com.tamin.taminhamrah.model.contracts.PaymentCalculationRowDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class GetPaymentCalculationDetailsUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(
        premiumType: ContractPremiumType,
        startDate: Long,
        endDate: Long,
    ): Flow<List<PaymentCalculationRowDN>> =
        contractsRepository.getPaymentCalculationDetails(premiumType, startDate, endDate)
}
