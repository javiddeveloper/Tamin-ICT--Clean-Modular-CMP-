package com.tamin.taminhamrah.data.repository.pension

import com.tamin.taminhamrah.data.mapper.*
import com.tamin.taminhamrah.dataSource.pension.PensionRemoteDataSource
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
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestCreatedDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestFormDN
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PensionRepositoryImpl(
    private val pensionRemoteDataSource: PensionRemoteDataSource
) : PensionRepository {

    override suspend fun getPensionInquiry(
        filters: List<ApiFilterDN>
    ): Flow<List<PensionInquiryDN>> = flow {
        val remoteData = pensionRemoteDataSource.getPensionInquiry(ApiQueryParamDN(filters = filters))
        emit(remoteData.list?.map { it.toDomain() } ?: emptyList())
    }

    override suspend fun getPensionerId(): Flow<List<PensionIdDN>> = flow {
        val remoteData = pensionRemoteDataSource.getPensionerId()
        emit(remoteData.list?.map { it.toDomain() } ?: emptyList())
    }

    override suspend fun getEdictPensioner(query: ApiQueryParamDN): Flow<EdictPensionerDN?> = flow {
        val remoteData = pensionRemoteDataSource.getEdictPensioner(query)
        emit(remoteData?.toDomain())
    }

    override suspend fun sendRequestDeferredInstallmentCertificate(request: DeferredInstallmentRequestDN): Flow<DeferredInstallmentCertificateDN> = flow {
        val remoteData = pensionRemoteDataSource.sendRequestDeferredInstallmentCertificate(request.toDTO())
        emit(remoteData.toDomain())
    }

    override suspend fun getPensionerPayRoll(filters: List<ApiFilterDN>): Flow<List<PayRollDN>> = flow {
        val remoteData = pensionRemoteDataSource.getPensionerPayRoll(filters)
        emit(remoteData.list?.map { it.toDomain() } ?: emptyList())
    }

    override suspend fun getDisabilityPersonalInfo(): Flow<DisabilityPersonalInfoDN> = flow {
        val remoteData = pensionRemoteDataSource.getDisabilityPersonalInfo()
        emit(remoteData.toDomain())
    }

    override suspend fun getUserAge(filters: List<ApiFilterDN>): Flow<AgeDN> = flow {
        val remoteData = pensionRemoteDataSource.getUserAge(filters)
        emit(remoteData.toDomain())
    }

    override suspend fun pensionerPayRollPDF(filters: List<ApiFilterDN>): Flow<com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN> =
        flow {
            val remoteData = pensionRemoteDataSource.pensionerPayRollPDF(filters)
            emit(remoteData.toDomain())
        }

    override suspend fun getEdictReportPDF(filters: List<ApiFilterDN>): Flow<com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN> =
        flow {
            val remoteData = pensionRemoteDataSource.getEdictReportPDF(filters)
            emit(remoteData.toDomain())
        }

    override suspend fun getRetirementRequestInfo(filters: List<ApiFilterDN>): Flow<List<RetirementRequestDN>> = flow {
        val remoteData = pensionRemoteDataSource.getRetirementRequestInfo(filters)
        emit(remoteData.list?.map { it.toDomain() } ?: emptyList())
    }

    override suspend fun createRetirementRequest(
        authenticationsCode: Long,
        form: RetirementRequestFormDN
    ): Flow<RetirementRequestCreatedDN> = flow {
        val remoteData = pensionRemoteDataSource.createRetirementRequest(
            authenticationsCode = authenticationsCode,
            form = form.toDTO()
        )
        emit(remoteData.toDomain())
    }

    override suspend fun checkRetirementStatus(): Flow<RetirementStatusDN> = flow {
        val remoteData = pensionRemoteDataSource.checkRetirementStatus()
        emit(remoteData.toDomain())
    }

    override suspend fun sendRetirementDocument(
        requestId: String,
        request: RetirementSaveDocumentDN
    ): Flow<String?> = flow {
        val remoteData = pensionRemoteDataSource.sendRetirementDocument(requestId, request.toDTO())
        emit(remoteData)
    }

    override suspend fun authenticationAndGetPersonalInfo(authenticationsCode: Long): Flow<RetirementPersonalDN> =
        flow {
            val remoteData =
                pensionRemoteDataSource.authenticationAndGetPersonalInfo(authenticationsCode)
            emit(remoteData.toDomain())
        }

    override suspend fun getAuthenticationCode(): Flow<AuthenticationTicketDN> = flow {
        val remoteData = pensionRemoteDataSource.getAuthenticationCode()
        emit(remoteData.toDomain())
    }

    override suspend fun sendEdictPensionerToMyInbox(filters: List<ApiFilterDN>): Flow<EdictPensionerInboxDN> =
        flow {
            val remoteData = pensionRemoteDataSource.sendEdictPensionerToMyInbox(filters)
            emit(EdictPensionerInboxDN(message = remoteData))
        }

    override suspend fun sendPayRollToInbox(filters: List<ApiFilterDN>): Flow<PayRollInboxDN> =
        flow {
            val remoteData = pensionRemoteDataSource.sendPayRollToInbox(filters)
            emit(PayRollInboxDN(message = remoteData))
        }

    override suspend fun sendRequestInquirePensionCertificate(filters: List<ApiFilterDN>): Flow<InquirePensionCertificateDN> =
        flow {
            val remoteData = pensionRemoteDataSource.sendRequestInquirePensionCertificate(filters)
            emit(remoteData.toInquirePensionCertificateDomain())
        }

    override suspend fun saveDisabilityUserInfo(body: DisabilitySaveInfoDN): Flow<DisabilityRequestRefDN?> = flow {
        val remoteData = pensionRemoteDataSource.saveDisabilityUserInfo(body.toDTO())
        emit(remoteData.toDomain())
    }

    override suspend fun finalConfirmDisabilityRequest(
        requestId: Long,
        body: DisabilityFinalConfirmDN
    ): Flow<DisabilityRequestRefDN?> = flow {
        val remoteData = pensionRemoteDataSource.finalConfirmDisabilityRequest(requestId, body.toDTO())
        emit(remoteData.toDomain())
    }

    override suspend fun saveDocumentDisability(
        requestId: Long,
        body: DisabilitySaveDocumentDN
    ): Flow<String?> = flow {
        val remoteData = pensionRemoteDataSource.saveDocumentDisability(requestId, body.toDTO())
        emit(remoteData)
    }

    override suspend fun getMedicalCommissionPdf(lastWorkshop: String): Flow<PdfDownloadDN> = flow {
        val remoteData = pensionRemoteDataSource.getMedicalCommissionPdf(lastWorkshop)
        emit(remoteData.toDomain())
    }

    override suspend fun getRegisteredMedicalCommission(
        filters: List<ApiFilterDN>
    ): Flow<List<RegisteredMedicalCommissionDN>> = flow {
        val remoteData = pensionRemoteDataSource.getRegisteredMedicalCommission(ApiQueryParamDN(filters = filters))
        emit(remoteData.list?.map { it.toDomain() } ?: emptyList())
    }
}
