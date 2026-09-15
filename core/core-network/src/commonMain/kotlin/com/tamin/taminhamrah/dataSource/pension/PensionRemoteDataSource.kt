package com.tamin.taminhamrah.dataSource.pension

import com.tamin.taminhamrah.model.pension.EdictPensionerDTO
import com.tamin.taminhamrah.model.pension.PensionIdDTO
import com.tamin.taminhamrah.model.pension.PensionInquiryDTO
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDTO
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestCreatedDTO
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestFormDTO
import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDTO
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityFinalConfirmRequest
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveDocumentRequest
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveInfoRequest
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveInfoResponseDTO
import com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.RegisteredMedicalCommissionDTO
import com.tamin.taminhamrah.model.pension.fish.PayRollDTO
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDTO
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequest
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDTO
import com.tamin.taminhamrah.model.pension.sendRetirementDocument.RetirementSaveDocumentRequest
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDTO
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal.DisabilityPersonalInfoDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData


interface PensionRemoteDataSource {
    suspend fun getPensionInquiry(query: ApiQueryParamDN): ListData<PensionInquiryDTO>
    suspend fun getPensionerId(): ListData<PensionIdDTO>
    suspend fun getEdictPensioner(query: ApiQueryParamDN): EdictPensionerDTO?
    suspend fun sendRequestDeferredInstallmentCertificate(request: DeferredInstallmentRequest): DeferredInstallmentCertificateDTO
    suspend fun getPensionerPayRoll(
        filter: List<ApiFilterDN>
    ): ListData<PayRollDTO>

    suspend fun getUserAge(
        filter: List<ApiFilterDN>
    ): AgeDTO

    suspend fun pensionerPayRollPDF(filter: List<ApiFilterDN>): PdfDownloadDTO
    suspend fun getEdictReportPDF(filter: List<ApiFilterDN>): PdfDownloadDTO
    suspend fun getAuthenticationCode(): AuthenticationTicketDTO

    suspend fun getRetirementRequestInfo(filter: List<ApiFilterDN>) :ListData<RetirementRequestDTO>
    suspend fun createRetirementRequest(
        authenticationsCode: Long,
        form: RetirementRequestFormDTO
    ): RetirementRequestCreatedDTO

    suspend fun checkRetirementStatus(): RetirementStatusDTO
    suspend fun getDisabilityPersonalInfo(): DisabilityPersonalInfoDTO
    suspend fun sendRequestInquirePensionCertificate(filter: List<ApiFilterDN>) : String?

    suspend fun sendRetirementDocument(
        requestId: String,
        request: RetirementSaveDocumentRequest
    ): String?
    suspend fun authenticationAndGetPersonalInfo(
        authenticationsCode: Long
    ): RetirementPersonalDTO
    suspend fun sendEdictPensionerToMyInbox(
        filter: List<ApiFilterDN>
    ): String?

    suspend fun sendPayRollToInbox(
        filter: List<ApiFilterDN>
    ): String?

    suspend fun saveDisabilityUserInfo(body: DisabilitySaveInfoRequest): DisabilitySaveInfoResponseDTO
    suspend fun finalConfirmDisabilityRequest(requestId: Long, body: DisabilityFinalConfirmRequest): DisabilitySaveInfoResponseDTO
    suspend fun saveDocumentDisability(requestId: Long, body: DisabilitySaveDocumentRequest): String?
    suspend fun getMedicalCommissionPdf(lastWorkshop: String): PdfDownloadDTO
    suspend fun getRegisteredMedicalCommission(query: ApiQueryParamDN): ListData<RegisteredMedicalCommissionDTO>
}
