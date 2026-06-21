package com.tamin.taminhamrah.repository.contracts

import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow

interface ContractsRepository {
    fun getContracts(query: ApiQueryParamDN?): Flow<List<ContractDN>>
}
