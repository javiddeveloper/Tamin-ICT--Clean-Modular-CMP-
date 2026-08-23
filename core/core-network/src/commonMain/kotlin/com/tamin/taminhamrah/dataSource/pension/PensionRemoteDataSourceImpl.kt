package com.tamin.taminhamrah.dataSource.pension

import com.tamin.taminhamrah.apiService.pension.PensionApiService
import com.tamin.taminhamrah.model.pension.EdictPensionerDTO
import com.tamin.taminhamrah.model.pension.PensionIdDTO
import com.tamin.taminhamrah.model.pension.PensionInquiryDTO
import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDTO
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDTO
import com.tamin.taminhamrah.model.pension.fish.PayRollDTO
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDTO
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequest
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDTO
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDTO
import com.tamin.taminhamrah.model.pension.sendRetirementDocument.RetirementSaveDocumentRequest
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal.DisabilityPersonalInfoDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.extractMessage
import com.tamin.taminhamrah.tools.safeCall

class PensionRemoteDataSourceImpl(
    private val pensionApiService: PensionApiService,
    private val apiQueryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : PensionRemoteDataSource {

    override suspend fun getPensionInquiry(
        query: ApiQueryParamDN
    ): ListData<PensionInquiryDTO> = errorParser.safeCall("getPensionInquiry") {
        val response = pensionApiService.getPensionInquiry(apiQueryBuilder.buildQuery(query))
        response.extractData()
    }

    override suspend fun getPensionerId(): ListData<PensionIdDTO> =
        errorParser.safeCall("getPensionerId") {
            val response = pensionApiService.getPensionerId()
            response.extractData()
        }

    override suspend fun getEdictPensioner(
        query: ApiQueryParamDN
    ): EdictPensionerDTO? = errorParser.safeCall("getEdictPensioner") {
        val filterJson = apiQueryBuilder.buildFilterJson(query.filters)
        val response = pensionApiService.getEdictPensioner(mapOf("filter" to filterJson))
        response?.extractData()
    }

    override suspend fun sendRequestDeferredInstallmentCertificate(
        request: DeferredInstallmentRequest
    ): DeferredInstallmentCertificateDTO =
        errorParser.safeCall("sendRequestDeferredInstallmentCertificate") {
            val response = pensionApiService.sendRequestDeferredInstallmentCertificate(request)
            response.extractData()
        }

    override suspend fun getPensionerPayRoll(
        filter: List<ApiFilterDN>
    ): ListData<PayRollDTO> = errorParser.safeCall("getPensionerPayRoll") {
        val response =
            pensionApiService.getPensionerPayRoll(apiQueryBuilder.buildFilterJson(filter))
        response.extractData()
    }

    override suspend fun getDisabilityPersonalInfo(): DisabilityPersonalInfoDTO =
        errorParser.safeCall("getDisabilityPersonalInfo") {
            val response = pensionApiService.getDisabilityPersonalInfo()
            response.extractData()
        }

    override suspend fun getUserAge(
        filter: List<ApiFilterDN>
    ): AgeDTO = errorParser.safeCall("getUserAge") {
        val filterJson = apiQueryBuilder.buildFilterJson(filter)
        val response = pensionApiService.getUserAge(mapOf("birthDate" to filterJson))
        response.extractData()
    }

    override suspend fun pensionerPayRollPDF(
        filter: List<ApiFilterDN>
    ): PdfDownloadDTO = errorParser.safeCall("pensionerPayRollPDF") {
        val filterJson = apiQueryBuilder.buildFilterJson(filter)
        val response = pensionApiService.pensionerPayRollPDF(mapOf("filter" to filterJson))
        PdfDownloadDTO(
            pdf = InputStreamDTO(
                pdf = response.body()
            )
        )
    }

    override suspend fun getEdictReportPDF(
        filter: List<ApiFilterDN>
    ): PdfDownloadDTO = errorParser.safeCall("getEdictReportPDF") {
        val filterJson = apiQueryBuilder.buildFilterJson(filter)
        val response = pensionApiService.getEdictReportPDF(mapOf("filter" to filterJson))
        PdfDownloadDTO(
            pdf = InputStreamDTO(
                pdf = response.body()
            )
        )
    }

    override suspend fun getRetirementRequestInfo(
        filter: List<ApiFilterDN>
    ): ListData<RetirementRequestDTO> = errorParser.safeCall("getRetirementRequestInfo") {
        val filterJson = apiQueryBuilder.buildFilterJson(filter)
        val response = pensionApiService.getRetirementRequestInfo(mapOf("filter" to filterJson))
        response.extractData()
    }

    override suspend fun checkRetirementStatus(): RetirementStatusDTO =
        errorParser.safeCall("checkRetirementStatus") {
            val response = pensionApiService.checkRetirementStatus()
            response.extractData()
        }

    override suspend fun authenticationAndGetPersonalInfo(
        authenticationsCode: Long
    ): RetirementPersonalDTO = errorParser.safeCall("authenticationAndGetPersonalInfo") {
        val response = pensionApiService.authenticationAndGetPersonalInfo(authenticationsCode)
        response.extractData()
    }

    override suspend fun sendRetirementDocument(
        requestId: String,
        request: RetirementSaveDocumentRequest
    ): String? = errorParser.safeCall("sendRetirementDocument") {
        val response = pensionApiService.sendRetirementDocument(requestId, request)
        response?.extractData()
    }

    override suspend fun getAuthenticationCode(): AuthenticationTicketDTO =
        errorParser.safeCall("getAuthenticationCode") {
            val response = pensionApiService.getAuthenticationCode()
            response.extractData()
        }

    override suspend fun sendEdictPensionerToMyInbox(
        filter: List<ApiFilterDN>
    ): String? = errorParser.safeCall("sendEdictPensionerToMyInbox") {
        val filterJson = apiQueryBuilder.buildFilterJson(filter)
        val response =
            pensionApiService.sendEdictPensionerToMyInbox(mapOf("filter" to filterJson))
        response.extractMessage()
    }

    override suspend fun sendRequestInquirePensionCertificate(
        filter: List<ApiFilterDN>
    ): String? = errorParser.safeCall("sendRequestInquirePensionCertificate") {
        val filterJson = apiQueryBuilder.buildFilterJson(filter)
        val response = pensionApiService.sendRequestInquirePensionCertificate(
            mapOf("filter" to filterJson)
        )
        response.extractMessage()
    }
}
