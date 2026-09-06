package com.tamin.taminhamrah.repository.contracts

import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.CancelContractParamsDN
import com.tamin.taminhamrah.model.contracts.ContractPaymentHistoryItemDN
import com.tamin.taminhamrah.model.contracts.ContractPremiumType
import com.tamin.taminhamrah.model.contracts.ContractStateDN
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
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow

interface ContractsRepository {
    fun getContracts(query: ApiQueryParamDN?): Flow<List<ContractDN>>

    /**
     * Page-at-a-time variant of [getContracts] for the امور قراردادها و پرداخت list, which is
     * network-only (no Room cache) and paginated through [com.tamin.taminhamrah.paging.Paginator].
     */
    fun getContractsPage(query: ApiQueryParamDN): Flow<PageDN<ContractDN>>

    /** علت خاتمه قرارداد — reasons offered when cancelling a contract. */
    fun getContractStates(): Flow<List<ContractStateDN>>

    /** غیرفعال کردن قرارداد. Completes normally on success, throws on failure. */
    fun cancelContract(params: CancelContractParamsDN): Flow<Unit>

    /** مشاهده پرداخت‌ها for a single contract. */
    fun getContractPaymentHistory(contractNumber: String): Flow<List<ContractPaymentHistoryItemDN>>

    /** مشاهده قرارداد — the contract report as a downloadable PDF. */
    fun downloadContractReport(premiumType: ContractPremiumType): Flow<PdfDownloadDN>
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
    fun makeContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN>
    fun makeFreelanceContractByGuardian(params: FreelanceContractByGuardianParams): Flow<FreelanceContractResultDN>
    fun makeOptionalContractByGuardian(params: OptionalContractByGuardianParams): Flow<FreelanceContractResultDN>
    fun getInsurancePayment(params: InsurancePaymentParamsDN): Flow<InsurancePaymentDN>
    fun checkInsurancePaymentStatus(systemType: String): Flow<Any?>
    fun uploadImage(request: UploadImageRequestDN): Flow<String>
    fun saveContact(request: SaveContactRequestDN): Flow<Any?>
}
