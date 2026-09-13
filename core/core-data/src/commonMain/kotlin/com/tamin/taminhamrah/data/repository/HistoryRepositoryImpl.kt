package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.data.local.dao.HistoryCacheDao
import com.tamin.taminhamrah.data.local.dao.HistoryJobInfoDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.data.mapper.toUserRole
import com.tamin.taminhamrah.dataSource.historySource.HistoryRemoteDataSource
import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.history.UserRoleDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

/**
 * How many rows «مجموع سوابق» asks for in one go.
 *
 * These two endpoints are not paged, and cannot be: the screen merges the years an employer reports
 * more than once and adds up a career total, so a page boundary would split a year in half and make
 * both figures wrong. The previous app sent the same 60 for the same reason — its own comment on
 * the endpoint reads "response of this API used for chart ⇒ can not use lazy load" — while
 * [ApiQueryParamDN]'s default of 10 is meant for genuinely paged lists.
 */
private const val UNPAGED_HISTORY_LIMIT = 60

/**
 * The ceiling on a second, wider request.
 *
 * A career cannot plausibly produce more rows than this, and a `total` above it means the response
 * is describing something other than one person's history — worth refusing rather than asking the
 * server for it.
 */
private const val MAX_HISTORY_LIMIT = 500

class HistoryRepositoryImpl(
    private val remoteDataSource: HistoryRemoteDataSource,
    private val historyJobInfoDao: HistoryJobInfoDao,
    private val historyCacheDao: HistoryCacheDao,
) : HistoryRepository {
    /**
     * The years, from the service when it answers and from the last successful load when it does
     * not.
     *
     * A fresh response is written through and becomes the cache; a failed call falls back to what
     * was cached rather than emptying the screen. The failure is only raised when there is nothing
     * cached to show.
     */
    override suspend fun getTalfighInfos(
        filters: List<ApiFilterDN>
    ): TalfighInfoDN {
        val remote = try {
            fetchTalfigh(filters)
        } catch (e: Exception) {
            return cachedTalfigh() ?: throw e
        }

        // Written through only when the service returned rows: `talfighinfos` answers with an empty
        // list for people the wage service reports in full, and caching that would erase a career
        // the cache is holding.
        remote.list?.takeIf { it.isNotEmpty() }?.let { rows ->
            historyCacheDao.replaceYears(rows.mapIndexed { index, row -> row.toEntity(index) })
        }
        return remote
    }

    /** The employers and their wages, cached and recovered exactly as the years are. */
    override suspend fun getDastmozdInfos(
        filters: List<ApiFilterDN>
    ): DastmozdInfoDN {
        val remote = try {
            fetchDastmozd(filters)
        } catch (e: Exception) {
            return cachedDastmozd() ?: throw e
        }

        remote.list?.takeIf { it.isNotEmpty() }?.let { rows ->
            historyCacheDao.replaceWageRows(rows.mapIndexed { index, row -> row.toEntity(index) })
        }
        return remote
    }

    private suspend fun cachedTalfigh(): TalfighInfoDN? =
        historyCacheDao.observeYears().first()
            .takeIf { it.isNotEmpty() }
            ?.let { rows -> TalfighInfoDN(list = rows.map { it.toDomain() }, total = rows.size) }

    private suspend fun cachedDastmozd(): DastmozdInfoDN? =
        historyCacheDao.observeWageRows().first()
            .takeIf { it.isNotEmpty() }
            ?.let { rows -> DastmozdInfoDN(list = rows.map { it.toDomain() }, total = rows.size) }

    /** The network half, including the second, wider request when the first was cut off. */
    private suspend fun fetchTalfigh(filters: List<ApiFilterDN>): TalfighInfoDN {
        val first = remoteDataSource.getTalfighInfos(query(filters, UNPAGED_HISTORY_LIMIT))
        val wider = widerLimitFor(first.total, first.list?.size) ?: return first.toDomain()
        val second = remoteDataSource.getTalfighInfos(query(filters, wider))
        return preferLonger(first, second) { it.list?.size }.toDomain()
    }

    private suspend fun fetchDastmozd(filters: List<ApiFilterDN>): DastmozdInfoDN {
        val first = remoteDataSource.getDastmozdInfos(query(filters, UNPAGED_HISTORY_LIMIT))
        val wider = widerLimitFor(first.total, first.list?.size) ?: return first.toDomain()
        val second = remoteDataSource.getDastmozdInfos(query(filters, wider))
        return preferLonger(first, second) { it.list?.size }.toDomain()
    }

    private fun query(filters: List<ApiFilterDN>, limit: Int) =
        ApiQueryParamDN(filters = filters, limit = limit)

    /**
     * The limit to ask again with, or null when the first response already held everything.
     *
     * A long career at several employers produces more than [UNPAGED_HISTORY_LIMIT] rows — one per
     * employer per year on `dastmozdinfos` — and the screen adds those rows up, so a truncated
     * response is not a shorter list but a wrong total. The response says how many there are; a
     * second call for exactly that many costs one round trip in the rare case and nothing in the
     * common one.
     */
    private fun widerLimitFor(total: Int?, received: Int?): Int? {
        val available = total ?: return null
        val got = received ?: return null
        return if (available in (got + 1)..MAX_HISTORY_LIMIT) available else null
    }

    /**
     * The wider response, but only when it actually carried more.
     *
     * A second request is an optimization, never a replacement: if asking for more comes back with
     * fewer rows, the first response was the good one. Losing a person's history to a speculative
     * retry is worse than missing the overflow it was meant to recover.
     */
    private inline fun <T> preferLonger(first: T, second: T, size: (T) -> Int?): T =
        if ((size(second) ?: 0) >= (size(first) ?: 0)) second else first

    override suspend fun getUserInfos(): UserInfoDN {
        return remoteDataSource.getUserInfos().toDomain()
    }

    override suspend fun getUserRole(): UserRoleDN {
        return remoteDataSource.getLoginInfo().toUserRole()
    }

    override suspend fun sendHistoryNotice(): String? = remoteDataSource.sendHistoryNotice()

    override fun downloadHistoryReport(type: HistoryCertificateType): Flow<PdfDownloadDN> = flow {
        emit(remoteDataSource.downloadHistoryReport(type).toDomain())
    }

    override suspend fun sendToInstitution(selectedTypes: Set<HistoryCertificateType>) {
        remoteDataSource.sendToInstitution(
            allHistorySelected = HistoryCertificateType.ALL in selectedTypes,
            historyAndWageSelected = HistoryCertificateType.WAGES in selectedTypes,
            combineHistorySelected = HistoryCertificateType.COMBINED in selectedTypes,
        )
    }

    override suspend fun getHistoryJobInfos(
        filters: List<ApiFilterDN>
    ): Flow<HistoryJobInfoDN> = flow {
        val cachedEntities = historyJobInfoDao.getAllJobInfos().firstOrNull()
        if (!cachedEntities.isNullOrEmpty()) {
            emit(HistoryJobInfoDN(list = cachedEntities.map { it.toDomain() }, total = cachedEntities.size))
        }

        try {
            val query = ApiQueryParamDN(filters = filters)
            val remoteDto = remoteDataSource.getHistoryJobInfos(query)
            val remoteList = remoteDto.list ?: emptyList()

            if (remoteList.isNotEmpty()) {
                historyJobInfoDao.clearAll()
                historyJobInfoDao.insertJobInfos(remoteList.map { it.toEntity() })
            }

            emit(remoteDto.toDomain())
        } catch (e: Exception) {
            if (cachedEntities.isNullOrEmpty()) {
                throw e
            }
        }
    }
}

