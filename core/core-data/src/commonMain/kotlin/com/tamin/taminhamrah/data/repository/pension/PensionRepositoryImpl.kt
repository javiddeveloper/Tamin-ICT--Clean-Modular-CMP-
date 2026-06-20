package com.tamin.taminhamrah.data.repository.pension

import com.tamin.taminhamrah.data.mapper.toDTO
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.dataSource.pension.PensionRemoteDataSource
import com.tamin.taminhamrah.model.pension.EdictPensionerDN
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequestDN
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
}
