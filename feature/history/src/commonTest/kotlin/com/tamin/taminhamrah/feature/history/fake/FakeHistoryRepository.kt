package com.tamin.taminhamrah.feature.history.fake

import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.HistoryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.time.Duration.Companion.milliseconds

/**
 * A [HistoryRepository] whose two calls can succeed, fail or be slow independently — which is the
 * whole point, since this screen survives one of them failing.
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
     * مستمری‌بگیر or a کارفرما sets [notInsuredUser] — the person the service has no years for.
     */
    var userInfoResult: UserInfoDN = insuredUser()
    var userInfoError: Throwable? = null

    override suspend fun getUserInfos(): UserInfoDN {
        userInfoError?.let { throw it }
        return userInfoResult
    }

    override suspend fun sendToInstitution(selectedTypes: Set<HistoryCertificateType>) = Unit

    override suspend fun getHistoryJobInfos(filters: List<ApiFilterDN>): Flow<HistoryJobInfoDN> =
        flowOf(HistoryJobInfoDN(list = emptyList(), total = 0))
}

private fun blankUser() = UserInfoDN(
    serial1 = null, militaryServiceCode = null, fatherName = null, lastName = null,
    serial2 = null, creationTime = null, lastModificationTime = null, cityCode = null,
    socialSecurityNumber = null, lastModifiedBy = null, issueplaceName = null, birthDate = null,
    firstName = null, insuranceNumber = null, genderCode = null, nationalID = null,
    marriageCode = null, createdBy = null, identityNumber = null, countryCode = null,
    id = null, birthDateTimestamp = null, issueplace = null, nationCode = null,
)

/** Someone the history endpoints will answer for: they have an insurance number. */
fun insuredUser(): UserInfoDN = blankUser().copy(insuranceNumber = "0081631829")

/** A مستمری‌بگیر or کارفرما: no insurance number, and so no insured years to ask for. */
fun notInsuredUser(): UserInfoDN = blankUser().copy(insuranceNumber = null)
