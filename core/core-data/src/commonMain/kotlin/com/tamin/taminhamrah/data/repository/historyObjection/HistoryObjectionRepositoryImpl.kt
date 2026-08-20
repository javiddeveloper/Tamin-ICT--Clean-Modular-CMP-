package com.tamin.taminhamrah.data.repository.historyObjection

import com.tamin.taminhamrah.data.mapper.toDTO
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.dataSource.historyObjection.HistoryObjectionRemoteDataSource
import com.tamin.taminhamrah.model.historyObjection.NotExistRequestDN
import com.tamin.taminhamrah.model.historyObjection.SaveNotExistRequestDN
import com.tamin.taminhamrah.repository.historyObjection.HistoryObjectionRepository
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class HistoryObjectionRepositoryImpl(
    private val historyObjectionRemoteDataSource: HistoryObjectionRemoteDataSource,
    private val apiQueryBuilder: ApiQueryBuilder,
) : HistoryObjectionRepository {

    override fun checkStatusNotExist(): Flow<Boolean> = flow {
        emit(historyObjectionRemoteDataSource.checkStatusNotExist())
    }

    override fun getNotExistRequests(): Flow<List<NotExistRequestDN>> = flow {
        val response = historyObjectionRemoteDataSource.getNotExistRequests(apiQueryBuilder.defaultQuery())
        emit(response.list.orEmpty().map { it.toDomain() })
    }

    override fun saveNotExist(request: SaveNotExistRequestDN): Flow<Boolean> = flow {
        emit(historyObjectionRemoteDataSource.saveNotExist(request.toDTO()))
    }

    override fun deleteNotExist(requestNumber: String, rowIndex: String): Flow<Boolean> = flow {
        emit(historyObjectionRemoteDataSource.deleteNotExist(requestNumber, rowIndex))
    }
}
