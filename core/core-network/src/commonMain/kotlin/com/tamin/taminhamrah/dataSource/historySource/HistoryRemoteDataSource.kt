package com.tamin.taminhamrah.dataSource.historySource

import com.tamin.taminhamrah.model.history.DastmozdInfoDTO
import com.tamin.taminhamrah.model.history.UserInfoDTO
import com.tamin.taminhamrah.model.history.HistoryJobInfoDTO
import com.tamin.taminhamrah.model.history.TalfighInfoDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN

interface HistoryRemoteDataSource {
    suspend fun getTalfighInfos(query: ApiQueryParamDN): TalfighInfoDTO
    suspend fun getDastmozdInfos(query: ApiQueryParamDN): DastmozdInfoDTO
    suspend fun getUserInfos(): UserInfoDTO
    suspend fun sendToInstitution(type1: Boolean, type2: Boolean, type3: Boolean)
    suspend fun getHistoryJobInfos(query: ApiQueryParamDN): HistoryJobInfoDTO
}

