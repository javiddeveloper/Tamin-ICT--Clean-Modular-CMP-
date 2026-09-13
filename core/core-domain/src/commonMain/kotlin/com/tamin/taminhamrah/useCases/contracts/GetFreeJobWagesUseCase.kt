package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class GetFreeJobWagesUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(
        page: Int = 1,
        searchQuery: String? = null,
    ): Flow<PagedListDN<FreeJobDN>> = contractsRepository.getFreeJobWages(page, searchQuery)
}
