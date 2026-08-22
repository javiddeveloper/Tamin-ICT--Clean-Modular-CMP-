package com.tamin.taminhamrah.repository.historyObjection

import com.tamin.taminhamrah.model.historyObjection.NotExistRequestDN
import com.tamin.taminhamrah.model.historyObjection.SaveNotExistRequestDN
import kotlinx.coroutines.flow.Flow

interface HistoryObjectionRepository {
    fun checkStatusNotExist(): Flow<Boolean>
    fun getNotExistRequests(): Flow<List<NotExistRequestDN>>
    fun saveNotExist(request: SaveNotExistRequestDN): Flow<Boolean>
    fun deleteNotExist(requestNumber: String, rowIndex: String): Flow<Boolean>
    fun confirmNotExist(description: String?): Flow<Boolean>
    fun finalConfirmNotExist(): Flow<String>
}
