package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN

interface HistoryRepository {
    suspend fun getTalfighInfos(
        filters: List<ApiFilterDN> = emptyList()
    ): TalfighInfoDN

    suspend fun getDastmozdInfos(
        filters: List<ApiFilterDN> = emptyList()
    ): DastmozdInfoDN
}
