package com.tamin.taminhamrah.dataSource.pension

import com.tamin.taminhamrah.apiService.pension.PensionApiService
import com.tamin.taminhamrah.model.pension.EdictPensionerDTO
import com.tamin.taminhamrah.model.pension.PensionIdDTO
import com.tamin.taminhamrah.model.pension.PensionInquiryDTO
import com.tamin.taminhamrah.model.pension.fish.PayRollDTO
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDTO
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequest
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal.DisabilityPersonalInfoDTO
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import io.ktor.client.statement.bodyAsChannel

class PensionRemoteDataSourceImpl(
    private val pensionApiService: PensionApiService,
    private val apiQueryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : PensionRemoteDataSource {
    override suspend fun getPensionInquiry(query: ApiQueryParamDN): ListData<PensionInquiryDTO> {
        return try {
            val response =
                pensionApiService.getPensionInquiry(apiQueryBuilder.buildQuery(query))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getPensionerId(): ListData<PensionIdDTO> {
        return try {
            val response = pensionApiService.getPensionerId()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getEdictPensioner(query: ApiQueryParamDN): EdictPensionerDTO? {
        return try {
            val filterJson = apiQueryBuilder.buildFilterJson(query.filters)
            val response =
                pensionApiService.getEdictPensioner(mapOf("filter" to filterJson))
            response?.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun sendRequestDeferredInstallmentCertificate(request: DeferredInstallmentRequest): DeferredInstallmentCertificateDTO {
        return try {
            val response = pensionApiService.sendRequestDeferredInstallmentCertificate(request)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getPensionerPayRoll(
        filter: List<ApiFilterDN>
    ): PayRollDTO {
        return try {
            val response =
                pensionApiService.getPensionerPayRoll(apiQueryBuilder.buildFilterJson(filter))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getDisabilityPersonalInfo(): DisabilityPersonalInfoDTO {
        return try {
            val response = pensionApiService.getDisabilityPersonalInfo()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getUserAge(filter: List<ApiFilterDN>): AgeDTO {
        return try {
            val filterJson = apiQueryBuilder.buildFilterJson(filter)
            val response =
                pensionApiService.getUserAge(mapOf("birthDate" to filterJson))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun pensionerPayRollPDF(filter: List<ApiFilterDN>): PdfDownloadDTO {
        return try {
            val filterJson = apiQueryBuilder.buildFilterJson(filter)
            val response = pensionApiService.pensionerPayRollPDF(mapOf("filter" to filterJson))
            PdfDownloadDTO(
                pdf = InputStreamDTO(
                    pdf = response.body()
                )
            )
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.UNKNOWN)
            )
        }
    }

    override suspend fun authenticationAndGetPersonalInfo(authenticationsCode: Long): RetirementPersonalDTO {
        return try {
            val response = pensionApiService.authenticationAndGetPersonalInfo(authenticationsCode)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}
