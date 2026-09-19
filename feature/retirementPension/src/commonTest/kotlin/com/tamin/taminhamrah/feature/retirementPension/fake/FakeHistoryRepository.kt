package com.tamin.taminhamrah.feature.retirementPension.fake

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
    var talfighInfoResult: TalfighInfoDN = TalfighInfoDN(
        list = listOf(
            TalfighInfoItemDN(
                historyYears = 20,
                historyMonths = 0,
                historyDays = 0,
                sumHistoryYears = 7300,
                months = emptyList(),
                risuid = null,
                sumYear = null,
                id = null,
                hisYear = null,
            ),
        ),
        total = 1,
    )
    var dastmozdInfoResult: DastmozdInfoDN = DastmozdInfoDN(
        list = listOf(
            DastmozdInfoItemDN(
                wageDetails = List(12) { WageDetailDN("30", "30000000") },
                hisyear = "1402", id = null, risufname = null, risubirthdate = null,
                risuidserial2 = null, risuidserial1 = null, rwshname = null, expcitycode = null,
                brhcode = null, risuidno = null, risudname = null, risuid = null, risulname = null,
                risunatcode = null, brhname = null, historytypedesc = null, rwshid = null,
            ),
            DastmozdInfoItemDN(
                wageDetails = List(12) { WageDetailDN("30", "30000000") },
                hisyear = "1403", id = null, risufname = null, risubirthdate = null,
                risuidserial2 = null, risuidserial1 = null, rwshname = null, expcitycode = null,
                brhcode = null, risuidno = null, risudname = null, risuid = null, risulname = null,
                risunatcode = null, brhname = null, historytypedesc = null, rwshid = null,
            ),
        ),
        total = 2,
    )

    override suspend fun getTalfighInfos(filters: List<ApiFilterDN>): TalfighInfoDN = talfighInfoResult
    override suspend fun getDastmozdInfos(filters: List<ApiFilterDN>): DastmozdInfoDN = dastmozdInfoResult

    override suspend fun getUserInfos(): UserInfoDN = emptyUserInfoDN()
    override suspend fun sendToInstitution(selectedTypes: Set<HistoryCertificateType>) {}
    override suspend fun getHistoryJobInfos(filters: List<ApiFilterDN>): Flow<HistoryJobInfoDN> = flow {}

    // Added to HistoryRepository by the «کلیه سوابق» work; nothing here exercises them.
    override suspend fun getUserRole(): UserRoleDN = error("not used in retirementPension tests")
    override suspend fun sendHistoryNotice(): String? = error("not used in retirementPension tests")
    override fun downloadHistoryReport(
        type: HistoryCertificateType,
    ): Flow<PdfDownloadDN> = error("not used in retirementPension tests")
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
    nationCode = null,
)

