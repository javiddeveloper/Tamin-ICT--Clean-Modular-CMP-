package com.tamin.taminhamrah.feature.history.fake

import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.history.UserRoleDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.HistoryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlin.time.Duration.Companion.milliseconds

/**
 * A [HistoryRepository] whose calls can succeed, fail or be slow independently — which is the whole
 * point, since this screen survives all but one of them failing.
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
        if (talfighDelayMs > 0) delay(talfighDelayMs.milliseconds)
        talfighError?.let { throw it }
        return talfighResult
    }

    override suspend fun getDastmozdInfos(filters: List<ApiFilterDN>): DastmozdInfoDN {
        dastmozdCalls++
        if (dastmozdDelayMs > 0) delay(dastmozdDelayMs.milliseconds)
        dastmozdError?.let { throw it }
        return dastmozdResult
    }

    /**
     * Insured by default, because that is what every other test is about. A test that wants a
     * مستمری‌بگیر — the one role the screen turns away — sets [UserRoleDN.PENSIONER].
     */
    var userRoleResult: UserRoleDN = UserRoleDN.INSURED
    var userRoleError: Throwable? = null

    override suspend fun getUserRole(): UserRoleDN {
        userRoleError?.let { throw it }
        return userRoleResult
    }

    override suspend fun getUserInfos(): UserInfoDN = error("not used by this screen")

    override suspend fun sendToInstitution(selectedTypes: Set<HistoryCertificateType>) = Unit

    /**
     * The report the download returns, and which one was asked for.
     *
     * No channel in it: draining one belongs to the viewer, and naming the ktor type here would put
     * it on the test module's classpath for no gain.
     */
    var reportResult: PdfDownloadDN = PdfDownloadDN()
    var reportError: Throwable? = null
    var lastReportRequested: HistoryCertificateType? = null
        private set

    override fun downloadHistoryReport(type: HistoryCertificateType): Flow<PdfDownloadDN> = flow {
        lastReportRequested = type
        reportError?.let { throw it }
        emit(reportResult)
    }

    override suspend fun getHistoryJobInfos(filters: List<ApiFilterDN>): Flow<HistoryJobInfoDN> =
        flowOf(HistoryJobInfoDN(list = emptyList(), total = 0))
}
