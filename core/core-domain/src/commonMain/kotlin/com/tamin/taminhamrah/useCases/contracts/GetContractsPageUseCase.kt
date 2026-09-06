package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class GetContractsPageUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(query: ApiQueryParamDN): Flow<PageDN<ContractDN>> =
        contractsRepository.getContractsPage(query)
}
