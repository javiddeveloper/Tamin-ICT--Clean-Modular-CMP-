package com.tamin.taminhamrah.feature.history.fake

import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.HistoryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * A [HistoryRepository] whose two calls can succeed, fail or be slow independently — which is the
 * whole point, since this screen survives one of them failing.
 */
class FakeHistoryRepository : HistoryRepository {

    var talfighResult: TalfighInfoDN = TalfighInfoDN(list = emptyList(), total = 0)
    var dastmozdResult: DastmozdInfoDN = DastmozdInfoDN(list = emptyList(), total = 0)

    var talfighError: Throwable? = null
    var dastmozdError: Throwable? = null

    /** Virtual milliseconds each call takes, for asserting they run together rather than in turn. */
    var talfighDelayMs: Long = 0
    var dastmozdDelayMs: Long = 0

    /** How many times each endpoint was actually hit — the only way to see a duplicated load. */
    var talfighCalls: Int = 0
        private set
    var dastmozdCalls: Int = 0
        private set

    override suspend fun getTalfighInfos(filters: List<ApiFilterDN>): TalfighInfoDN {
        talfighCalls++
        if (talfighDelayMs > 0) delay(talfighDelayMs)
        talfighError?.let { throw it }
        return talfighResult
    }

    override suspend fun getDastmozdInfos(filters: List<ApiFilterDN>): DastmozdInfoDN {
        dastmozdCalls++
        if (dastmozdDelayMs > 0) delay(dastmozdDelayMs)
        dastmozdError?.let { throw it }
        return dastmozdResult
    }

    override suspend fun getHistoryJobInfos(filters: List<ApiFilterDN>): Flow<HistoryJobInfoDN> =
        flowOf(HistoryJobInfoDN(list = emptyList(), total = 0))
}
