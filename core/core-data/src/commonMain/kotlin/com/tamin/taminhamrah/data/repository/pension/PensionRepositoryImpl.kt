package com.tamin.taminhamrah.data.repository.pension

import com.tamin.taminhamrah.data.mapper.*
import com.tamin.taminhamrah.dataSource.pension.PensionRemoteDataSource
import com.tamin.taminhamrah.model.pension.EdictPensionerDN
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.pension.PayRollDN
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDN
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequestDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDN
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoDN
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

    override suspend fun getPensionerPayRoll(filters: List<ApiFilterDN>): Flow<PayRollDN> = flow {
        val remoteData = pensionRemoteDataSource.getPensionerPayRoll(filters)
        emit(remoteData.toDomain())
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

    override suspend fun getRetirementRequestInfo(filters: List<ApiFilterDN>): Flow<List<RetirementRequestDN>> = flow {
        val remoteData = pensionRemoteDataSource.getRetirementRequestInfo(filters)
        emit(remoteData.list?.map { it.toDomain() } ?: emptyList())
    }

    override suspend fun checkRetirementStatus(): Flow<RetirementStatusDN> = flow {
        val remoteData = pensionRemoteDataSource.checkRetirementStatus()
        emit(remoteData.toDomain())
    }

    override suspend fun authenticationAndGetPersonalInfo(authenticationsCode: Long): Flow<RetirementPersonalDN> =
        flow {
            val remoteData =
                pensionRemoteDataSource.authenticationAndGetPersonalInfo(authenticationsCode)
            emit(remoteData.toDomain())
        }
}
