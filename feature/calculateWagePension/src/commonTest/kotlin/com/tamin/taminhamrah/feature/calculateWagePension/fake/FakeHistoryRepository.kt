package com.tamin.taminhamrah.feature.calculateWagePension.fake

import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoItemDN
import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.history.UserRoleDN
import com.tamin.taminhamrah.model.history.WageDetailDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeHistoryRepository : HistoryRepository {
    var talfighResult: TalfighInfoDN = sampleTalfigh()
    var dastmozdResult: DastmozdInfoDN = sampleDastmozd()
    var talfighError: Throwable? = null
    var dastmozdError: Throwable? = null

    var talfighCalls: Int = 0
        private set
    var dastmozdCalls: Int = 0
        private set

    override suspend fun getTalfighInfos(filters: List<ApiFilterDN>): TalfighInfoDN {
        talfighCalls++
        talfighError?.let { throw it }
        return talfighResult
    }

    override suspend fun getDastmozdInfos(filters: List<ApiFilterDN>): DastmozdInfoDN {
        dastmozdCalls++
        dastmozdError?.let { throw it }
        return dastmozdResult
    }

    override suspend fun getUserInfos(): UserInfoDN = error("not used")
    override suspend fun getUserRole(): UserRoleDN = error("not used")
    override suspend fun sendToInstitution(selectedTypes: Set<HistoryCertificateType>) = error("not used")
    override suspend fun sendHistoryNotice(): String? = error("not used")
    override fun downloadHistoryReport(type: HistoryCertificateType): Flow<PdfDownloadDN> =
        error("not used")
    override suspend fun getHistoryJobInfos(filters: List<ApiFilterDN>): Flow<HistoryJobInfoDN> =
        flow { error("not used") }
}

internal fun sampleTalfigh(
    historyYears: Int = 10,
    historyMonths: Int = 4,
    historyDays: Int = 5,
    sumHistoryYears: Int = 3650,
) = TalfighInfoDN(
    list = listOf(
        TalfighInfoItemDN(
            months = emptyList(),
            risuid = "0033261750",
            historyYears = historyYears,
            historyMonths = historyMonths,
            sumYear = 107,
            historyDays = historyDays,
            sumHistoryYears = sumHistoryYears,
            id = 1,
            hisYear = "1393",
        ),
    ),
    total = 1,
)

internal fun sampleDastmozd(wage: String = "1000") = DastmozdInfoDN(
    list = listOf(yearItem(wage), yearItem(wage)),
    total = 2,
)

private fun yearItem(wage: String) = DastmozdInfoItemDN(
    wageDetails = List(12) { WageDetailDN(month = "31", wage = wage) },
    hisyear = "1402",
    id = 1,
    risufname = null,
    risubirthdate = null,
    risuidserial2 = null,
    risuidserial1 = null,
    rwshname = null,
    expcitycode = null,
    brhcode = null,
    risuidno = null,
    risudname = null,
    risuid = null,
    risulname = null,
    risunatcode = null,
    brhname = null,
    historytypedesc = null,
    rwshid = null,
)
