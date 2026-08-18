package com.tamin.taminhamrah.data.repository.userRequests

import com.tamin.taminhamrah.data.local.dao.UserRequestDao
import com.tamin.taminhamrah.data.local.entity.UserRequestEntity
import com.tamin.taminhamrah.dataSource.request.UserRequestRemoteDataSource
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.userRequest.Article16RequestInfoDTO
import com.tamin.taminhamrah.model.userRequest.DeferredInstallmentInfoDTO
import com.tamin.taminhamrah.model.userRequest.FollowUpObjectionHistoryDTO
import com.tamin.taminhamrah.model.userRequest.PregnancyLookupDTO
import com.tamin.taminhamrah.model.userRequest.RequestErrorDTO
import com.tamin.taminhamrah.model.userRequest.ShortTermRequestInfoDTO
import com.tamin.taminhamrah.model.userRequest.ShortTermRequestStatusDTO
import com.tamin.taminhamrah.model.userRequest.SmartGuideDTO
import com.tamin.taminhamrah.model.userRequest.SmartGuideSearchParams
import com.tamin.taminhamrah.model.userRequest.UserRequestDTO
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDTO
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeIds
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UserRequestRepositoryImplTest {

    private lateinit var remoteDataSource: FakeRemoteDataSource
    private lateinit var dao: FakeDao
    private lateinit var apiQueryBuilder: FakeApiQueryBuilder
    private lateinit var repository: UserRequestRepository

    @BeforeTest
    fun setup() {
        remoteDataSource = FakeRemoteDataSource()
        dao = FakeDao()
        apiQueryBuilder = FakeApiQueryBuilder()
        repository = UserRequestRepositoryImpl(remoteDataSource, dao, apiQueryBuilder)
    }

    @Test
    fun `getRequestErrors should call remote data source and map to domain`() = runTest {
        val expectedDto = RequestErrorDTO(
            id = 101L,
            errorMessage = "خطای نمونه",
            errorType = "VALIDATION",
            errorStatus = "FAILED",
            creationTime = 1700000000000L
        )
        remoteDataSource.getRequestErrorsResult = ListData(total = 1, list = listOf(expectedDto))

        val result = repository.getRequestErrors(123L)

        assertEquals(1, result.size)
        assertEquals(101L, result.first().id)
        assertEquals("خطای نمونه", result.first().errorMessage)
    }

    @Test
    fun `getSmartGuideList should call remote data source and map to domain`() = runTest {
        val expectedDto = SmartGuideDTO(
            id = 201L,
            question = "سوال نمونه",
            reply = "پاسخ نمونه",
            requestCode = "0018",
            requestDesc = "توضیحات درخواست",
            isPublic = true,
            title = "عنوان راهنما",
            description = "توضیحات تکمیلی"
        )
        remoteDataSource.getSmartGuideListResult = ListData(total = 1, list = listOf(expectedDto))

        val result = repository.getSmartGuideList(SmartGuideSearchParams(requestType = 3, requestStatus = "0018", isPublic = true))

        assertEquals(1, result.size)
        assertEquals(201L, result.first().id)
        assertEquals("سوال نمونه", result.first().question)
    }

    @Test
    fun `getShowRequestInfo for deferred installment maps pensioner and borrower`() = runTest {
        remoteDataSource.getDeferredInstallmentResult = DeferredInstallmentInfoDTO(
            firstName = "علی",
            lastName = "محمدی",
            nationalId = "0010000000",
            birthDate = 1716000000000L,
            pensionerNationalId = "0020000000",
            userFirstName = "کاربر",
            userLastName = "کاربری",
            pensionerId = "123456",
            bankBranch = "شعبه مرکزی",
            installmentAmount = 15000000,
            installmentCount = 12,
            loanAmount = 180000000,
            guaranteeAmount = 20000000,
        )

        val result = repository.getShowRequestInfo("req-1", UserRequestTypeIds.DEFERRED_INSTALLMENT)

        assertEquals("علی محمدی", result?.deferredInstallment?.borrowerName)
        assertEquals("کاربر", result?.deferredInstallment?.pensionerFirstName)
        assertEquals(180000000L, result?.deferredInstallment?.repaymentAmount)
    }

    @Test
    fun `getUserRequestDetail maps remote dto to domain`() = runTest {
        remoteDataSource.getUserRequestDetailResult = UserRequestDTO(
            id = 12L,
            operation = null,
            createdBy = null,
            creationTime = 1L,
            lastModifiedBy = null,
            lastModificationTime = null,
            refCode = "1075558440",
            userName = null,
            status = null,
            title = "درخواست",
            comment = null,
            template = null,
            requestType = null,
            deliverCode = null,
            referenceId = "99",
            requestDetails = null,
            requestChid = null,
            fullName = null,
            createByName = "علی",
        )

        val result = repository.getUserRequestDetail(12L)
        assertEquals(12L, result.id)
        assertEquals("1075558440", result.refCode)
        assertEquals("99", result.referenceId)
    }

    private class FakeRemoteDataSource : UserRequestRemoteDataSource {
        var getRequestErrorsResult = ListData<RequestErrorDTO>(total = 0, list = emptyList())
        var getSmartGuideListResult = ListData<SmartGuideDTO>(total = 0, list = emptyList())
        var getUserRequestDetailResult: UserRequestDTO? = null
        var getShortTermStatusResult = ListData<ShortTermRequestStatusDTO>(total = 0, list = emptyList())
        var getShortTermInfoResult = ListData<ShortTermRequestInfoDTO>(total = 0, list = emptyList())
        var getPregnancyStatusResult = ListData<PregnancyLookupDTO>(total = 0, list = emptyList())
        var getPregnancyTypesResult = ListData<PregnancyLookupDTO>(total = 0, list = emptyList())
        var getArticle16Result = Article16RequestInfoDTO()
        var getDeferredInstallmentResult = DeferredInstallmentInfoDTO()
        var getFollowUpResult = ListData<FollowUpObjectionHistoryDTO>(total = 0, list = emptyList())
        var downloadDocumentResult = ""

        override suspend fun getUserRequests(query: ApiQueryParamDN): ListData<UserRequestDTO> =
            ListData(total = 0, list = emptyList())

        override suspend fun getRequestTypes(query: ApiQueryParamDN): ListData<UserRequestTypeDTO> =
            ListData(total = 0, list = emptyList())

        override suspend fun getRequestErrors(query: ApiQueryParamDN): ListData<RequestErrorDTO> =
            getRequestErrorsResult

        override suspend fun getSmartGuideList(params: Map<String, String>): ListData<SmartGuideDTO> =
            getSmartGuideListResult

        override suspend fun getUserRequestDetail(id: Long): UserRequestDTO =
            getUserRequestDetailResult ?: error("detail not set")

        override suspend fun getShortTermRequestStatus(referenceId: String) = getShortTermStatusResult

        override suspend fun getShortTermRequestLoadData(referenceId: String) = getShortTermInfoResult

        override suspend fun getPregnancyStatus() = getPregnancyStatusResult

        override suspend fun getPregnancyTypes() = getPregnancyTypesResult

        override suspend fun getArticle16RequestInfo(objectionNumber: Long) = getArticle16Result

        override suspend fun getDeferredInstallmentInfo(requestId: String) = getDeferredInstallmentResult

        override suspend fun getFollowUpObjectionHistory(referenceId: String) = getFollowUpResult

        override suspend fun downloadDocument(guid: String) = downloadDocumentResult
    }

    private class FakeDao : UserRequestDao {
        val requestsFlow = MutableStateFlow<List<UserRequestEntity>>(emptyList())

        override fun getUserRequests(): Flow<List<UserRequestEntity>> = requestsFlow

        override suspend fun upsertUserRequests(requests: List<UserRequestEntity>) {
            requestsFlow.value = requests
        }

        override suspend fun clearUserRequests() {
            requestsFlow.value = emptyList()
        }

        override suspend fun replaceAll(requests: List<UserRequestEntity>) {
            clearUserRequests()
            upsertUserRequests(requests)
        }
    }


    private class FakeApiQueryBuilder : ApiQueryBuilder {
        override fun defaultQuery(): ApiQueryParamDN = ApiQueryParamDN()
        override fun buildQuery(query: ApiQueryParamDN): Map<String, String> = emptyMap()
        override fun buildFilterJson(filters: List<ApiFilterDN>): String = ""
    }
}
