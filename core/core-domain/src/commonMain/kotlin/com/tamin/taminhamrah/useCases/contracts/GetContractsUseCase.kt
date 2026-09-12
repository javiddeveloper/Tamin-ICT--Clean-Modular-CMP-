package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class GetContractsUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(query: ApiQueryParamDN? = null): Flow<List<ContractDN>> {
        return contractsRepository.getContracts(query)
    }

    fun contractsByPremiumType(premiumTypeCode: String): Flow<List<ContractDN>> =
        contractsRepository.getContractsByPremiumType(premiumTypeCode)
}
