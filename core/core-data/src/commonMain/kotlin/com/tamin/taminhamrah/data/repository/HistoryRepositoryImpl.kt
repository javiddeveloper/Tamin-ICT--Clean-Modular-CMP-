package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.dataSource.historySource.HistoryRemoteDataSource
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.HistoryRepository
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.model.history.DastmozdInfoDN

class HistoryRepositoryImpl(
    private val remoteDataSource: HistoryRemoteDataSource
) : HistoryRepository {
    override suspend fun getTalfighInfos(
        page: Int,
        limit: Int,
        start: Int
    ): TalfighInfoDN {
        val query = ApiQueryParamDN(page = page, limit = limit, start = start)
        return remoteDataSource.getTalfighInfos(query).toDomain()
    }

    override suspend fun getDastmozdInfos(
        page: Int,
        limit: Int,
        start: Int
    ): DastmozdInfoDN {
        val query = ApiQueryParamDN(page = page, limit = limit, start = start)
        return remoteDataSource.getDastmozdInfos(query).toDomain()
    }
}
