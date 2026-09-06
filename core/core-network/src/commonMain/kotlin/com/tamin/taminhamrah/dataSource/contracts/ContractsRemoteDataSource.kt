package com.tamin.taminhamrah.dataSource.contracts

import com.tamin.taminhamrah.model.contracts.BranchDTO
import com.tamin.taminhamrah.model.contracts.CancelContractRequestDTO
import com.tamin.taminhamrah.model.contracts.ContractDTO
import com.tamin.taminhamrah.model.contracts.ContractByGuardianRequestDTO
import com.tamin.taminhamrah.model.contracts.ContractPaymentHistoryItemDTO
import com.tamin.taminhamrah.model.contracts.ContractPremiumType
import com.tamin.taminhamrah.model.contracts.ContractStateDTO
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDTO
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractRequestDTO
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDTO
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.model.contracts.FreeJobDTO
import com.tamin.taminhamrah.model.contracts.InsurancePaymentDTO
import com.tamin.taminhamrah.model.contracts.InsurancePaymentParamsDN
import com.tamin.taminhamrah.model.contracts.OptionalContractByGuardianRequestDTO
import com.tamin.taminhamrah.model.contracts.PremiumRateDTO
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDTO
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDTO
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData

interface ContractsRemoteDataSource {
    suspend fun getContracts(query: ApiQueryParamDN): ListData<ContractDTO>
    suspend fun getContractStates(query: ApiQueryParamDN): ListData<ContractStateDTO>
    suspend fun cancelContract(
        premiumType: ContractPremiumType,
        stateCode: Int,
        request: CancelContractRequestDTO,
    )
    suspend fun getContractPaymentHistory(contractNumber: String): List<ContractPaymentHistoryItemDTO>
    suspend fun downloadContractReport(premiumType: ContractPremiumType): PdfDownloadDTO
    suspend fun getRegistrationInfo(): RegistrationInfoDTO
    suspend fun getBranches(query: ApiQueryParamDN): ListData<BranchDTO>
    suspend fun getSpcPremiumRates(): ListData<PremiumRateDTO>
    suspend fun getFreelancePremiumRange(params: FreelancePremiumRangeParams): FreelancePremiumRangeDTO
    suspend fun calculateFreelanceSalary(params: FreelanceCalculateSalaryParams): Long
    suspend fun calculateOptionalSalary(premiumRateCode: String): Long
    suspend fun getFreeJobWages(query: ApiQueryParamDN): ListData<FreeJobDTO>
    suspend fun makeFreelanceContract(
        monthlyPremium: Long,
        request: FreelanceMakeContractRequestDTO,
    ): FreelanceContractResultDTO
    suspend fun makeContract(
        selectedSalary: Long,
        request: FreelanceMakeContractRequestDTO,
    ): FreelanceContractResultDTO
    suspend fun makeFreelanceContractByGuardian(
        selectedSalary: Long,
        request: ContractByGuardianRequestDTO,
    ): FreelanceContractResultDTO
    suspend fun makeOptionalContractByGuardian(
        selectedSalary: Long,
        request: OptionalContractByGuardianRequestDTO,
    ): FreelanceContractResultDTO
    suspend fun getInsurancePayment(params: InsurancePaymentParamsDN): InsurancePaymentDTO
    suspend fun checkInsurancePaymentStatus(systemType: String): Any?
    suspend fun uploadImage(request: UploadImageRequestDN): String?
    suspend fun saveContact(request: SaveContactRequestDTO): Any?
}
