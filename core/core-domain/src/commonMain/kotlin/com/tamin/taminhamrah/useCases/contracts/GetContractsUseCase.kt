package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class GetContractsUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(page: Int = 1): Flow<PagedListDN<ContractDN>> {
        return contractsRepository.getContracts(page)
    }

    fun contractsByPremiumType(
        premiumTypeCode: String,
        page: Int = 1,
    ): Flow<PagedListDN<ContractDN>> =
        contractsRepository.getContractsByPremiumType(premiumTypeCode, page)
}
