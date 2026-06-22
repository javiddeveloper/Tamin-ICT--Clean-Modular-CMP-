package com.tamin.taminhamrah.dataSource.contracts

import com.tamin.taminhamrah.model.contracts.ContractDTO
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData

interface ContractsRemoteDataSource {
    suspend fun getContracts(query: ApiQueryParamDN): ListData<ContractDTO>
    suspend fun getRegistrationInfo(): RegistrationInfoDTO
}
