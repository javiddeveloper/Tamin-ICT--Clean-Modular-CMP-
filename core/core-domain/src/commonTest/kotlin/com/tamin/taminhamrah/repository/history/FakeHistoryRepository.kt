package com.tamin.taminhamrah.repository.history

import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.HistoryRepository

class FakeHistoryRepository : HistoryRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")

    var userInfoResult: UserInfoDN = emptyUserInfoDN()
    var lastSentType1: Boolean? = null
    var lastSentType2: Boolean? = null
    var lastSentType3: Boolean? = null

    override suspend fun getTalfighInfos(filters: List<ApiFilterDN>): TalfighInfoDN =
        TalfighInfoDN(list = emptyList(), total = 0)

    override suspend fun getDastmozdInfos(filters: List<ApiFilterDN>): DastmozdInfoDN =
        DastmozdInfoDN(list = emptyList(), total = 0)

    override suspend fun getUserInfos(): UserInfoDN {
        if (shouldThrowError) throw error
        return userInfoResult
    }

    override suspend fun sendToInstitution(type1: Boolean, type2: Boolean, type3: Boolean) {
        if (shouldThrowError) throw error
        lastSentType1 = type1
        lastSentType2 = type2
        lastSentType3 = type3
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
