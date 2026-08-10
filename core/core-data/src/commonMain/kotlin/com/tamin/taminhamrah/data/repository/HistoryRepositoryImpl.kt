package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.dataSource.historySource.HistoryRemoteDataSource
import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.HistoryRepository

class HistoryRepositoryImpl(
    private val remoteDataSource: HistoryRemoteDataSource
) : HistoryRepository {
    override suspend fun getTalfighInfos(
        filters: List<ApiFilterDN>
    ): TalfighInfoDN {
        val query = ApiQueryParamDN(filters = filters)
        return remoteDataSource.getTalfighInfos(query).toDomain()
    }

    override suspend fun getDastmozdInfos(
        filters: List<ApiFilterDN>
    ): DastmozdInfoDN {
        val query = ApiQueryParamDN(filters = filters)
        return remoteDataSource.getDastmozdInfos(query).toDomain()
    }

    override suspend fun getHistoryJobInfos(
        filters: List<ApiFilterDN>
    ): HistoryJobInfoDN {
        val query = ApiQueryParamDN(filters = filters)
        return remoteDataSource.getHistoryJobInfos(query).toDomain()
    }
}
