package com.tamin.taminhamrah.repository.pension

import com.tamin.taminhamrah.model.pension.InquirePensionCertificateDN
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.EdictPensionerDN
import com.tamin.taminhamrah.model.pension.EdictPensionerInboxDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.pension.PayRollDN
import com.tamin.taminhamrah.model.pension.PayRollInboxDN
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDN
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequestDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementSaveDocumentDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityFinalConfirmDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityRequestRefDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveDocumentDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveInfoDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.RegisteredMedicalCommissionDN
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoDN
import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDN
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePensionRepository : PensionRepository {
    var pensionInquiryResult: List<PensionInquiryDN> = emptyList()
    var pensionIdResult: List<PensionIdDN> = emptyList()
    var edictPensionerResult: EdictPensionerDN? = null
    var deferredInstallmentCertificateResult: DeferredInstallmentCertificateDN? = null
    var payRollResult: List<PayRollDN> = emptyList()
    var userAgeResult: AgeDN? = null
    var disabilityPersonalInfoResult: DisabilityPersonalInfoDN? = null
    var edictPdfReportResult : PdfDownloadDN? = null
    var payRollPDFResult: com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN? = null
    var retirementRequestInfoResult: List<RetirementRequestDN> = emptyList()
    var retirementStatusResult: RetirementStatusDN? = null
    var authenticationAndGetPersonalInfoResult: RetirementPersonalDN? = null
    var sendRetirementDocumentResult: String? = null
    var authenticationTicketResult: AuthenticationTicketDN? = null
    var sendEdictPensionerToMyInboxResult: EdictPensionerInboxDN = EdictPensionerInboxDN(null)
    var sendPayRollToInboxResult: PayRollInboxDN = PayRollInboxDN(null)
    var inquirePensionCertificateResult: InquirePensionCertificateDN? = null
    var shouldThrowError: Boolean = false
    var error: Throwable? = null


    override suspend fun getPensionInquiry(
        filters: List<ApiFilterDN>
    ): Flow<List<PensionInquiryDN>> = flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(pensionInquiryResult)
    }

    override suspend fun getPensionerId(): Flow<List<PensionIdDN>> = flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(pensionIdResult)
    }

    override suspend fun getEdictPensioner(query: ApiQueryParamDN): Flow<EdictPensionerDN?> = flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(edictPensionerResult)
    }

    override suspend fun sendRequestDeferredInstallmentCertificate(request: DeferredInstallmentRequestDN): Flow<DeferredInstallmentCertificateDN> = flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(deferredInstallmentCertificateResult!!)
    }

    override suspend fun getPensionerPayRoll(filters: List<ApiFilterDN>): Flow<List<PayRollDN>> = flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(payRollResult)
    }

    override suspend fun getDisabilityPersonalInfo(): Flow<DisabilityPersonalInfoDN> = flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(disabilityPersonalInfoResult!!)
    }

    override suspend fun getUserAge(filters: List<ApiFilterDN>): Flow<AgeDN> = flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(userAgeResult!!)
    }


    override suspend fun pensionerPayRollPDF(filters: List<ApiFilterDN>): Flow<com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN> =
        flow {
            if (shouldThrowError) {
                throw error!!
            }
            emit(payRollPDFResult!!)
        }

    override suspend fun getEdictReportPDF(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> =   flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(edictPdfReportResult!!)
    }


    override suspend fun getRetirementRequestInfo(filters: List<ApiFilterDN>): Flow<List<RetirementRequestDN>> = flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(retirementRequestInfoResult)
    }

    override suspend fun checkRetirementStatus(): Flow<RetirementStatusDN> = flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(retirementStatusResult!!)
    }

    override suspend fun sendRetirementDocument(
        requestId: String,
        request: RetirementSaveDocumentDN
    ): Flow<String?> = flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(sendRetirementDocumentResult)
    }
    override suspend fun authenticationAndGetPersonalInfo(authenticationsCode: Long): Flow<RetirementPersonalDN> =
        flow {
            if (shouldThrowError) {
                throw error!!
            }
            emit(authenticationAndGetPersonalInfoResult!!)
        }

    override suspend fun getAuthenticationCode(): Flow<AuthenticationTicketDN> = flow {
        if (shouldThrowError) {
            throw error!!
        }
        emit(authenticationTicketResult!!)
    }

    override suspend fun sendEdictPensionerToMyInbox(filters: List<ApiFilterDN>): Flow<EdictPensionerInboxDN> =
        flow {
            if (shouldThrowError) {
                throw error!!
            }
            emit(sendEdictPensionerToMyInboxResult)
        }
    override suspend fun sendPayRollToInbox(filters: List<ApiFilterDN>): Flow<PayRollInboxDN> =
        flow {
            if (shouldThrowError) {
                throw error!!
            }
            emit(sendPayRollToInboxResult)
        }

    override suspend fun sendRequestInquirePensionCertificate(filters: List<ApiFilterDN>): Flow<InquirePensionCertificateDN> =
        flow {
            if (shouldThrowError) {
                throw error!!
            }
            emit(inquirePensionCertificateResult!!)
        }

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
