package com.tamin.taminhamrah.dataSource.pension

import kotlinx.coroutines.CancellationException
import io.ktor.serialization.JsonConvertException
import com.tamin.taminhamrah.apiService.pension.PensionApiService
import com.tamin.taminhamrah.model.pension.EdictPensionerDTO
import com.tamin.taminhamrah.model.pension.PensionIdDTO
import com.tamin.taminhamrah.model.pension.PensionInquiryDTO
import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDTO
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDTO
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityFinalConfirmRequest
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveDocumentRequest
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveInfoRequest
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveInfoResponseDTO
import com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.RegisteredMedicalCommissionDTO
import com.tamin.taminhamrah.model.pension.fish.PayRollDTO
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDTO
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequest
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDTO
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestCreatedDTO
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestFormDTO
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDTO
import com.tamin.taminhamrah.model.pension.sendRetirementDocument.RetirementSaveDocumentRequest
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal.DisabilityPersonalInfoDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.extractMessage
import com.tamin.taminhamrah.tools.readPdfChannel
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException

class PensionRemoteDataSourceImpl(
    private val pensionApiService: PensionApiService,
    private val apiQueryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : PensionRemoteDataSource {

    override suspend fun getPensionInquiry(
        query: ApiQueryParamDN
    ): ListData<PensionInquiryDTO> {
        return try {
            val response = pensionApiService.getPensionInquiry(apiQueryBuilder.buildQuery(query))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
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
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getEdictPensioner(
        query: ApiQueryParamDN
    ): EdictPensionerDTO {
        return try {
            val filterJson = apiQueryBuilder.buildFilterJson(query.filters)
            val response = pensionApiService.getEdictPensioner(mapOf("filter" to filterJson))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun sendRequestDeferredInstallmentCertificate(
        request: DeferredInstallmentRequest
    ): DeferredInstallmentCertificateDTO {
        return try {
            val response = pensionApiService.sendRequestDeferredInstallmentCertificate(request)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getPensionerPayRoll(
        filter: List<ApiFilterDN>
    ): ListData<PayRollDTO> {
        return try {
            val response =
                pensionApiService.getPensionerPayRoll(apiQueryBuilder.buildFilterJson(filter))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
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
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getUserAge(
        filter: List<ApiFilterDN>
    ): AgeDTO {
        return try {
            // `birthDate` is a bare epoch, not a filter array — the endpoint takes the value itself
            // (legacy: `@Query("birthDate") birthDate: Long?`). Encoding the filter JSON here sent
            // `birthDate=[]` and the service answered with no age at all.
            val birthDate = filter
                .firstOrNull { it.property == FilterProperty.BIRTH_DATE }
                ?.value
                .orEmpty()
            val response = pensionApiService.getUserAge(mapOf("birthDate" to birthDate))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun pensionerPayRollPDF(
        filter: List<ApiFilterDN>
    ): PdfDownloadDTO {
        return try {
            val filterJson = apiQueryBuilder.buildFilterJson(filter)
            val response = pensionApiService.pensionerPayRollPDF(mapOf("filter" to filterJson))
            PdfDownloadDTO(
                pdf = InputStreamDTO(
                    pdf = response.readPdfChannel()
                )
            )
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getEdictReportPDF(
        filter: List<ApiFilterDN>
    ): PdfDownloadDTO {
        return try {
            val filterJson = apiQueryBuilder.buildFilterJson(filter)
            val response = pensionApiService.getEdictReportPDF(mapOf("filter" to filterJson))
            PdfDownloadDTO(
                pdf = InputStreamDTO(
                    pdf = response.readPdfChannel()
                )
            )
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getRetirementRequestInfo(
        filter: List<ApiFilterDN>
    ): ListData<RetirementRequestDTO> {
        return try {
            val filterJson = apiQueryBuilder.buildFilterJson(filter)
            val response = pensionApiService.getRetirementRequestInfo(mapOf("filter" to filterJson))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun createRetirementRequest(
        authenticationsCode: Long,
        form: RetirementRequestFormDTO
    ): RetirementRequestCreatedDTO {
        return try {
            val response = pensionApiService.createRetirementRequest(authenticationsCode, form)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun checkRetirementStatus(): RetirementStatusDTO {
        return try {
            val response = pensionApiService.checkRetirementStatus()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun authenticationAndGetPersonalInfo(
        authenticationsCode: Long
    ): RetirementPersonalDTO {
        return try {
            val response = pensionApiService.authenticationAndGetPersonalInfo(authenticationsCode)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun sendRetirementDocument(
        requestId: String,
        request: RetirementSaveDocumentRequest
    ): String? {
        return try {
            val response = pensionApiService.sendRetirementDocument(requestId, request)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getAuthenticationCode(): AuthenticationTicketDTO {
        return try {
            val response = pensionApiService.getAuthenticationCode()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun sendEdictPensionerToMyInbox(
        filter: List<ApiFilterDN>
    ): String {
        return try {
            val filterJson = apiQueryBuilder.buildFilterJson(filter)
            val response =
                pensionApiService.sendEdictPensionerToMyInbox(mapOf("filter" to filterJson))
            response.extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun sendPayRollToInbox(filter: List<ApiFilterDN>): String? {
        return try {
            val filterJson = apiQueryBuilder.buildFilterJson(filter)
            val response = pensionApiService.sendPayRollToInbox(filterJson)
            response?.extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun sendRequestInquirePensionCertificate(filter: List<ApiFilterDN>) :String? {
        return try {
            val filterJson = apiQueryBuilder.buildFilterJson(filter)

            val response = pensionApiService.sendRequestInquirePensionCertificate(
                mapOf("filter" to filterJson)
            )
            response.extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun saveDisabilityUserInfo(
        body: DisabilitySaveInfoRequest
    ): DisabilitySaveInfoResponseDTO {
        return try {
            pensionApiService.saveDisabilityUserInfo(body).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun finalConfirmDisabilityRequest(
        requestId: Long,
        body: DisabilityFinalConfirmRequest
    ): DisabilitySaveInfoResponseDTO {
        return try {
            pensionApiService.finalConfirmDisabilityRequest(requestId, body).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun saveDocumentDisability(
        requestId: Long,
        body: DisabilitySaveDocumentRequest
    ): String {
        return try {
            pensionApiService.saveDocumentDisability(requestId, body).extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getMedicalCommissionPdf(
        lastWorkshop: String
    ): PdfDownloadDTO {
        return try {
            val response = pensionApiService.getMedicalCommissionPdf(lastWorkshop)
            PdfDownloadDTO(
                pdf = InputStreamDTO(
                    pdf = response.readPdfChannel()
                )
            )
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getRegisteredMedicalCommission(
        query: ApiQueryParamDN
    ): ListData<RegisteredMedicalCommissionDTO> {
        return try {
            val response = pensionApiService.getRegisteredMedicalCommission(apiQueryBuilder.buildQuery(query))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}
