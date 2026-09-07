package com.tamin.taminhamrah.feature.pensionSurvivor.fake

import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.CancelContractParamsDN
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.ContractPaymentHistoryItemDN
import com.tamin.taminhamrah.model.contracts.ContractPremiumType
import com.tamin.taminhamrah.model.contracts.ContractStateDN
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
import com.tamin.taminhamrah.model.contracts.ContractDebitDN
import com.tamin.taminhamrah.model.contracts.ContractLastPaymentDN
import com.tamin.taminhamrah.model.contracts.PaymentCalculationRowDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDN
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeContractsRepository : ContractsRepository {
    var lastUploadImageRequest: UploadImageRequestDN? = null
    var uploadImageResult: String = "uploaded-guid"

    override fun uploadImage(request: UploadImageRequestDN): Flow<String> = flow {
        lastUploadImageRequest = request
        emit(uploadImageResult)
    }

    override fun getContracts(query: ApiQueryParamDN?): Flow<List<ContractDN>> = flow { emit(emptyList()) }
    override fun getContractsByPremiumType(premiumTypeCode: String): Flow<List<ContractDN>> = flow { emit(emptyList()) }
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
    override fun getBranches(cityCode: String): Flow<List<BranchDN>> = flow { emit(emptyList()) }
    override fun getSpcPremiumRates(): Flow<List<PremiumRateDN>> = flow { emit(emptyList()) }
    override fun getFreeJobWages(): Flow<List<FreeJobDN>> = flow { emit(emptyList()) }
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
    override fun getContractsPage(query: ApiQueryParamDN, ): Flow<PageDN<ContractDN>> = flow { emit(PageDN(emptyList(), 0)) }
    override fun getContractStates(): Flow<List<ContractStateDN>> = flow { emit(emptyList()) }
    override fun cancelContract(params: CancelContractParamsDN): Flow<Unit> = flow { emit(Unit) }
    override fun getContractPaymentHistory(contractNumber: String): Flow<List<ContractPaymentHistoryItemDN>> = flow { emit(emptyList()) }
    override fun downloadContractReport(premiumType: ContractPremiumType): Flow<PdfDownloadDN> = flow { emit(PdfDownloadDN(null)) }
    override fun getContractDebit(premiumType: ContractPremiumType, month: Int): Flow<ContractDebitDN> = flow { emit(ContractDebitDN(null, null, null, null, null, null, null)) }
    override fun getContractLastPayment(premiumType: ContractPremiumType): Flow<ContractLastPaymentDN> = flow { emit(ContractLastPaymentDN(null, null, null)) }
    override fun getPaymentCalculationDetails(premiumType: ContractPremiumType, startDate: Long, endDate: Long): Flow<List<PaymentCalculationRowDN>> = flow { emit(emptyList()) }
}
