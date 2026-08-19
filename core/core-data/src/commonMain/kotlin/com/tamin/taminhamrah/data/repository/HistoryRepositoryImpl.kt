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
import com.tamin.taminhamrah.model.history.UserRoleDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.HistoryRepository
import com.tamin.taminhamrah.model.history.UserInfoDN
import kotlinx.coroutines.flow.Flow
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
     * Offline-first the way the rest of the app is: a fresh response is written through and becomes
     * the cache, and a failed call falls back to what was cached rather than emptying the screen.
     * The failure is only raised when there is nothing cached to show — at that point the person
     * genuinely has nothing, and silence would be a lie.
     */
    override suspend fun getTalfighInfos(
        filters: List<ApiFilterDN>
    ): TalfighInfoDN {
        val remote = try {
            val first = remoteDataSource.getTalfighInfos(query(filters, UNPAGED_HISTORY_LIMIT))
            val wider = widerLimitFor(first.total, first.list?.size)
            if (wider == null) {
                first
            } else {
                val second = remoteDataSource.getTalfighInfos(query(filters, wider))
                preferLonger(first, second) { it.list?.size }
            }
        } catch (e: Exception) {
            return cachedTalfigh() ?: throw e
        }

        // Written through only when the service actually returned rows: an empty answer is not a
        // reason to throw away a career the cache still holds.
        val domain = remote.toDomain()
        domain.list?.takeIf { it.isNotEmpty() }?.let { rows ->
            historyCacheDao.replaceYears(rows.mapIndexed { index, row -> row.toEntity(index) })
        }
        return domain
    }

    private suspend fun cachedTalfigh(): TalfighInfoDN? =
        historyCacheDao.observeYears().firstOrNull()
            ?.takeIf { it.isNotEmpty() }
            ?.let { rows -> TalfighInfoDN(list = rows.map { it.toDomain() }, total = rows.size) }

    /** The employers and their wages, cached and recovered exactly as the years are. */
    override suspend fun getDastmozdInfos(
        filters: List<ApiFilterDN>
    ): DastmozdInfoDN {
        val remote = try {
            val first = remoteDataSource.getDastmozdInfos(query(filters, UNPAGED_HISTORY_LIMIT))
            val wider = widerLimitFor(first.total, first.list?.size)
            if (wider == null) {
                first
            } else {
                val second = remoteDataSource.getDastmozdInfos(query(filters, wider))
                preferLonger(first, second) { it.list?.size }
            }
        } catch (e: Exception) {
            return cachedDastmozd() ?: throw e
        }

        val domain = remote.toDomain()
        domain.list?.takeIf { it.isNotEmpty() }?.let { rows ->
            historyCacheDao.replaceWageRows(rows.mapIndexed { index, row -> row.toEntity(index) })
        }
        return domain
    }

    private suspend fun cachedDastmozd(): DastmozdInfoDN? =
        historyCacheDao.observeWageRows().firstOrNull()
            ?.takeIf { it.isNotEmpty() }
            ?.let { rows -> DastmozdInfoDN(list = rows.map { it.toDomain() }, total = rows.size) }

    private fun query(filters: List<ApiFilterDN>, limit: Int) =
        ApiQueryParamDN(filters = filters, limit = limit)

    /**
     * The wider response, but only when it actually carried more.
     *
     * A second request is an optimization, never a replacement: if asking for more comes back with
     * fewer rows — a limit the service will not honor, a transient empty answer — the first
     * response was the good one and is what the screen gets. Losing a person's history to a
     * speculative retry is far worse than missing the overflow it was meant to recover.
     */
    private inline fun <T> preferLonger(first: T, second: T, size: (T) -> Int?): T =
        if ((size(second) ?: 0) >= (size(first) ?: 0)) second else first

    /**
     * The limit to ask again with, or null when the first response already held everything.
     *
     * A long career at several employers produces more than [UNPAGED_HISTORY_LIMIT] rows — one per
     * employer per year on `dastmozdinfos` — and the screen adds those rows up, so a truncated
     * response is not a shorter list but a wrong total. The response says how many there are; a
     * second call for exactly that many costs one round trip in the rare case and nothing at all in
     * the common one. The previous app never noticed, and quietly dropped the overflow.
     */
    private fun widerLimitFor(total: Int?, received: Int?): Int? {
        val available = total ?: return null
        val got = received ?: return null
        return if (available in (got + 1)..MAX_HISTORY_LIMIT) available else null
    }

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

