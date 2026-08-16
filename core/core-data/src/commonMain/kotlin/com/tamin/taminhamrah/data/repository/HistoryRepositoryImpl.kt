package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.dataSource.historySource.HistoryRemoteDataSource
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.HistoryRepository
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN

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

    override suspend fun getUserInfos(): UserInfoDN {
        return remoteDataSource.getUserInfos().toDomain()
    }

    override suspend fun sendToInstitution(type1: Boolean, type2: Boolean, type3: Boolean) {
        remoteDataSource.sendToInstitution(type1, type2, type3)
    }
}

