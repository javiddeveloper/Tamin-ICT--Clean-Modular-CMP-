package com.tamin.taminhamrah.data.repository.history

import com.tamin.taminhamrah.data.local.dao.HistoryJobInfoDao
import com.tamin.taminhamrah.data.local.entity.HistoryJobInfoEntity
import com.tamin.taminhamrah.data.repository.HistoryRepositoryImpl
import com.tamin.taminhamrah.dataSource.historySource.HistoryRemoteDataSource
import com.tamin.taminhamrah.model.history.DastmozdInfoDTO
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.HistoryJobInfoDTO
import com.tamin.taminhamrah.model.history.TalfighInfoDTO
import com.tamin.taminhamrah.model.history.UserInfoDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertFailsWith
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.utils.ListData

class HistoryRepositoryImplReportTest {

    private lateinit var remoteDataSource: FakeHistoryRemoteDataSource
    private lateinit var repository: HistoryRepository

    @BeforeTest
    fun setup() {
        remoteDataSource = FakeHistoryRemoteDataSource()
        repository = HistoryRepositoryImpl(remoteDataSource, FakeHistoryJobInfoDao())
    }

    // ── getUserInfos ──────────────────────────────────────────────────────────

    @Test
    fun `getUserInfos should map all fields correctly from remote data source`() = runTest {
        remoteDataSource.userInfoResult = createUserInfoDTO(
            firstName = "عادل",
            lastName = "حسين پناهي",
            socialSecurityNumber = "2062144681",
            insuranceNumber = "0082984639",
            nationalID = "5589743451",
            birthDate = "1361/10/01",
            id = "2782294052"
        )

        val result = repository.getUserInfos()

        assertEquals("عادل", result.firstName)
        assertEquals("حسين پناهي", result.lastName)
        assertEquals("2062144681", result.socialSecurityNumber)
        assertEquals("0082984639", result.insuranceNumber)
        assertEquals("5589743451", result.nationalID)
        assertEquals("1361/10/01", result.birthDate)
        assertEquals("2782294052", result.id)
    }

    @Test
    fun `getUserInfos should preserve nullable fields as null`() = runTest {
        remoteDataSource.userInfoResult = createUserInfoDTO(
            militaryServiceCode = null,
            marriageCode = null
        )

        val result = repository.getUserInfos()

        assertNull(result.militaryServiceCode)
        assertNull(result.marriageCode)
    }

    @Test
    fun `getUserInfos should throw when remote data source throws`() = runTest {
        remoteDataSource.shouldThrowOnGetUserInfos = true

        assertFailsWith<RuntimeException> {
            repository.getUserInfos()
        }
    }

    // ── sendToInstitution ─────────────────────────────────────────────────────

    @Test
    fun `sendToInstitution should delegate to remote data source with all types selected`() = runTest {
        repository.sendToInstitution(setOf(HistoryCertificateType.ALL, HistoryCertificateType.WAGES, HistoryCertificateType.COMBINED))

        assertEquals(true, remoteDataSource.lastSentAllHistory)
        assertEquals(true, remoteDataSource.lastSentHistoryAndWage)
        assertEquals(true, remoteDataSource.lastSentCombineHistory)
    }

    @Test
    fun `sendToInstitution should map only selected types to true`() = runTest {
        repository.sendToInstitution(setOf(HistoryCertificateType.WAGES))

        assertEquals(false, remoteDataSource.lastSentAllHistory)
        assertEquals(true, remoteDataSource.lastSentHistoryAndWage)
        assertEquals(false, remoteDataSource.lastSentCombineHistory)
    }

    @Test
    fun `sendToInstitution should throw when remote data source throws`() = runTest {
        remoteDataSource.shouldThrowOnSendToInstitution = true

        assertFailsWith<RuntimeException> {
            repository.sendToInstitution(setOf(HistoryCertificateType.ALL, HistoryCertificateType.WAGES, HistoryCertificateType.COMBINED))
        }
    }

    // ── Fake ──────────────────────────────────────────────────────────────────

    private class FakeHistoryRemoteDataSource : HistoryRemoteDataSource {
        var userInfoResult: UserInfoDTO = createUserInfoDTO()
        var shouldThrowOnGetUserInfos = false
        var shouldThrowOnSendToInstitution = false

        var lastSentAllHistory: Boolean? = null
        var lastSentHistoryAndWage: Boolean? = null
        var lastSentCombineHistory: Boolean? = null

        override suspend fun getTalfighInfos(query: ApiQueryParamDN): TalfighInfoDTO =
            TalfighInfoDTO(list = emptyList(), total = 0)

        override suspend fun getDastmozdInfos(query: ApiQueryParamDN): DastmozdInfoDTO =
            DastmozdInfoDTO(list = emptyList(), total = 0)

        override suspend fun getUserInfos(): UserInfoDTO {
            if (shouldThrowOnGetUserInfos) throw RuntimeException("Remote failure")
            return userInfoResult
        }

        override suspend fun getLoginInfo(): ListData<String> =
            ListData(total = 0, list = emptyList())

        override suspend fun downloadHistoryReport(
            type: HistoryCertificateType
        ): PdfDownloadDTO = PdfDownloadDTO()

        override suspend fun sendToInstitution(allHistorySelected: Boolean, historyAndWageSelected: Boolean, combineHistorySelected: Boolean) {
            if (shouldThrowOnSendToInstitution) throw RuntimeException("Remote failure")
            lastSentAllHistory = allHistorySelected
            lastSentHistoryAndWage = historyAndWageSelected
            lastSentCombineHistory = combineHistorySelected
        }

        override suspend fun getHistoryJobInfos(query: ApiQueryParamDN): HistoryJobInfoDTO =
            HistoryJobInfoDTO(list = emptyList(), total = 0)
    }

    private class FakeHistoryJobInfoDao : HistoryJobInfoDao {
        override fun getAllJobInfos(): Flow<List<HistoryJobInfoEntity>> = flowOf(emptyList())
        override suspend fun insertJobInfos(jobInfos: List<HistoryJobInfoEntity>) = Unit
        override suspend fun clearAll() = Unit
    }
}

private fun createUserInfoDTO(
    serial1: String? = "ا19",
    militaryServiceCode: String? = null,
    fatherName: String? = "فرج اله",
    lastName: String? = "حسين پناهي",
    serial2: String? = "632661",
    creationTime: Long? = 1470332948237L,
    lastModificationTime: Long? = 1771136020155L,
    cityCode: String? = "1514",
    socialSecurityNumber: String? = "2062144681",
    lastModifiedBy: String? = "3790166227",
    issueplaceName: String? = "دهگلان",
    birthDate: String? = "1361/10/01",
    firstName: String? = "عادل",
    insuranceNumber: String? = "0082984639",
    genderCode: String? = "01",
    nationalID: String? = "5589743451",
    marriageCode: String? = null,
    createdBy: String? = "5589127671",
    identityNumber: String? = "5",
    countryCode: String? = "0001",
    id: String? = "2782294052",
    birthDateTimestamp: Long? = 409350600000L,
    issueplace: String? = "1514",
    nationCode: String? = "01"
) = UserInfoDTO(
    serial1 = serial1,
    militaryServiceCode = militaryServiceCode,
    fatherName = fatherName,
    lastName = lastName,
    serial2 = serial2,
    creationTime = creationTime,
    lastModificationTime = lastModificationTime,
    cityCode = cityCode,
    socialSecurityNumber = socialSecurityNumber,
    lastModifiedBy = lastModifiedBy,
    issueplaceName = issueplaceName,
    birthDate = birthDate,
    firstName = firstName,
    insuranceNumber = insuranceNumber,
    genderCode = genderCode,
    nationalID = nationalID,
    marriageCode = marriageCode,
    createdBy = createdBy,
    identityNumber = identityNumber,
    countryCode = countryCode,
    id = id,
    birthDateTimestamp = birthDateTimestamp,
    issueplace = issueplace,
    nationCode = nationCode
)
