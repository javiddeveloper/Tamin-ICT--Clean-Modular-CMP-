package com.tamin.taminhamrah.data.repository.historyObjection

import com.tamin.taminhamrah.dataSource.historyObjection.HistoryObjectionRemoteDataSource
import com.tamin.taminhamrah.repository.historyObjection.HistoryObjectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class HistoryObjectionRepositoryImpl(
    private val historyObjectionRemoteDataSource: HistoryObjectionRemoteDataSource,
) : HistoryObjectionRepository {

    override fun checkStatusNotExist(): Flow<Boolean> = flow {
        emit(historyObjectionRemoteDataSource.checkStatusNotExist())
    }
}
