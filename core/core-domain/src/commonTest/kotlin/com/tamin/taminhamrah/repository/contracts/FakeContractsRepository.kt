package com.tamin.taminhamrah.repository.contracts

import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.CancelContractParamsDN
import com.tamin.taminhamrah.model.contracts.ContractDebitDN
import com.tamin.taminhamrah.model.contracts.ContractLastPaymentDN
import com.tamin.taminhamrah.model.contracts.ContractPaymentHistoryItemDN
import com.tamin.taminhamrah.model.contracts.ContractPremiumType
import com.tamin.taminhamrah.model.contracts.ContractStateDN
import com.tamin.taminhamrah.model.contracts.PaymentCalculationRowDN
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
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

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
    var makeContractCalled = false
    var lastMakeFreelanceContractByGuardianParams: FreelanceContractByGuardianParams? = null
    var makeFreelanceContractByGuardianCalled = false
    var lastMakeOptionalContractByGuardianParams: OptionalContractByGuardianParams? = null
    var makeOptionalContractByGuardianCalled = false
    var lastInsurancePaymentParams: InsurancePaymentParamsDN? = null
    var insurancePaymentResult: InsurancePaymentDN? = null
    var lastPaymentStatusSystemType: String? = null
    var paymentStatusResult: JsonElement? = JsonPrimitive(true)
    var lastQuery: ApiQueryParamDN? = null
    var lastBranchCityCode: String? = null
    var studentInsuranceContractsCalled = false

    var lastUploadImageRequest: UploadImageRequestDN? = null
    var uploadImageResult: String = "a4769aa8-b9af-4183-83b9-367dc9f52511"

    var lastSaveContactRequest: SaveContactRequestDN? = null
    var saveContactResult: Any? = null

    override fun getContracts(query: ApiQueryParamDN?): Flow<List<ContractDN>> = flow {
        lastQuery = query
        if (shouldThrowError) throw error
        emit(contractsResult)
    }

    var lastPageQuery: ApiQueryParamDN? = null
    var contractsPageResult: PageDN<ContractDN> = PageDN(items = emptyList(), total = 0)
    var contractStatesResult: List<ContractStateDN> = emptyList()
    var lastCancelParams: CancelContractParamsDN? = null
    var cancelContractCalled = false
    var lastPaymentHistoryContractNumber: String? = null
    var paymentHistoryResult: List<ContractPaymentHistoryItemDN> = emptyList()
    var lastReportPremiumType: ContractPremiumType? = null
    var contractReportResult: PdfDownloadDN = PdfDownloadDN(pdf = null)

    override fun getContractsPage(query: ApiQueryParamDN): Flow<PageDN<ContractDN>> = flow {
        lastPageQuery = query
        if (shouldThrowError) throw error
        emit(contractsPageResult)
    }

    override fun getContractStates(): Flow<List<ContractStateDN>> = flow {
        if (shouldThrowError) throw error
        emit(contractStatesResult)
    }

    override fun cancelContract(params: CancelContractParamsDN): Flow<Unit> = flow {
        lastCancelParams = params
        cancelContractCalled = true
        if (shouldThrowError) throw error
        emit(Unit)
    }

    override fun getContractPaymentHistory(
        contractNumber: String,
    ): Flow<List<ContractPaymentHistoryItemDN>> = flow {
        lastPaymentHistoryContractNumber = contractNumber
        if (shouldThrowError) throw error
        emit(paymentHistoryResult)
    }

    override fun downloadContractReport(premiumType: ContractPremiumType): Flow<PdfDownloadDN> = flow {
        lastReportPremiumType = premiumType
        if (shouldThrowError) throw error
        emit(contractReportResult)
    }

    var contractDebitResult: ContractDebitDN = ContractDebitDN(
        total = 0L,
        insurancePremiums = 0L,
        previousDebit = 0L,
        startDate = 0L,
        endDate = 0L,
        payPremiumDate = null,
        infoMessage = null,
    )
    var lastDebitPremiumType: ContractPremiumType? = null
    var lastDebitMonth: Int? = null

    override fun getContractDebit(
        premiumType: ContractPremiumType,
        month: Int,
    ): Flow<ContractDebitDN> = flow {
        lastDebitPremiumType = premiumType
        lastDebitMonth = month
        if (shouldThrowError) throw error
        emit(contractDebitResult)
    }

    var contractLastPaymentResult: ContractLastPaymentDN = ContractLastPaymentDN(
        lastPaymentTimestamp = null,
        checkReloLap = null,
        medicalResultResend = null,
    )
    var lastLastPaymentPremiumType: ContractPremiumType? = null

    override fun getContractLastPayment(
        premiumType: ContractPremiumType,
    ): Flow<ContractLastPaymentDN> = flow {
        lastLastPaymentPremiumType = premiumType
        if (shouldThrowError) throw error
        emit(contractLastPaymentResult)
    }

    var paymentCalculationDetailsResult: List<PaymentCalculationRowDN> = emptyList()
    var lastCalcDetailsArgs: Triple<ContractPremiumType, Long, Long>? = null

    override fun getPaymentCalculationDetails(
        premiumType: ContractPremiumType,
        startDate: Long,
        endDate: Long,
    ): Flow<List<PaymentCalculationRowDN>> = flow {
        lastCalcDetailsArgs = Triple(premiumType, startDate, endDate)
        if (shouldThrowError) throw error
        emit(paymentCalculationDetailsResult)
    }

    var lastPremiumTypeCode: String? = null
    var freeJobWagesResult: List<FreeJobDN> = emptyList()
    var calculatedOptionalSalaryResult: Long? = null
    var lastPremiumRateCode: String? = null
    var lastFreelanceCalculateParams: FreelanceCalculateSalaryParams? = null
    var lastFreelancePremiumRangeParams: FreelancePremiumRangeParams? = null

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
