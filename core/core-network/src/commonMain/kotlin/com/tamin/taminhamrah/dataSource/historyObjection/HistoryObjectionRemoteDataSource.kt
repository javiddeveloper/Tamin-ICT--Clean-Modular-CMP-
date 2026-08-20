package com.tamin.taminhamrah.dataSource.historyObjection

import com.tamin.taminhamrah.model.historyObjection.NotExistRequestDTO
import com.tamin.taminhamrah.model.historyObjection.SaveNotExistRequestDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData

interface HistoryObjectionRemoteDataSource {
    suspend fun checkStatusNotExist(): Boolean
    suspend fun getNotExistRequests(query: ApiQueryParamDN): ListData<NotExistRequestDTO>
    suspend fun saveNotExist(request: SaveNotExistRequestDTO): Boolean
    suspend fun deleteNotExist(requestNumber: String, rowIndex: String): Boolean
}
