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
        /*
         * Set to serve a specific payload. Left null, the source syntheses rows from
         * [rowsReturned] / [reportedTotal], which is what the paging tests drive it with.
         */
        var talfighResult: TalfighInfoDTO? = null
        var dastmozdResult: DastmozdInfoDTO? = null
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
            return talfighResult ?: TalfighInfoDTO(
                list = List(rowsReturned.coerceAtMost(query.limit)) { TalfighInfoItemDTO() },
                total = reportedTotal,
            )
        }

        override suspend fun getDastmozdInfos(query: ApiQueryParamDN): DastmozdInfoDTO {
            lastDastmozdQuery = query
            dastmozdError?.let { throw it }
            dastmozdLimits += query.limit
            return dastmozdResult ?: DastmozdInfoDTO(
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
