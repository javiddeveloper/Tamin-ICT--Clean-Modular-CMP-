package com.tamin.taminhamrah.data.repository.pension

import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.dataSource.pension.PensionRemoteDataSource
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import com.tamin.taminhamrah.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PensionRepositoryImpl(
    private val pensionRemoteDataSource: PensionRemoteDataSource
) : PensionRepository {

    override suspend fun getPensionInquiry(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<List<PensionInquiryDN>> = flow {
        val queryParam = ApiQueryParamDN(
            page = page.toIntOrNull() ?: 0,
            start = start.toIntOrNull() ?: 0,
            limit = limit.toIntOrNull() ?: 10,
            // Assuming filter and sort need more parsing if they are complex strings,
            // but for now, following the pattern if any.
        )
        val remoteData = pensionRemoteDataSource.getPensionInquiry(queryParam)
        Logger.d(tag = "getPensionInquiry" , message =  "the list is ${remoteData.list?.toString() }" )
        emit(remoteData.list?.map { it.toDomain() } ?: emptyList())
    }

    override suspend fun getPensionerId(): Flow<List<PensionIdDN>> = flow {
        val remoteData = pensionRemoteDataSource.getPensionerId()
        emit(remoteData.map { it.toDomain() })
    }
}
