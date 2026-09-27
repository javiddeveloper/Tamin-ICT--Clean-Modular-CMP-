package com.tamin.taminhamrah.feature.retirementPension.fake

import com.tamin.taminhamrah.model.pension.EdictPensionerDN
import com.tamin.taminhamrah.model.pension.EdictPensionerInboxDN
import com.tamin.taminhamrah.model.pension.InquirePensionCertificateDN
import com.tamin.taminhamrah.model.pension.PayRollDN
import com.tamin.taminhamrah.model.pension.PayRollInboxDN
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDN
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityFinalConfirmDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityRequestRefDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveDocumentDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveInfoDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.RegisteredMedicalCommissionDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequestDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestCreatedDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestFormDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementSaveDocumentDN
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDN
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePensionRepository : PensionRepository {
    var userAgeResult: AgeDN = AgeDN(age = "55,0,0", birthDate = "1345/01/01")
    /** No request on file: the live `checkRequests` reply is an object of nulls, never an empty body. */
    var checkRetirementStatusResult = RetirementStatusDN(requestId = null, requestStatusCode = null)
    var authenticationCodeResult: AuthenticationTicketDN? = AuthenticationTicketDN(mobileNumber = "09123456789")
    var authenticationAndGetPersonalInfoResult: RetirementPersonalDN? = null
    var retirementRequestInfoResult: List<RetirementRequestDN> = emptyList()
    var createRetirementRequestResult: RetirementRequestCreatedDN = RetirementRequestCreatedDN(requestId = 12345L)
    var sendRetirementDocumentResult: String? = "saved"

    var lastCreateRequestTicketCode: Long? = null
    var lastCreateRequestForm: RetirementRequestFormDN? = null
    var lastSendDocumentRequestId: String? = null
    var lastSendDocumentRequest: RetirementSaveDocumentDN? = null

    var shouldThrowError: Boolean = false
    var errorToThrow: Throwable = RuntimeException("Fake error")

    private fun notUsed(): Nothing = error("not used here")

    override suspend fun getUserAge(filters: List<ApiFilterDN>): Flow<AgeDN> = flow {
        if (shouldThrowError) throw errorToThrow
        emit(userAgeResult)
    }

    override suspend fun checkRetirementStatus(): Flow<RetirementStatusDN> = flow {
        if (shouldThrowError) throw errorToThrow
        emit(checkRetirementStatusResult)
    }

    override suspend fun getAuthenticationCode(): Flow<AuthenticationTicketDN> = flow {
        if (shouldThrowError) throw errorToThrow
        authenticationCodeResult?.let { emit(it) }
    }

    override suspend fun authenticationAndGetPersonalInfo(authenticationsCode: Long): Flow<RetirementPersonalDN> = flow {
        if (shouldThrowError) throw errorToThrow
        authenticationAndGetPersonalInfoResult?.let { emit(it) }
    }

    override suspend fun getRetirementRequestInfo(filters: List<ApiFilterDN>): Flow<List<RetirementRequestDN>> = flow {
        if (shouldThrowError) throw errorToThrow
        emit(retirementRequestInfoResult)
    }

    override suspend fun createRetirementRequest(
        authenticationsCode: Long,
        form: RetirementRequestFormDN,
    ): Flow<RetirementRequestCreatedDN> = flow {
        if (shouldThrowError) throw errorToThrow
        lastCreateRequestTicketCode = authenticationsCode
        lastCreateRequestForm = form
        emit(createRetirementRequestResult)
    }

    override suspend fun sendRetirementDocument(
        requestId: String,
        request: RetirementSaveDocumentDN,
    ): Flow<String?> = flow {
        if (shouldThrowError) throw errorToThrow
        lastSendDocumentRequestId = requestId
        lastSendDocumentRequest = request
        emit(sendRetirementDocumentResult)
    }

    override suspend fun getPensionInquiry(filters: List<ApiFilterDN>): Flow<List<PensionInquiryDN>> = flow { emit(emptyList()) }
    override suspend fun getPensionerId(): Flow<List<PensionIdDN>> = flow { emit(emptyList()) }
    override suspend fun getEdictPensioner(query: ApiQueryParamDN): Flow<EdictPensionerDN?> = flow { emit(null) }
    override suspend fun sendRequestDeferredInstallmentCertificate(request: DeferredInstallmentRequestDN): Flow<DeferredInstallmentCertificateDN> = flow {}
    override suspend fun getPensionerPayRoll(filters: List<ApiFilterDN>): Flow<List<PayRollDN>> = flow { emit(emptyList()) }
    override suspend fun getDisabilityPersonalInfo(): Flow<DisabilityPersonalInfoDN> = flow {}
    override suspend fun pensionerPayRollPDF(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> = flow {}
    override suspend fun getEdictReportPDF(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> = flow {}
    override suspend fun sendEdictPensionerToMyInbox(filters: List<ApiFilterDN>): Flow<EdictPensionerInboxDN> = flow { emit(EdictPensionerInboxDN(null)) }
    override suspend fun sendPayRollToInbox(filters: List<ApiFilterDN>): Flow<PayRollInboxDN> = flow { emit(PayRollInboxDN(null)) }
    override suspend fun sendRequestInquirePensionCertificate(filters: List<ApiFilterDN>): Flow<InquirePensionCertificateDN> = flow {}

    override suspend fun saveDisabilityUserInfo(body: DisabilitySaveInfoDN): Flow<DisabilityRequestRefDN?> =
        error("not used in this test")

    override suspend fun finalConfirmDisabilityRequest(
        requestId: Long,
        body: DisabilityFinalConfirmDN
    ): Flow<DisabilityRequestRefDN?> = error("not used in this test")

    override suspend fun saveDocumentDisability(
        requestId: Long,
        body: DisabilitySaveDocumentDN
    ): Flow<String?> = error("not used in this test")

    override suspend fun getMedicalCommissionPdf(lastWorkshop: String): Flow<PdfDownloadDN> =
        error("not used in this test")

    override suspend fun getRegisteredMedicalCommission(
        filters: List<ApiFilterDN>
    ): Flow<List<RegisteredMedicalCommissionDN>> = error("not used in this test")
}
