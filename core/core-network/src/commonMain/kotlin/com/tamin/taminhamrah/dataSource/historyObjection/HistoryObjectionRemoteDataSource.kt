package com.tamin.taminhamrah.dataSource.historyObjection

interface HistoryObjectionRemoteDataSource {
    suspend fun checkStatusNotExist(): Boolean
}
