package com.tamin.taminhamrah.useCases.contractAffair

import com.tamin.taminhamrah.model.contractAffair.ContractDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.contractAffair.ContractAffairRepository
import kotlinx.coroutines.flow.Flow

class GetContractsPageUseCase(
    private val contractAffairRepository: ContractAffairRepository,
) {
    operator fun invoke(query: ApiQueryParamDN): Flow<PageDN<ContractDN>> =
        contractAffairRepository.getContractsPage(query)
}
