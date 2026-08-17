package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeHistoryRepository : HistoryRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Fake History Repository Error")

    var getTalfighInfosResult: TalfighInfoDN = TalfighInfoDN(list = emptyList(), total = 0)
    var getDastmozdInfosResult: DastmozdInfoDN = DastmozdInfoDN(list = emptyList(), total = 0)
    var getHistoryJobInfosResult: HistoryJobInfoDN = HistoryJobInfoDN(list = emptyList(), total = 0)

    override suspend fun getTalfighInfos(filters: List<ApiFilterDN>): TalfighInfoDN {
        if (shouldThrowError) throw error
        return getTalfighInfosResult
    }

    override suspend fun getDastmozdInfos(filters: List<ApiFilterDN>): DastmozdInfoDN {
        if (shouldThrowError) throw error
        return getDastmozdInfosResult
    }

    override suspend fun getHistoryJobInfos(filters: List<ApiFilterDN>): Flow<HistoryJobInfoDN> = flow {
        if (shouldThrowError) throw error
        emit(getHistoryJobInfosResult)
    }
}
