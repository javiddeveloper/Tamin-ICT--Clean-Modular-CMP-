package com.tamin.taminhamrah.dataSource.contracts

import com.tamin.taminhamrah.model.contracts.BranchDTO
import com.tamin.taminhamrah.model.contracts.ContractDTO
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDTO
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractRequestDTO
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDTO
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.model.contracts.PremiumRateDTO
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData

interface ContractsRemoteDataSource {
    suspend fun getContracts(query: ApiQueryParamDN): ListData<ContractDTO>
    suspend fun getRegistrationInfo(): RegistrationInfoDTO
    suspend fun getBranches(query: ApiQueryParamDN): ListData<BranchDTO>
    suspend fun getSpcPremiumRates(): ListData<PremiumRateDTO>
    suspend fun getFreelancePremiumRange(params: FreelancePremiumRangeParams): FreelancePremiumRangeDTO
    suspend fun calculateFreelanceSalary(params: FreelanceCalculateSalaryParams): Long
    suspend fun makeFreelanceContract(
        monthlyPremium: Long,
        request: FreelanceMakeContractRequestDTO,
    ): FreelanceContractResultDTO
}
