package com.tamin.taminhamrah.data.repository.history

import com.tamin.taminhamrah.data.local.dao.HistoryJobInfoDao
import com.tamin.taminhamrah.data.local.entity.HistoryJobInfoEntity
import com.tamin.taminhamrah.data.repository.HistoryRepositoryImpl
import com.tamin.taminhamrah.dataSource.historySource.HistoryRemoteDataSource
import com.tamin.taminhamrah.model.history.DastmozdInfoDTO
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDTO
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.HistoryJobInfoDTO
import com.tamin.taminhamrah.model.history.TalfighInfoDTO
import com.tamin.taminhamrah.model.history.TalfighInfoItemDTO
import com.tamin.taminhamrah.model.history.UserInfoDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.flow.first

/**
 * The page size these two endpoints are asked for is not cosmetic.
 *
 * `talfighinfos` answered 500 to the default `limit=10` in production; the previous app has always
 * sent 60 and its own comment says the response feeds a chart and "can not use lazy load". A page
 * boundary would also split a year across two responses, and this screen adds years up.
 */
class HistoryRepositoryImplTest {

    private val remote = RecordingRemoteDataSource()
    private val cache = InMemoryHistoryCacheDao()
    private val repository = HistoryRepositoryImpl(remote, NoJobInfoDao(), cache)

    /**
     * The point of the cache: the page keeps working on the last good load.
     *
     * The service is asked first — this is cache-backed, not cache-only — and only a failure falls
     * back to what was stored.
     */
    @Test
    fun talfighInfos_whenTheServiceFails_fallsBackToTheCachedYears() = runTest {
        remote.talfighResult = TalfighInfoDTO(list = listOf(yearDto("1404")), total = 1)
        repository.getTalfighInfos()
        assertEquals(1, cache.observeYears().first().size, "the good load was written through")

        remote.talfighError = IllegalStateException("offline")
        val cached = repository.getTalfighInfos()

        assertEquals(listOf("1404"), cached.list?.map { it.hisYear })
    }

    /** With nothing stored there is nothing to fall back on, so the failure has to surface. */
    @Test
    fun talfighInfos_whenTheServiceFailsAndNothingIsCached_raisesTheFailure() = runTest {
        remote.talfighError = IllegalStateException("offline")

        assertFailsWith<IllegalStateException> { repository.getTalfighInfos() }
    }

    /**
     * Seen in production: `talfighinfos` answers `{"total":0,"list":[]}` for people the wage service
     * reports in full. Caching that would erase a career the cache was holding.
     */
    @Test
    fun talfighInfos_anEmptyResponseDoesNotWipeTheCache() = runTest {
        remote.talfighResult = TalfighInfoDTO(list = listOf(yearDto("1404")), total = 1)
        repository.getTalfighInfos()

        remote.talfighResult = TalfighInfoDTO(list = emptyList(), total = 0)
        repository.getTalfighInfos()

        remote.talfighError = IllegalStateException("offline")
        assertEquals(listOf("1404"), repository.getTalfighInfos().list?.map { it.hisYear })
    }

    /** Reloading the same years replaces them; the cache must not grow a copy each time. */
    @Test
    fun talfighInfos_reloadingTheSameYearsDoesNotDuplicateThem() = runTest {
        remote.talfighResult = TalfighInfoDTO(list = listOf(yearDto("1404")), total = 1)
        repository.getTalfighInfos()
        repository.getTalfighInfos()

        remote.talfighError = IllegalStateException("offline")
        assertEquals(1, repository.getTalfighInfos().list?.size, "one row, not two")
    }

    @Test
    fun dastmozdInfos_whenTheServiceFails_fallsBackToTheCachedRows() = runTest {
        remote.dastmozdResult = DastmozdInfoDTO(list = listOf(wageDto(1, "1404")), total = 1)
        repository.getDastmozdInfos()

        remote.dastmozdError = IllegalStateException("offline")
        val cached = repository.getDastmozdInfos()

        assertEquals(listOf("1404"), cached.list?.map { it.hisyear })
    }

    private fun yearDto(year: String) = TalfighInfoItemDTO(
        hisYear = year, hisMonth1 = "31", hisMonth2 = "0", hisMonth3 = "0", hisMonth4 = "0",
        hisMonth5 = "0", hisMonth6 = "0", hisMonth7 = "0", hisMonth8 = "0", hisMonth9 = "0",
        hisMonth10 = "0", hisMonth11 = "0", hisMonth12 = "0", historyYears = 1, historyMonths = 0,
        historyDays = 0, sumHistoryYears = 31, sumYear = 31, id = 1, risuid = null,
    )

    private fun wageDto(id: Int, year: String) = DastmozdInfoItemDTO(
        id = id, hisyear = year, hismon1 = "31", hiswage1 = "1000", rwshname = "کارگاه",
        brhname = "چناران", historytypedesc = "کارگری", rwshid = "1",
    )

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

    /**
     * A response cut off at the limit is not a shorter list, it is a wrong total — the screen adds
     * these rows up. The envelope says how many exist, so the repository asks again for all of them.
     */
    @Test
    fun aTruncatedResponseIsFetchedAgainInFull() = runTest {
        remote.reportedTotal = 84
        remote.rowsReturned = 84

        val result = repository.getTalfighInfos()

        assertEquals(listOf(60, 84), remote.talfighLimits, "asked again for exactly what exists")
        assertEquals(84, result.list?.size)
    }

    @Test
    fun aCompleteResponseIsNotFetchedTwice() = runTest {
        remote.reportedTotal = 12
        remote.rowsReturned = 12

        repository.getDastmozdInfos()

        assertEquals(listOf(60), remote.dastmozdLimits, "one call is enough")
    }

    /** A total beyond any plausible career describes something other than one person's history. */
    @Test
    fun anImplausibleTotalIsNotChased() = runTest {
        remote.reportedTotal = 50_000
        remote.rowsReturned = 50_000

        repository.getTalfighInfos()

        assertEquals(listOf(60), remote.talfighLimits, "the second request is refused")
    }

    private class RecordingRemoteDataSource : HistoryRemoteDataSource {
        var lastTalfighQuery: ApiQueryParamDN? = null
        var lastDastmozdQuery: ApiQueryParamDN? = null
        var talfighError: Throwable? = null
        var dastmozdError: Throwable? = null
        var talfighCalls = 0
        var talfighResult = TalfighInfoDTO(list = emptyList(), total = 0)
        var dastmozdResult = DastmozdInfoDTO(list = emptyList(), total = 0)
        val talfighLimits = mutableListOf<Int>()
        val dastmozdLimits = mutableListOf<Int>()

        /** `total` the responses report, against a list of [rowsReturned] rows. */
        var reportedTotal: Int = 0
        var rowsReturned: Int = 0

        override suspend fun getTalfighInfos(query: ApiQueryParamDN): TalfighInfoDTO {
            talfighCalls++
            lastTalfighQuery = query
            talfighError?.let { throw it }
            talfighLimits += query.limit
            return TalfighInfoDTO(
                list = List(rowsReturned.coerceAtMost(query.limit)) { TalfighInfoItemDTO() },
                total = reportedTotal,
            )
        }

        override suspend fun getDastmozdInfos(query: ApiQueryParamDN): DastmozdInfoDTO {
            lastDastmozdQuery = query
            dastmozdError?.let { throw it }
            dastmozdLimits += query.limit
            return DastmozdInfoDTO(
                list = List(rowsReturned.coerceAtMost(query.limit)) { DastmozdInfoItemDTO() },
                total = reportedTotal,
            )
        }

        override suspend fun getHistoryJobInfos(query: ApiQueryParamDN): HistoryJobInfoDTO =
            HistoryJobInfoDTO(list = emptyList(), total = 0)

        override suspend fun getUserInfos(): UserInfoDTO = UserInfoDTO(
            serial1 = null, militaryServiceCode = null, fatherName = null, lastName = null,
            serial2 = null, creationTime = null, lastModificationTime = null, cityCode = null,
            socialSecurityNumber = null, lastModifiedBy = null, issueplaceName = null,
            birthDate = null, firstName = null, insuranceNumber = null, genderCode = null,
            nationalID = null, marriageCode = null, createdBy = null, identityNumber = null,
            countryCode = null, id = null, birthDateTimestamp = null, issueplace = null,
            nationCode = null,
        )

        override suspend fun getLoginInfo(): ListData<String> =
            ListData(total = 0, list = emptyList())

        override suspend fun downloadHistoryReport(
            type: HistoryCertificateType
        ): PdfDownloadDTO = PdfDownloadDTO()

        override suspend fun sendHistoryNotice(): String? = null

        override suspend fun sendToInstitution(
            allHistorySelected: Boolean,
            historyAndWageSelected: Boolean,
            combineHistorySelected: Boolean
        ) = Unit
    }

    private class NoJobInfoDao : HistoryJobInfoDao {
        override fun getAllJobInfos(): Flow<List<HistoryJobInfoEntity>> = flowOf(emptyList())
        override suspend fun insertJobInfos(jobInfos: List<HistoryJobInfoEntity>) = Unit
        override suspend fun clearAll() = Unit
    }

}
