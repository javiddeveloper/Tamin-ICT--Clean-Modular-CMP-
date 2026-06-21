package com.tamin.taminhamrah.repository.contracts

import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeContractsRepository : ContractsRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")
    var contractsResult: List<ContractDN> = emptyList()
    var lastQuery: ApiQueryParamDN? = null

    override fun getContracts(query: ApiQueryParamDN?): Flow<List<ContractDN>> = flow {
        lastQuery = query
        if (shouldThrowError) throw error
        emit(contractsResult)
    }
}
