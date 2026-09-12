package com.tamin.taminhamrah.repository.contracts

import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.FreelanceContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.OptionalContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractParams
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.InsurancePaymentDN
import com.tamin.taminhamrah.model.contracts.InsurancePaymentParamsDN
import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDN
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.util.PagedListDN
import kotlinx.coroutines.flow.Flow

interface ContractsRepository {
    fun getContracts(query: ApiQueryParamDN?): Flow<List<ContractDN>>
    fun getContractsByPremiumType(premiumTypeCode: String): Flow<List<ContractDN>>
    fun getStudentInsuranceContracts(): Flow<List<ContractDN>>
    fun getRegistrationInfo(): Flow<RegistrationInfoDN>
    fun getBranches(cityCode: String): Flow<List<BranchDN>>
    fun getSpcPremiumRates(): Flow<List<PremiumRateDN>>
    fun getFreeJobWages(page: Int = 1, searchQuery: String? = null): Flow<PagedListDN<FreeJobDN>>
    fun getFreelancePremiumRange(params: FreelancePremiumRangeParams): Flow<FreelancePremiumRangeDN>
    fun getOptionalPremiumRange(): Flow<FreelancePremiumRangeDN>
    fun checkRedCrossStatus(): Flow<String>
    fun checkMedicalStudent(): Flow<String>
    fun calculateFreelanceSalary(params: FreelanceCalculateSalaryParams): Flow<Long>
    fun calculateOptionalSalary(premiumRateCode: String): Flow<Long>
    fun makeFreelanceContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN>
    fun makeContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN>
    fun makeFreelanceContractByGuardian(params: FreelanceContractByGuardianParams): Flow<FreelanceContractResultDN>
    fun makeOptionalContractByGuardian(params: OptionalContractByGuardianParams): Flow<FreelanceContractResultDN>
    fun getInsurancePayment(params: InsurancePaymentParamsDN): Flow<InsurancePaymentDN>
    fun checkInsurancePaymentStatus(systemType: String): Flow<Any?>
    fun uploadImage(request: UploadImageRequestDN): Flow<String>
    fun saveContact(request: SaveContactRequestDN): Flow<Any?>
}
