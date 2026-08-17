package com.tamin.taminhamrah.data.repository.history

import com.tamin.taminhamrah.data.local.dao.HistoryJobInfoDao
import com.tamin.taminhamrah.data.local.entity.HistoryJobInfoEntity
import com.tamin.taminhamrah.data.repository.HistoryRepositoryImpl
import com.tamin.taminhamrah.dataSource.historySource.HistoryRemoteDataSource
import com.tamin.taminhamrah.model.history.DastmozdInfoDTO
import com.tamin.taminhamrah.model.history.HistoryJobInfoDTO
import com.tamin.taminhamrah.model.history.TalfighInfoDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The page size these two endpoints are asked for is not cosmetic.
 *
 * `talfighinfos` answered 500 to the default `limit=10` in production; the previous app has always
 * sent 60 and its own comment says the response feeds a chart and "can not use lazy load". A page
 * boundary would also split a year across two responses, and this screen adds years up.
 */
class HistoryRepositoryImplTest {

    private val remote = RecordingRemoteDataSource()
    private val repository = HistoryRepositoryImpl(remote, NoJobInfoDao())

    @Test
    fun talfighInfosIsRequestedUnpaged() = runTest {
        repository.getTalfighInfos()

        assertEquals(60, remote.lastTalfighQuery?.limit, "the years must arrive in one response")
        assertEquals(0, remote.lastTalfighQuery?.page)
        assertEquals(0, remote.lastTalfighQuery?.start)
    }

    @Test
    fun dastmozdInfosIsRequestedUnpaged() = runTest {
        repository.getDastmozdInfos()

        assertEquals(60, remote.lastDastmozdQuery?.limit, "one workshop row per year, all of them")
        assertEquals(0, remote.lastDastmozdQuery?.page)
        assertEquals(0, remote.lastDastmozdQuery?.start)
    }

    private class RecordingRemoteDataSource : HistoryRemoteDataSource {
        var lastTalfighQuery: ApiQueryParamDN? = null
        var lastDastmozdQuery: ApiQueryParamDN? = null

        override suspend fun getTalfighInfos(query: ApiQueryParamDN): TalfighInfoDTO {
            lastTalfighQuery = query
            return TalfighInfoDTO(list = emptyList(), total = 0)
        }

        override suspend fun getDastmozdInfos(query: ApiQueryParamDN): DastmozdInfoDTO {
            lastDastmozdQuery = query
            return DastmozdInfoDTO(list = emptyList(), total = 0)
        }

        override suspend fun getHistoryJobInfos(query: ApiQueryParamDN): HistoryJobInfoDTO =
            HistoryJobInfoDTO(list = emptyList(), total = 0)
    }

    private class NoJobInfoDao : HistoryJobInfoDao {
        override fun getAllJobInfos(): Flow<List<HistoryJobInfoEntity>> = flowOf(emptyList())
        override suspend fun insertJobInfos(items: List<HistoryJobInfoEntity>) = Unit
        override suspend fun clearAll() = Unit
    }
}
