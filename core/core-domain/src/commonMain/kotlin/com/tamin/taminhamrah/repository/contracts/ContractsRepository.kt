package com.tamin.taminhamrah.repository.contracts

import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractParams
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDN
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow

interface ContractsRepository {
    fun getContracts(query: ApiQueryParamDN?): Flow<List<ContractDN>>
    fun getContractsByPremiumType(premiumTypeCode: String): Flow<List<ContractDN>>
    fun getStudentInsuranceContracts(): Flow<List<ContractDN>>
    fun getRegistrationInfo(): Flow<RegistrationInfoDN>
    fun getBranches(cityCode: String): Flow<List<BranchDN>>
    fun getSpcPremiumRates(): Flow<List<PremiumRateDN>>
    fun getFreeJobWages(): Flow<List<FreeJobDN>>
    fun getFreelancePremiumRange(params: FreelancePremiumRangeParams): Flow<FreelancePremiumRangeDN>
    fun calculateFreelanceSalary(params: FreelanceCalculateSalaryParams): Flow<Long>
    fun calculateOptionalSalary(premiumRateCode: String): Flow<Long>
    fun makeFreelanceContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN>
    fun uploadImage(request: UploadImageRequestDN): Flow<String>
    fun saveContact(request: SaveContactRequestDN): Flow<Any?>
}
