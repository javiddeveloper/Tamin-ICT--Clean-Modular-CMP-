package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    suspend fun getTalfighInfos(
        filters: List<ApiFilterDN> = emptyList()
    ): TalfighInfoDN

    suspend fun getDastmozdInfos(
        filters: List<ApiFilterDN> = emptyList()
    ): DastmozdInfoDN

    suspend fun getUserInfos(): UserInfoDN

    suspend fun sendToInstitution(selectedTypes: Set<HistoryCertificateType>)

    suspend fun getHistoryJobInfos(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<HistoryJobInfoDN>
}

