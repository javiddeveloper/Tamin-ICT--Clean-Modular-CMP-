package com.tamin.taminhamrah.repository.contracts

import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow

interface ContractsRepository {
    fun getContracts(query: ApiQueryParamDN?): Flow<List<ContractDN>>
    fun getStudentInsuranceContracts(): Flow<List<ContractDN>>
    fun getRegistrationInfo(): Flow<RegistrationInfoDN>
    fun getBranches(cityCode: String): Flow<List<BranchDN>>
}
