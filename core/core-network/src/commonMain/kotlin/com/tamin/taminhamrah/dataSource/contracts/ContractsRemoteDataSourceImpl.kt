package com.tamin.taminhamrah.dataSource.contracts

import com.tamin.taminhamrah.apiService.contract.ContractsApiService
import com.tamin.taminhamrah.model.contracts.BranchDTO
import com.tamin.taminhamrah.model.contracts.ContractDTO
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDTO
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractRequestDTO
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDTO
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.model.contracts.FreeJobDTO
import com.tamin.taminhamrah.model.contracts.PremiumRateDTO
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDTO
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
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
        return try {
            val response =
                contractsApiService.getContractList(apiQueryBuilder.buildQuery(query))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getRegistrationInfo(): RegistrationInfoDTO {
        return try {
            contractsApiService.getRegistrationInfo().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getBranches(query: ApiQueryParamDN): ListData<BranchDTO> {
        return try {
            val response = contractsApiService.getBranches(apiQueryBuilder.buildQuery(query))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getSpcPremiumRates(): ListData<PremiumRateDTO> {
        return try {
            contractsApiService.getSpcPremiumRates().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getFreelancePremiumRange(params: FreelancePremiumRangeParams): FreelancePremiumRangeDTO {
        return try {
            contractsApiService.getFreelancePremiumRange(
                treatmentSupportCode = params.treatmentSupportCode,
                spcRateCode = params.spcRateCode,
                freeJobCode = params.freeJobCode,
            ).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun calculateFreelanceSalary(params: FreelanceCalculateSalaryParams): Long {
        return try {
            contractsApiService.calculateFreelanceSalary(
                monthlyPremium = params.monthlyPremium,
                treatmentSupportCode = params.treatmentSupportCode,
                spcRateCode = params.spcRateCode,
            ).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun calculateOptionalSalary(premiumRateCode: String): Long {
        return try {
            contractsApiService.calculateOptionalSalary(premiumRateCode).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getFreeJobWages(query: ApiQueryParamDN): ListData<FreeJobDTO> {
        return try {
            contractsApiService.getFreeJobWages(apiQueryBuilder.buildQuery(query)).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun makeFreelanceContract(
        monthlyPremium: Long,
        request: FreelanceMakeContractRequestDTO,
    ): FreelanceContractResultDTO {
        return try {
            contractsApiService.makeFreelanceContract(
                monthlyPremium = monthlyPremium,
                request = request,
            ).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun uploadImage(request: UploadImageRequestDN): String? {
        return try {
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
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR),
            )
        }
    }

    override suspend fun saveContact(request: SaveContactRequestDTO): Any? {
        return try {
            println("SaveContact: calling save-contact endpoint with $request")
            val response = contractsApiService.saveContact(request)
            println("SaveContact: response status=${response.status}, family=${response.family}")
            if (response.status !in 200..299) {
                throw TaminErrorUriException(
                    ErrorUri.fromString("CLIENT_ERROR: ${response.reason}"),
                )
            }
            response.data as Any?
        } catch (e: TaminErrorUriException) {
            println("SaveContact: tamin error ${e.message}")
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            println("SaveContact: exception ${e::class.simpleName} ${e.message}")
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR),
            )
        }
    }
}
