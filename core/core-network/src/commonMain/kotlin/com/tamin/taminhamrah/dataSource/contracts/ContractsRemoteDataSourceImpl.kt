package com.tamin.taminhamrah.dataSource.contracts

import com.tamin.taminhamrah.tools.requireSuccessStatus
import com.tamin.taminhamrah.tools.safeCall
import com.tamin.taminhamrah.apiService.contract.ContractsApiService
import com.tamin.taminhamrah.model.contracts.BranchDTO
import com.tamin.taminhamrah.model.contracts.ContractDTO
import com.tamin.taminhamrah.model.contracts.ContractByGuardianRequestDTO
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
import com.tamin.taminhamrah.model.contracts.UpdateOptionalContractByGuardianRequestDTO
import com.tamin.taminhamrah.model.contracts.UpdateOptionalContractDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import kotlinx.serialization.json.JsonElement
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class ContractsRemoteDataSourceImpl(
    private val contractsApiService: ContractsApiService,
    private val apiQueryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : ContractsRemoteDataSource {
    override suspend fun getContracts(query: ApiQueryParamDN): ListData<ContractDTO> {
        return errorParser.safeCall("getContracts") {
            val response =
                contractsApiService.getContractList(apiQueryBuilder.buildQuery(query))
            response.extractData()
        }
    }

    override suspend fun getRegistrationInfo(): RegistrationInfoDTO {
        return errorParser.safeCall("getRegistrationInfo") {
            contractsApiService.getRegistrationInfo().extractData()
        }
    }

    override suspend fun getBranches(query: ApiQueryParamDN): ListData<BranchDTO> {
        return errorParser.safeCall("getBranches") {
            val response = contractsApiService.getBranches(apiQueryBuilder.buildQuery(query))
            response.extractData()
        }
    }

    override suspend fun getSpcPremiumRates(): ListData<PremiumRateDTO> {
        return errorParser.safeCall("getSpcPremiumRates") {
            contractsApiService.getSpcPremiumRates().extractData()
        }
    }

    override suspend fun getFreelancePremiumRange(params: FreelancePremiumRangeParams): FreelancePremiumRangeDTO {
        return errorParser.safeCall("getFreelancePremiumRange") {
            contractsApiService.getFreelancePremiumRange(
                treatmentSupportCode = params.treatmentSupportCode,
                spcRateCode = params.spcRateCode,
                freeJobCode = params.freeJobCode,
            ).extractData()
        }
    }

    override suspend fun getOptionalPremiumRange(): FreelancePremiumRangeDTO {
        return errorParser.safeCall("getOptionalPremiumRange") {
            contractsApiService.getOptionalPremiumRange().extractData()
        }
    }

    override suspend fun checkRedCrossStatus(): String {
        return errorParser.safeCall("checkRedCrossStatus") {
            contractsApiService.checkRedCrossStatus().extractData()
        }
    }

    override suspend fun checkMedicalStudent(): String {
        return errorParser.safeCall("checkMedicalStudent") {
            contractsApiService.checkMedicalStudent().extractData()
        }
    }

    override suspend fun calculateFreelanceSalary(params: FreelanceCalculateSalaryParams): Long {
        return errorParser.safeCall("calculateFreelanceSalary") {
            contractsApiService.calculateFreelanceSalary(
                monthlyPremium = params.monthlyPremium,
                treatmentSupportCode = params.treatmentSupportCode,
                spcRateCode = params.spcRateCode,
            ).extractData()
        }
    }

    override suspend fun calculateOptionalSalary(premiumRateCode: String): Long {
        return errorParser.safeCall("calculateOptionalSalary") {
            contractsApiService.calculateOptionalSalary(premiumRateCode).extractData()
        }
    }

    override suspend fun getFreeJobWages(query: ApiQueryParamDN): ListData<FreeJobDTO> {
        return errorParser.safeCall("getFreeJobWages") {
            contractsApiService.getFreeJobWages(apiQueryBuilder.buildQuery(query)).extractData()
        }
    }

    override suspend fun makeFreelanceContract(
        monthlyPremium: Long,
        request: FreelanceMakeContractRequestDTO,
    ): FreelanceContractResultDTO {
        return errorParser.safeCall("makeFreelanceContract") {
            contractsApiService.makeFreelanceContract(
                monthlyPremium = monthlyPremium,
                request = request,
            ).extractData()
        }
    }

    override suspend fun makeContract(
        selectedSalary: Long,
        request: FreelanceMakeContractRequestDTO,
    ): FreelanceContractResultDTO {
        return errorParser.safeCall("makeContract") {
            contractsApiService.makeContract(
                selectedSalary = selectedSalary,
                request = request,
            ).extractData()
        }
    }

    override suspend fun makeFreelanceContractByGuardian(
        selectedSalary: Long,
        request: ContractByGuardianRequestDTO,
    ): FreelanceContractResultDTO {
        return errorParser.safeCall("makeFreelanceContractByGuardian") {
            contractsApiService.makeFreelanceContractByGuardian(
                selectedSalary = selectedSalary,
                request = request,
            ).extractData()
        }
    }

    override suspend fun makeOptionalContractByGuardian(
        selectedSalary: Long,
        request: OptionalContractByGuardianRequestDTO,
    ): FreelanceContractResultDTO {
        return errorParser.safeCall("makeOptionalContractByGuardian") {
            contractsApiService.makeOptionalContractByGuardian(
                selectedSalary = selectedSalary,
                request = request,
            ).extractData()
        }
    }

    override suspend fun updateFreelanceContract(
        premium: Long,
        request: FreelanceMakeContractRequestDTO,
    ) {
        errorParser.safeCall("updateFreelanceContract") {
            val response = contractsApiService.updateFreelanceContract(premium, request)
            response.requireSuccessStatus()
        }
    }

    override suspend fun updateOptionalContract(
        premium: Long,
        request: UpdateOptionalContractDTO,
    ) {
        errorParser.safeCall("updateOptionalContract") {
            val response = contractsApiService.updateOptionalContract(premium, request)
            response.requireSuccessStatus()
        }
    }

    override suspend fun updateFreelanceContractByGuardian(
        premium: Long,
        request: ContractByGuardianRequestDTO,
    ) {
        errorParser.safeCall("updateFreelanceContractByGuardian") {
            val response = contractsApiService.updateFreelanceContractByGuardian(premium, request)
            response.requireSuccessStatus()
        }
    }

    override suspend fun updateOptionalContractByGuardian(
        premium: Long,
        request: UpdateOptionalContractByGuardianRequestDTO,
    ) {
        errorParser.safeCall("updateOptionalContractByGuardian") {
            val response = contractsApiService.updateOptionalContractByGuardian(premium, request)
            response.requireSuccessStatus()
        }
    }

    override suspend fun getInsurancePayment(params: InsurancePaymentParamsDN): InsurancePaymentDTO {
        return errorParser.safeCall("getInsurancePayment") {
            contractsApiService.getInsurancePayment(
                startDate = params.startDate,
                endDate = params.endDate,
                amount = params.amount,
                systemType = params.systemType,
                redirectUri = params.redirectUri,
                paramPage = params.paramPage,
                month = params.month,
                redirectUrl = params.redirectUrl,
            ).extractData()
        }
    }

    override suspend fun checkInsurancePaymentStatus(systemType: String): JsonElement? {
        return errorParser.safeCall("checkInsurancePaymentStatus") {
            contractsApiService.checkInsurancePaymentStatus(systemType).data
        }
    }

    override suspend fun uploadImage(request: UploadImageRequestDN): String? {
        return errorParser.safeCall("uploadImage") {
            val content = MultiPartFormDataContent(
                formData {
                    append(
                        key = "file",
                        value = request.bytes,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, ContentType.Image.JPEG.toString())
                            append(
                                HttpHeaders.ContentDisposition,
                                "filename=\"${request.fileName}\"",
                            )
                        },
                    )
                },
            )
            contractsApiService.uploadImage(content).guid
        }
    }

    override suspend fun saveContact(request: SaveContactRequestDTO): Any? =
        errorParser.safeCall("saveContact") {
            val response = contractsApiService.saveContact(request)
            response.requireSuccessStatus()
            response.data as Any?
        }
}
