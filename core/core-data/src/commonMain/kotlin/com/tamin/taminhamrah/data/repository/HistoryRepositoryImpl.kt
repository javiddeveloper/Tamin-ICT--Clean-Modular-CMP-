package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.dataSource.historySource.HistoryRemoteDataSource
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.HistoryRepository
import com.tamin.taminhamrah.data.mapper.toDomain

class HistoryRepositoryImpl(
    private val remoteDataSource: HistoryRemoteDataSource
) : HistoryRepository {
    override suspend fun getTalfighInfos(query: ApiQueryParamDN): TalfighInfoDN {
        return remoteDataSource.getTalfighInfos(query).toDomain()
    }
}
