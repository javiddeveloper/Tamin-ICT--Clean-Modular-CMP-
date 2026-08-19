package com.tamin.taminhamrah.repository.history

import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.tamin.taminhamrah.model.history.UserRoleDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.history.TalfighInfoItemDN
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.model.history.WageDetailDN

class FakeHistoryRepository : HistoryRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")

    var userInfoResult: UserInfoDN = mockInsuredUserInfo()
    var lastSentTypes: Set<HistoryCertificateType>? = null

    var historyJobInfoResult: HistoryJobInfoDN = HistoryJobInfoDN(list = emptyList(), total = 0)

    /**
     * Empty by default so a test that asks for "nothing came back" gets exactly that; a test that
     * wants a career assigns [mockTalfighInfo] / [mockDastmozdInfo].
     */
    var talfighResult: TalfighInfoDN = TalfighInfoDN(list = emptyList(), total = 0)
    var dastmozdResult: DastmozdInfoDN = DastmozdInfoDN(list = emptyList(), total = 0)

    var lastTalfighFilters: List<ApiFilterDN>? = null
    var lastDastmozdFilters: List<ApiFilterDN>? = null

    override suspend fun getTalfighInfos(filters: List<ApiFilterDN>): TalfighInfoDN {
        if (shouldThrowError) throw error
        lastTalfighFilters = filters
        return talfighResult
    }

    override suspend fun getDastmozdInfos(filters: List<ApiFilterDN>): DastmozdInfoDN {
        if (shouldThrowError) throw error
        lastDastmozdFilters = filters
        return dastmozdResult
    }

    override suspend fun getUserInfos(): UserInfoDN {
        if (shouldThrowError) throw error
        return userInfoResult
    }

    var userRoleResult: UserRoleDN = UserRoleDN.INSURED

    override suspend fun getUserRole(): UserRoleDN {
        if (shouldThrowError) throw error
        return userRoleResult
    }

    override fun downloadHistoryReport(type: HistoryCertificateType): Flow<PdfDownloadDN> =
        flow {
            if (shouldThrowError) throw error
            emit(PdfDownloadDN())
        }

    /** What `sendeblagh` answered; null stands for a service that confirmed without wording. */
    var noticeResult: String? = "سوابق شما برای موسسات ارسال شد."
    var noticeCalls: Int = 0
        private set

    override suspend fun sendHistoryNotice(): String? {
        noticeCalls++
        if (shouldThrowError) throw error
        return noticeResult
    }

    override suspend fun sendToInstitution(selectedTypes: Set<HistoryCertificateType>) {
        if (shouldThrowError) throw error
        lastSentTypes = selectedTypes
    }

    override suspend fun getHistoryJobInfos(filters: List<ApiFilterDN>): Flow<HistoryJobInfoDN> =
        flow {
            if (shouldThrowError) throw error
            emit(historyJobInfoResult)
        }
}

private fun emptyUserInfoDN() = UserInfoDN(
    serial1 = null,
    militaryServiceCode = null,
    fatherName = null,
    lastName = null,
    serial2 = null,
    creationTime = null,
    lastModificationTime = null,
    cityCode = null,
    socialSecurityNumber = null,
    lastModifiedBy = null,
    issueplaceName = null,
    birthDate = null,
    firstName = null,
    insuranceNumber = null,
    genderCode = null,
    nationalID = null,
    marriageCode = null,
    createdBy = null,
    identityNumber = null,
    countryCode = null,
    id = null,
    birthDateTimestamp = null,
    issueplace = null,
    nationCode = null
)

/**
 * A career the way the services actually report one: two employers, one of them a scheme with no
 * workshop name of its own, and a year they overlap in.
 *
 * Shaped from a real production response rather than invented, so a test that passes against it is
 * a test that would have caught what production did.
 */
fun mockTalfighInfo(): TalfighInfoDN = TalfighInfoDN(
    total = 2,
    list = listOf(
        // Only the first row carries the career totals — the service's own quirk.
        mockTalfighYear(
            year = "1404",
            months = listOf("0", "0", "0", "4", "31", "31", "30", "30", "30", "30", "30", "29"),
            historyYears = 1,
            historyMonths = 9,
            historyDays = 1,
            sumHistoryYears = 631,
        ),
        mockTalfighYear(
            year = "1403",
            months = listOf("0", "0", "0", "0", "0", "0", "0", "19", "0", "0", "0", "0"),
        ),
    ),
)

fun mockTalfighYear(
    year: String,
    months: List<String>,
    historyYears: Int? = null,
    historyMonths: Int? = null,
    historyDays: Int? = null,
    sumHistoryYears: Int? = null,
): TalfighInfoItemDN = TalfighInfoItemDN(
    months = months,
    risuid = "0081631829",
    historyYears = historyYears,
    historyMonths = historyMonths,
    sumYear = months.sumOf { it.toIntOrNull() ?: 0 },
    historyDays = historyDays,
    sumHistoryYears = sumHistoryYears,
    id = year.toIntOrNull() ?: 0,
    hisYear = year,
)

fun mockDastmozdInfo(): DastmozdInfoDN = DastmozdInfoDN(
    total = 2,
    list = listOf(
        mockWorkshopYear(
            year = "1404",
            name = "شركت صنايع دما بخار مشهد",
            type = "كاركرد عادي  ليست",
            branch = "هفت مشهد، توس",
            workshopId = "6393610019",
            days = listOf(0, 0, 0, 4, 31, 31, 30, 30, 30, 30, 30, 29),
            wage = "135082560",
        ),
        // A scheme, not an employer: no workshop name and no workshop number on the wire.
        mockWorkshopYear(
            year = "1404",
            name = "",
            type = "بيمه هاي اجتماعي کارگران ساختماني مصوب 1386",
            branch = "چناران",
            workshopId = "",
            days = listOf(31, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            wage = "64000000",
        ),
    ),
)

fun mockWorkshopYear(
    year: String,
    name: String,
    type: String,
    branch: String,
    workshopId: String,
    days: List<Int>,
    wage: String,
): DastmozdInfoItemDN = DastmozdInfoItemDN(
    wageDetails = days.map { WageDetailDN(month = it.toString(), wage = if (it > 0) wage else "0") },
    hisyear = year,
    id = year.toIntOrNull() ?: 0,
    risufname = null,
    risubirthdate = null,
    risuidserial2 = null,
    risuidserial1 = null,
    rwshname = name,
    expcitycode = null,
    brhcode = "5750",
    risuidno = null,
    risudname = null,
    risuid = null,
    risulname = null,
    risunatcode = null,
    brhname = branch,
    historytypedesc = type,
    rwshid = workshopId,
)

/** Someone the history endpoints answer for. */
fun mockInsuredUserInfo(): UserInfoDN = emptyUserInfoDN().copy(
    firstName = "حمید",
    lastName = "چیذاز",
    nationalID = "0946168113",
    insuranceNumber = "0081631829",
    birthDate = "1366/09/26",
)
