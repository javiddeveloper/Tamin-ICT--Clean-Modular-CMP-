package com.tamin.taminhamrah.repository.pension

import com.tamin.taminhamrah.model.pension.EdictPensionerDN
import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDN
import com.tamin.taminhamrah.model.pension.EdictPensionerInboxDN
import com.tamin.taminhamrah.model.pension.InquirePensionCertificateDN
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.pension.PayRollDN
import com.tamin.taminhamrah.model.pension.PayRollInboxDN
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityFinalConfirmDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityRequestRefDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveDocumentDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveInfoDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.RegisteredMedicalCommissionDN
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequestDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementSaveDocumentDN
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoDN
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow

interface PensionRepository {
    suspend fun getPensionInquiry(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<PensionInquiryDN>>

    suspend fun getPensionerId(): Flow<List<PensionIdDN>>

    suspend fun getEdictPensioner(
        query: ApiQueryParamDN
    ): Flow<EdictPensionerDN?>

    suspend fun sendRequestDeferredInstallmentCertificate(
        request: DeferredInstallmentRequestDN
    ): Flow<DeferredInstallmentCertificateDN>

    suspend fun getPensionerPayRoll(
        filters: List<ApiFilterDN>
    ): Flow<List<PayRollDN>>

    suspend fun getDisabilityPersonalInfo(): Flow<DisabilityPersonalInfoDN>

    suspend fun getUserAge(
        filters: List<ApiFilterDN>
    ): Flow<AgeDN>

    suspend fun pensionerPayRollPDF(
        filters: List<ApiFilterDN>
    ): Flow<com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN>

    suspend fun getEdictReportPDF(
        filters: List<ApiFilterDN>
    ): Flow<com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN>

    suspend fun getRetirementRequestInfo(
        filters: List<ApiFilterDN>
    ): Flow<List<RetirementRequestDN>>

    suspend fun checkRetirementStatus(): Flow<RetirementStatusDN>

    suspend fun sendRetirementDocument(
        requestId: String,
        request: RetirementSaveDocumentDN
    ): Flow<String?>

    suspend fun authenticationAndGetPersonalInfo(
        authenticationsCode: Long
    ): Flow<RetirementPersonalDN>

    suspend fun getAuthenticationCode(): Flow<AuthenticationTicketDN>

    suspend fun sendEdictPensionerToMyInbox(
        filters: List<ApiFilterDN>
    ): Flow<EdictPensionerInboxDN>

    suspend fun sendPayRollToInbox(
        filters: List<ApiFilterDN>
    ): Flow<PayRollInboxDN>

    suspend fun sendRequestInquirePensionCertificate(
        filters: List<ApiFilterDN>
    ): Flow<InquirePensionCertificateDN>

    suspend fun saveDisabilityUserInfo(
        body: DisabilitySaveInfoDN
    ): Flow<DisabilityRequestRefDN?>

    suspend fun finalConfirmDisabilityRequest(
        requestId: Long,
        body: DisabilityFinalConfirmDN
    ): Flow<DisabilityRequestRefDN?>

    suspend fun saveDocumentDisability(
        requestId: Long,
        body: DisabilitySaveDocumentDN
    ): Flow<String?>

    suspend fun getMedicalCommissionPdf(
        lastWorkshop: String
    ): Flow<PdfDownloadDN>

    suspend fun getRegisteredMedicalCommission(
        filters: List<ApiFilterDN>
    ): Flow<List<RegisteredMedicalCommissionDN>>
}

