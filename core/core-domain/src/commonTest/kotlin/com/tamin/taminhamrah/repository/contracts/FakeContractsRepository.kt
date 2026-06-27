package com.tamin.taminhamrah.repository.contracts

import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractParams
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeContractsRepository : ContractsRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")
    var contractsResult: List<ContractDN> = emptyList()
    var registrationInfoResult: RegistrationInfoDN? = null
    var branchesResult: List<BranchDN> = emptyList()
    var spcPremiumRatesResult: List<PremiumRateDN> = emptyList()
    var freelancePremiumRangeResult: FreelancePremiumRangeDN? = null
    var calculatedSalaryResult: Long? = null
    var makeContractResult: FreelanceContractResultDN? = null
    var lastMakeContractParams: FreelanceMakeContractParams? = null
    var lastQuery: ApiQueryParamDN? = null
    var lastBranchCityCode: String? = null
    var studentInsuranceContractsCalled = false

    var lastUploadImageRequest: UploadImageRequestDN? = null
    var uploadImageResult: String = "a4769aa8-b9af-4183-83b9-367dc9f52511"

    override fun getContracts(query: ApiQueryParamDN?): Flow<List<ContractDN>> = flow {
        lastQuery = query
        if (shouldThrowError) throw error
        emit(contractsResult)
    }

    var lastPremiumTypeCode: String? = null
    var freeJobWagesResult: List<FreeJobDN> = emptyList()
    var calculatedOptionalSalaryResult: Long? = null

    override fun getContractsByPremiumType(premiumTypeCode: String): Flow<List<ContractDN>> = flow {
        lastPremiumTypeCode = premiumTypeCode
        if (shouldThrowError) throw error
        emit(contractsResult)
    }

    override fun getStudentInsuranceContracts(): Flow<List<ContractDN>> = flow {
        studentInsuranceContractsCalled = true
        if (shouldThrowError) throw error
        emit(contractsResult)
    }

    override fun getRegistrationInfo(): Flow<RegistrationInfoDN> = flow {
        if (shouldThrowError) throw error
        emit(registrationInfoResult ?: RegistrationInfoDN(
            personalInfo = null,
            insuranceIdValidity = false,
            mobileNumber = null,
            insuranceId = null,
            lastContact = null,
        ))
    }

    override fun getBranches(cityCode: String): Flow<List<BranchDN>> = flow {
        lastBranchCityCode = cityCode
        if (shouldThrowError) throw error
        emit(branchesResult)
    }

    override fun getSpcPremiumRates(): Flow<List<PremiumRateDN>> = flow {
        if (shouldThrowError) throw error
        emit(spcPremiumRatesResult)
    }

    override fun getFreeJobWages(): Flow<List<FreeJobDN>> = flow {
        if (shouldThrowError) throw error
        emit(freeJobWagesResult)
    }

    override fun getFreelancePremiumRange(params: FreelancePremiumRangeParams): Flow<FreelancePremiumRangeDN> = flow {
        if (shouldThrowError) throw error
        emit(
            freelancePremiumRangeResult ?: FreelancePremiumRangeDN(
                paymentTabayi = 0L,
                lowPremium = 0L,
                history = 0,
                highPremium = 0L,
            ),
        )
    }

    override fun calculateFreelanceSalary(params: FreelanceCalculateSalaryParams): Flow<Long> = flow {
        if (shouldThrowError) throw error
        emit(calculatedSalaryResult ?: 0L)
    }

    override fun calculateOptionalSalary(premiumRateCode: String): Flow<Long> = flow {
        if (shouldThrowError) throw error
        emit(calculatedOptionalSalaryResult ?: 0L)
    }

    override fun makeFreelanceContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> = flow {
        lastMakeContractParams = params
        if (shouldThrowError) throw error
        emit(
            makeContractResult ?: FreelanceContractResultDN(
                contractNumber = null,
                contractDate = null,
            ),
        )
    }

    override fun uploadImage(request: UploadImageRequestDN): Flow<String> = flow {
        lastUploadImageRequest = request
        if (shouldThrowError) throw error
        emit(uploadImageResult)
    }
}
