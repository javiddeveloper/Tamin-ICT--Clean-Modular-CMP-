package com.tamin.taminhamrah.repository.contracts

import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.ContractPremiumTypeCode
import com.tamin.taminhamrah.model.contracts.FreelanceContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.OptionalContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractParams
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.model.contracts.InsurancePaymentDN
import com.tamin.taminhamrah.model.contracts.InsurancePaymentParamsDN
import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDN
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.util.PagedListDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

class FakeContractsRepository : ContractsRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")
    var contractsResult: List<ContractDN> = emptyList()
    var contractsTotal: Int = 0
    var registrationInfoResult: RegistrationInfoDN? = null
    var branchesResult: List<BranchDN> = emptyList()
    var branchesTotal: Int = 0
    var spcPremiumRatesResult: List<PremiumRateDN> = emptyList()
    var freelancePremiumRangeResult: FreelancePremiumRangeDN? = null
    var calculatedSalaryResult: Long? = null
    var makeContractResult: FreelanceContractResultDN? = null
    var lastMakeContractParams: FreelanceMakeContractParams? = null
    var makeContractCalled = false
    var makeFreelanceContractCalled = false
    var lastMakeFreelanceContractByGuardianParams: FreelanceContractByGuardianParams? = null
    var makeFreelanceContractByGuardianCalled = false
    var lastMakeOptionalContractByGuardianParams: OptionalContractByGuardianParams? = null
    var makeOptionalContractByGuardianCalled = false
    var lastInsurancePaymentParams: InsurancePaymentParamsDN? = null
    var insurancePaymentResult: InsurancePaymentDN? = null
    var lastPaymentStatusSystemType: String? = null
    var paymentStatusResult: JsonElement? = JsonPrimitive(true)
    var lastContractsPage: Int? = null
    var lastBranchCityCode: String? = null
    var lastBranchesPage: Int? = null

    var lastUploadImageRequest: UploadImageRequestDN? = null
    var uploadImageResult: String = "a4769aa8-b9af-4183-83b9-367dc9f52511"

    var lastSaveContactRequest: SaveContactRequestDN? = null
    var saveContactResult: Any? = null

    override fun getContracts(page: Int): Flow<PagedListDN<ContractDN>> = flow {
        lastContractsPage = page
        if (shouldThrowError) throw error
        emit(
            PagedListDN(
                items = contractsResult,
                total = contractsTotal.coerceAtLeast(contractsResult.size),
            ),
        )
    }

    var lastPremiumTypeCode: String? = null
    var freeJobWagesResult: List<FreeJobDN> = emptyList()
    var freeJobWagesTotal: Int = 0
    var lastFreeJobWagesPage: Int? = null
    var lastFreeJobWagesSearchQuery: String? = null
    var calculatedOptionalSalaryResult: Long? = null
    var lastPremiumRateCode: String? = null
    var lastFreelanceCalculateParams: FreelanceCalculateSalaryParams? = null
    var lastFreelancePremiumRangeParams: FreelancePremiumRangeParams? = null

    override fun getContractsByPremiumType(
        premiumTypeCode: String,
        page: Int,
    ): Flow<PagedListDN<ContractDN>> = flow {
        lastPremiumTypeCode = premiumTypeCode
        lastContractsPage = page
        if (shouldThrowError) throw error
        emit(
            PagedListDN(
                items = contractsResult,
                total = contractsTotal.coerceAtLeast(contractsResult.size),
            ),
        )
    }

    override fun getStudentInsuranceContracts(page: Int): Flow<PagedListDN<ContractDN>> =
        getContractsByPremiumType(ContractPremiumTypeCode.STUDENT, page)

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

    override fun getBranches(cityCode: String, page: Int): Flow<PagedListDN<BranchDN>> = flow {
        lastBranchCityCode = cityCode
        lastBranchesPage = page
        if (shouldThrowError) throw error
        emit(
            PagedListDN(
                items = branchesResult,
                total = branchesTotal.coerceAtLeast(branchesResult.size),
            ),
        )
    }

    override fun getSpcPremiumRates(): Flow<List<PremiumRateDN>> = flow {
        if (shouldThrowError) throw error
        emit(spcPremiumRatesResult)
    }

    override fun getFreeJobWages(page: Int, searchQuery: String?): Flow<PagedListDN<FreeJobDN>> = flow {
        lastFreeJobWagesPage = page
        lastFreeJobWagesSearchQuery = searchQuery
        if (shouldThrowError) throw error
        emit(PagedListDN(items = freeJobWagesResult, total = freeJobWagesTotal.coerceAtLeast(freeJobWagesResult.size)))
    }

    override fun getFreelancePremiumRange(params: FreelancePremiumRangeParams): Flow<FreelancePremiumRangeDN> = flow {
        lastFreelancePremiumRangeParams = params
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

    override fun getOptionalPremiumRange(): Flow<FreelancePremiumRangeDN> = flow {
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

    var redCrossStatusResult: String = "ok"
    var medicalStudentStatusResult: String = "ok14"

    override fun checkRedCrossStatus(): Flow<String> = flow {
        if (shouldThrowError) throw error
        emit(redCrossStatusResult)
    }

    override fun checkMedicalStudent(): Flow<String> = flow {
        if (shouldThrowError) throw error
        emit(medicalStudentStatusResult)
    }

    override fun calculateFreelanceSalary(params: FreelanceCalculateSalaryParams): Flow<Long> = flow {
        lastFreelanceCalculateParams = params
        if (shouldThrowError) throw error
        emit(calculatedSalaryResult ?: 0L)
    }

    override fun calculateOptionalSalary(premiumRateCode: String): Flow<Long> = flow {
        lastPremiumRateCode = premiumRateCode
        if (shouldThrowError) throw error
        emit(calculatedOptionalSalaryResult ?: 0L)
    }

    override fun makeFreelanceContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> = flow {
        lastMakeContractParams = params
        makeFreelanceContractCalled = true
        makeContractCalled = false
        if (shouldThrowError) throw error
        emit(
            makeContractResult ?: FreelanceContractResultDN(
                contractNumber = null,
                contractDate = null,
            ),
        )
    }

    override fun makeContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> = flow {
        lastMakeContractParams = params
        makeContractCalled = true
        makeFreelanceContractCalled = false
        if (shouldThrowError) throw error
        emit(
            makeContractResult ?: FreelanceContractResultDN(
                contractNumber = null,
                contractDate = null,
            ),
        )
    }

    override fun makeFreelanceContractByGuardian(
        params: FreelanceContractByGuardianParams,
    ): Flow<FreelanceContractResultDN> = flow {
        lastMakeFreelanceContractByGuardianParams = params
        makeFreelanceContractByGuardianCalled = true
        if (shouldThrowError) throw error
        emit(
            makeContractResult ?: FreelanceContractResultDN(
                contractNumber = null,
                contractDate = null,
            ),
        )
    }

    override fun makeOptionalContractByGuardian(
        params: OptionalContractByGuardianParams,
    ): Flow<FreelanceContractResultDN> = flow {
        lastMakeOptionalContractByGuardianParams = params
        makeOptionalContractByGuardianCalled = true
        if (shouldThrowError) throw error
        emit(
            makeContractResult ?: FreelanceContractResultDN(
                contractNumber = null,
                contractDate = null,
            ),
        )
    }

    var lastUpdateFreelanceContractParams: FreelanceMakeContractParams? = null
    var updateFreelanceContractCalled = false
    var lastUpdateOptionalPremium: Long? = null
    var updateOptionalContractCalled = false
    var lastUpdateFreelanceContractByGuardianParams: FreelanceContractByGuardianParams? = null
    var updateFreelanceContractByGuardianCalled = false
    var lastUpdateOptionalContractByGuardianParams: OptionalContractByGuardianParams? = null
    var updateOptionalContractByGuardianCalled = false

    override fun updateFreelanceContract(params: FreelanceMakeContractParams): Flow<Unit> = flow {
        lastUpdateFreelanceContractParams = params
        updateFreelanceContractCalled = true
        if (shouldThrowError) throw error
        emit(Unit)
    }

    override fun updateOptionalContract(premium: Long): Flow<Unit> = flow {
        lastUpdateOptionalPremium = premium
        updateOptionalContractCalled = true
        if (shouldThrowError) throw error
        emit(Unit)
    }

    override fun updateFreelanceContractByGuardian(
        params: FreelanceContractByGuardianParams,
    ): Flow<Unit> = flow {
        lastUpdateFreelanceContractByGuardianParams = params
        updateFreelanceContractByGuardianCalled = true
        if (shouldThrowError) throw error
        emit(Unit)
    }

    override fun updateOptionalContractByGuardian(
        params: OptionalContractByGuardianParams,
    ): Flow<Unit> = flow {
        lastUpdateOptionalContractByGuardianParams = params
        updateOptionalContractByGuardianCalled = true
        if (shouldThrowError) throw error
        emit(Unit)
    }

    override fun getInsurancePayment(params: InsurancePaymentParamsDN): Flow<InsurancePaymentDN> = flow {
        lastInsurancePaymentParams = params
        if (shouldThrowError) throw error
        emit(
            insurancePaymentResult ?: InsurancePaymentDN(
                paymentTicket = null,
                paymentUrl = null,
                responseMessage = null,
                succeed = null,
            ),
        )
    }

    override fun checkInsurancePaymentStatus(systemType: String): Flow<Any?> = flow {
        lastPaymentStatusSystemType = systemType
        if (shouldThrowError) throw error
        emit(paymentStatusResult)
    }

    override fun uploadImage(request: UploadImageRequestDN): Flow<String> = flow {
        lastUploadImageRequest = request
        if (shouldThrowError) throw error
        emit(uploadImageResult)
    }

    override fun saveContact(request: SaveContactRequestDN): Flow<Any?> = flow {
        lastSaveContactRequest = request
        if (shouldThrowError) throw error
        emit(saveContactResult)
    }
}
