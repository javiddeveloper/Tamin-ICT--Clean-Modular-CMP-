package com.tamin.taminhamrah.repository.history

import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import com.tamin.taminhamrah.model.history.UserRoleDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN

class FakeHistoryRepository : HistoryRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")

    var userInfoResult: UserInfoDN = emptyUserInfoDN()
    var lastSentTypes: Set<HistoryCertificateType>? = null

    var historyJobInfoResult: HistoryJobInfoDN = HistoryJobInfoDN(list = emptyList(), total = 0)

    override suspend fun getTalfighInfos(filters: List<ApiFilterDN>): TalfighInfoDN =
        TalfighInfoDN(list = emptyList(), total = 0)

    override suspend fun getDastmozdInfos(filters: List<ApiFilterDN>): DastmozdInfoDN =
        DastmozdInfoDN(list = emptyList(), total = 0)

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
        kotlinx.coroutines.flow.flow {
            if (shouldThrowError) throw error
            emit(PdfDownloadDN())
        }

    override suspend fun sendToInstitution(selectedTypes: Set<HistoryCertificateType>) {
        if (shouldThrowError) throw error
        lastSentTypes = selectedTypes
    }

    override suspend fun getHistoryJobInfos(filters: List<ApiFilterDN>): Flow<HistoryJobInfoDN> =
        kotlinx.coroutines.flow.flow {
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
