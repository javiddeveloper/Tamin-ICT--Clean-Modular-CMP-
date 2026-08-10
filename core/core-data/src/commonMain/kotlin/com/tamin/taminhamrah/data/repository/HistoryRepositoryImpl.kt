package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.data.local.dao.HistoryJobInfoDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.dataSource.historySource.HistoryRemoteDataSource
import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class HistoryRepositoryImpl(
    private val remoteDataSource: HistoryRemoteDataSource,
    private val historyJobInfoDao: HistoryJobInfoDao
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
    ): Flow<HistoryJobInfoDN> = flow {
        val cachedEntities = historyJobInfoDao.getAllJobInfos().firstOrNull()
        if (!cachedEntities.isNullOrEmpty()) {
            emit(HistoryJobInfoDN(list = cachedEntities.map { it.toDomain() }, total = cachedEntities.size))
        }

        try {
            val query = ApiQueryParamDN(filters = filters)
            val remoteDto = remoteDataSource.getHistoryJobInfos(query)
            val remoteList = remoteDto.list ?: emptyList()

            if (remoteList.isNotEmpty()) {
                historyJobInfoDao.clearAll()
                historyJobInfoDao.insertJobInfos(remoteList.map { it.toEntity() })
            }

            emit(remoteDto.toDomain())
        } catch (e: Exception) {
            if (cachedEntities.isNullOrEmpty()) {
                throw e
            }
        }
    }
}
