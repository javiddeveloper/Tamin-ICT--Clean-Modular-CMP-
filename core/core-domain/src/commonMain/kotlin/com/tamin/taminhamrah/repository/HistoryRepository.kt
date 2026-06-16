package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN

interface HistoryRepository {
    suspend fun getTalfighInfos(query: ApiQueryParamDN): TalfighInfoDN
}
