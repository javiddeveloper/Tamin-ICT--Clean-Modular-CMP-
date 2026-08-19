package com.tamin.taminhamrah.repository.historyObjection

import com.tamin.taminhamrah.model.historyObjection.NotExistRequestDN
import kotlinx.coroutines.flow.Flow

interface HistoryObjectionRepository {
    fun checkStatusNotExist(): Flow<Boolean>
    fun getNotExistRequests(): Flow<List<NotExistRequestDN>>
}
