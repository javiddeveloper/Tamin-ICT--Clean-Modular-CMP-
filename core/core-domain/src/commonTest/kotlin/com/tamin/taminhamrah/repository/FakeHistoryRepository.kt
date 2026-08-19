package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.tamin.taminhamrah.model.history.UserRoleDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN

class FakeHistoryRepository : HistoryRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Fake History Repository Error")

    var getTalfighInfosResult: TalfighInfoDN = TalfighInfoDN(list = emptyList(), total = 0)
    var getDastmozdInfosResult: DastmozdInfoDN = DastmozdInfoDN(list = emptyList(), total = 0)
    var getHistoryJobInfosResult: HistoryJobInfoDN = HistoryJobInfoDN(list = emptyList(), total = 0)

    var getUserInfosResult: UserInfoDN = UserInfoDN(
        serial1 = null, militaryServiceCode = null, fatherName = null, lastName = null,
        serial2 = null, creationTime = null, lastModificationTime = null, cityCode = null,
        socialSecurityNumber = null, lastModifiedBy = null, issueplaceName = null, birthDate = null,
        firstName = null, insuranceNumber = null, genderCode = null, nationalID = null,
        marriageCode = null, createdBy = null, identityNumber = null, countryCode = null,
        id = null, birthDateTimestamp = null, issueplace = null, nationCode = null
    )

    var lastSentTypes: Set<HistoryCertificateType>? = null

    override suspend fun getTalfighInfos(filters: List<ApiFilterDN>): TalfighInfoDN {
        if (shouldThrowError) throw error
        return getTalfighInfosResult
    }

    override suspend fun getDastmozdInfos(filters: List<ApiFilterDN>): DastmozdInfoDN {
        if (shouldThrowError) throw error
        return getDastmozdInfosResult
    }

    override suspend fun getUserInfos(): UserInfoDN {
        if (shouldThrowError) throw error
        return getUserInfosResult
    }

    var userRoleResult: UserRoleDN = UserRoleDN.INSURED

    override suspend fun getUserRole(): UserRoleDN {
        if (shouldThrowError) throw error
        return userRoleResult
    }

    override fun downloadHistoryReport(type: HistoryCertificateType): Flow<PdfDownloadDN> = flow {
        if (shouldThrowError) throw error
        emit(PdfDownloadDN())
    }

    override suspend fun sendHistoryNotice(): String? = null

    override suspend fun sendToInstitution(selectedTypes: Set<HistoryCertificateType>) {
        if (shouldThrowError) throw error
        lastSentTypes = selectedTypes
    }

    override suspend fun getHistoryJobInfos(filters: List<ApiFilterDN>): Flow<HistoryJobInfoDN> = flow {
        if (shouldThrowError) throw error
        emit(getHistoryJobInfosResult)
    }
}
