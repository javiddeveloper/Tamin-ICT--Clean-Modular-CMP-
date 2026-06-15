package com.tamin.taminhamrah.data.repository.pension

import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.dataSource.pension.PensionRemoteDataSource
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import com.tamin.taminhamrah.util.Logger
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
}
