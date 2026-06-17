package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.DastmozdInfoDN

interface HistoryRepository {
    suspend fun getTalfighInfos(
        page: Int = 1,
        limit: Int = 10,
        start: Int = 0
    ): TalfighInfoDN

    suspend fun getDastmozdInfos(
        page: Int = 1,
        limit: Int = 10,
        start: Int = 0
    ): DastmozdInfoDN
}
