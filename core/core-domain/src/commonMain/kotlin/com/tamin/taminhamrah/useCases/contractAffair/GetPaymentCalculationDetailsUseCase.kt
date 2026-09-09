package com.tamin.taminhamrah.useCases.contractAffair

import com.tamin.taminhamrah.model.contractAffair.ContractPremiumType
import com.tamin.taminhamrah.model.contractAffair.PaymentCalculationRowDN
import com.tamin.taminhamrah.repository.contractAffair.ContractAffairRepository
import kotlinx.coroutines.flow.Flow

class GetPaymentCalculationDetailsUseCase(
    private val contractAffairRepository: ContractAffairRepository,
) {
    operator fun invoke(
        premiumType: ContractPremiumType,
        startDate: Long,
        endDate: Long,
    ): Flow<List<PaymentCalculationRowDN>> =
        contractAffairRepository.getPaymentCalculationDetails(premiumType, startDate, endDate)
}
