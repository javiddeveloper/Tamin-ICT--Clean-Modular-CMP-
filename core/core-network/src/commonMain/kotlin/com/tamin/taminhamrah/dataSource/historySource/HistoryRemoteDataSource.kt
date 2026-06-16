package com.tamin.taminhamrah.dataSource.historySource

import com.tamin.taminhamrah.model.history.TalfighInfoDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN

interface HistoryRemoteDataSource {
    suspend fun getTalfighInfos(query: ApiQueryParamDN): TalfighInfoDTO
}
