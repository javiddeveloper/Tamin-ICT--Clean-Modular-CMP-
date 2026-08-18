package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.data.local.dao.HistoryJobInfoDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.dataSource.historySource.HistoryRemoteDataSource
import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.HistoryRepository
import com.tamin.taminhamrah.model.history.UserInfoDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

/**
 * How many rows «مجموع سوابق» asks for in one go.
 *
 * These two endpoints are not paged, and cannot be: the screen merges the years an employer reports
 * more than once and adds up a career total, so a page boundary would split a year in half and make
 * both figures wrong. The previous app sent the same 60 for the same reason — its own comment on
 * the endpoint reads "response of this API used for chart ⇒ can not use lazy load" — while
 * [ApiQueryParamDN]'s default of 10 is meant for genuinely paged lists.
 */
private const val UNPAGED_HISTORY_LIMIT = 60

class HistoryRepositoryImpl(
    private val remoteDataSource: HistoryRemoteDataSource,
    private val historyJobInfoDao: HistoryJobInfoDao
) : HistoryRepository {
    override suspend fun getTalfighInfos(
        filters: List<ApiFilterDN>
    ): TalfighInfoDN {
        val query = ApiQueryParamDN(filters = filters, limit = UNPAGED_HISTORY_LIMIT)
        return remoteDataSource.getTalfighInfos(query).toDomain()
    }

    override suspend fun getDastmozdInfos(
        filters: List<ApiFilterDN>
    ): DastmozdInfoDN {
        val query = ApiQueryParamDN(filters = filters, limit = UNPAGED_HISTORY_LIMIT)
        return remoteDataSource.getDastmozdInfos(query).toDomain()
    }

    override suspend fun getUserInfos(): UserInfoDN {
        return remoteDataSource.getUserInfos().toDomain()
    }

    override suspend fun sendToInstitution(selectedTypes: Set<HistoryCertificateType>) {
        remoteDataSource.sendToInstitution(
            allHistorySelected = HistoryCertificateType.ALL in selectedTypes,
            historyAndWageSelected = HistoryCertificateType.WAGES in selectedTypes,
            combineHistorySelected = HistoryCertificateType.COMBINED in selectedTypes,
        )
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

