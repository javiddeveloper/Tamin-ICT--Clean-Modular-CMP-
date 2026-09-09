package com.tamin.taminhamrah.feature.pregnancyPay.fake

import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractParams
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.model.contracts.InsurancePaymentDN
import com.tamin.taminhamrah.model.contracts.InsurancePaymentParamsDN
import com.tamin.taminhamrah.model.contracts.OptionalContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDN
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Only [uploadImage] is exercised by [com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase]
 * — the rest of [ContractsRepository] is unrelated to pregnancy-pay and stubbed to satisfy the interface.
 */
class FakeContractsRepository : ContractsRepository {
    var lastUploadImageRequest: UploadImageRequestDN? = null
    var uploadImageResult: String = "uploaded-guid"
    var shouldThrowOnUpload = false
    var uploadError: Throwable = RuntimeException("upload failed")

    override fun uploadImage(request: UploadImageRequestDN): Flow<String> = flow {
        lastUploadImageRequest = request
        if (shouldThrowOnUpload) throw uploadError
        emit(uploadImageResult)
    }

    override fun getContracts(query: ApiQueryParamDN?): Flow<List<ContractDN>> = flow { emit(emptyList()) }
    override fun getContractsByPremiumType(premiumTypeCode: String): Flow<List<ContractDN>> =
        flow { emit(emptyList()) }
    override fun getStudentInsuranceContracts(): Flow<List<ContractDN>> = flow { emit(emptyList()) }
    override fun getRegistrationInfo(): Flow<RegistrationInfoDN> = flow {
        emit(
            RegistrationInfoDN(
                personalInfo = null,
                insuranceIdValidity = false,
                mobileNumber = null,
                insuranceId = null,
                lastContact = null,
            ),
        )
    }
    override fun getOptionalPremiumRange(): Flow<FreelancePremiumRangeDN> = flow {
        emit(FreelancePremiumRangeDN(paymentTabayi = 0L, lowPremium = 0L, history = 0, highPremium = 0L))
    }
    override fun checkRedCrossStatus(): Flow<String> = flow { emit("ok") }
    override fun checkMedicalStudent(): Flow<String> = flow { emit("ok14") }
    override fun getBranches(cityCode: String): Flow<List<BranchDN>> = flow { emit(emptyList()) }
    override fun getSpcPremiumRates(): Flow<List<PremiumRateDN>> = flow { emit(emptyList()) }
    override fun getFreeJobWages(page: Int, searchQuery: String?): Flow<PagedListDN<FreeJobDN>> =
        flow { emit(PagedListDN()) }
    override fun getFreelancePremiumRange(params: FreelancePremiumRangeParams): Flow<FreelancePremiumRangeDN> = flow {
        emit(FreelancePremiumRangeDN(paymentTabayi = 0L, lowPremium = 0L, history = 0, highPremium = 0L))
    }
    override fun calculateFreelanceSalary(params: FreelanceCalculateSalaryParams): Flow<Long> = flow { emit(0L) }
    override fun calculateOptionalSalary(premiumRateCode: String): Flow<Long> = flow { emit(0L) }
    override fun makeFreelanceContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> = flow {
        emit(FreelanceContractResultDN(contractNumber = null, contractDate = null))
    }
    override fun makeContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> = flow {
        emit(FreelanceContractResultDN(contractNumber = null, contractDate = null))
    }
    override fun makeFreelanceContractByGuardian(
        params: FreelanceContractByGuardianParams,
    ): Flow<FreelanceContractResultDN> = flow {
        emit(FreelanceContractResultDN(contractNumber = null, contractDate = null))
    }
    override fun makeOptionalContractByGuardian(
        params: OptionalContractByGuardianParams,
    ): Flow<FreelanceContractResultDN> = flow {
        emit(FreelanceContractResultDN(contractNumber = null, contractDate = null))
    }
    override fun getInsurancePayment(params: InsurancePaymentParamsDN): Flow<InsurancePaymentDN> = flow {
        emit(InsurancePaymentDN(paymentTicket = null, paymentUrl = null, responseMessage = null, succeed = null))
    }
    override fun checkInsurancePaymentStatus(systemType: String): Flow<Any?> = flow { emit(null) }
    override fun saveContact(request: SaveContactRequestDN): Flow<Any?> = flow { emit(null) }
}
