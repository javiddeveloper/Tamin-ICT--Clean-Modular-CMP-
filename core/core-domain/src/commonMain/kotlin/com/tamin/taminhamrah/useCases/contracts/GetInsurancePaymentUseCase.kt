package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.InsurancePaymentDN
import com.tamin.taminhamrah.model.contracts.InsurancePaymentParamsDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class GetInsurancePaymentUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(params: InsurancePaymentParamsDN): Flow<InsurancePaymentDN> =
        contractsRepository.getInsurancePayment(params)
}
