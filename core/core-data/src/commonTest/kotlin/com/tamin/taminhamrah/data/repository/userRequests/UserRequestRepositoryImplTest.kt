package com.tamin.taminhamrah.data.repository.userRequests

import com.tamin.taminhamrah.data.local.dao.UserRequestDao
import com.tamin.taminhamrah.data.local.entity.UserRequestEntity
import com.tamin.taminhamrah.dataSource.request.UserRequestRemoteDataSource
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.userRequest.ArticleSixteenRequestInfoDTO
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
import com.tamin.taminhamrah.model.userRequest.UserRequestSearchParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
            isPublic = "1",
            title = "عنوان راهنما",
            description = "توضیحات تکمیلی"
        )
        remoteDataSource.getSmartGuideListResult = ListData(total = 1, list = listOf(expectedDto))

        val result = repository.getSmartGuideList(SmartGuideSearchParams(requestType = 3, requestStatus = "0018", isPublic = true))

        assertEquals(1, result.size)
        assertEquals(201L, result.first().id)
        assertEquals("سوال نمونه", result.first().question)
        assertEquals(true, result.first().isPublic)
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

    @Test
    fun `getUserRequestsPage emits the cached slice, then the network page with its total`() = runTest {
        dao.requestsFlow.value = listOf(entity(1, "100"), entity(2, "200"))
        remoteDataSource.userRequestsResult = ListData(total = 3, list = listOf(dto(3, "300"), dto(2, "200"), dto(1, "100")))

        val pages = repository.getUserRequestsPage(UserRequestSearchParams(), page(start = 0)).toList()

        assertEquals(2, pages.size)
        assertTrue(pages[0].isFromCache)
        assertEquals(listOf("200", "100"), pages[0].items.map { it.refCode }) // refCode DESC, like the server
        assertFalse(pages[1].isFromCache)
        assertEquals(listOf(3L, 2L, 1L), pages[1].items.map { it.id })
        assertEquals(3, pages[1].total)
        val sent = remoteDataSource.lastUserRequestsQuery!!
        assertEquals(0, sent.start)
        assertEquals(10, sent.limit)
        assertTrue(sent.sorts.isNotEmpty(), "server sort must survive the page params")
    }

    @Test
    fun `getUserRequestsPage slices the cache by start and limit`() = runTest {
        dao.requestsFlow.value = (1L..15L).map { entity(it, "${100 + it}") }
        remoteDataSource.userRequestsError = RuntimeException("offline")

        val pages = repository.getUserRequestsPage(UserRequestSearchParams(), page(start = 10)).toList()

        assertEquals(listOf("105", "104", "103", "102", "101"), pages.single().items.map { it.refCode })
        assertEquals(10, remoteDataSource.lastUserRequestsQuery?.start)
    }

    @Test
    fun `getUserRequestsPage offline with a cached slice completes quietly`() = runTest {
        dao.requestsFlow.value = listOf(entity(1, "100"))
        remoteDataSource.userRequestsError = RuntimeException("offline")

        val pages = repository.getUserRequestsPage(UserRequestSearchParams(), page(start = 0)).toList()

        assertTrue(pages.single().isFromCache)
    }

    @Test
    fun `getUserRequestsPage offline with nothing cached throws`() = runTest {
        remoteDataSource.userRequestsError = RuntimeException("offline")

        assertFailsWith<RuntimeException> {
            repository.getUserRequestsPage(UserRequestSearchParams(), page(start = 0)).toList()
        }
    }

    @Test
    fun `getUserRequestsPage unfiltered first page replaces the cache`() = runTest {
        dao.requestsFlow.value = listOf(entity(99, "999")) // deleted on the server
        remoteDataSource.userRequestsResult = ListData(total = 1, list = listOf(dto(1, "100")))

        repository.getUserRequestsPage(UserRequestSearchParams(), page(start = 0)).toList()

        assertEquals(listOf(1L), dao.requestsFlow.value.map { it.id })
    }

    @Test
    fun `getUserRequestsPage later page appends to the cache`() = runTest {
        dao.requestsFlow.value = listOf(entity(1, "100"))
        remoteDataSource.userRequestsResult = ListData(total = 11, list = listOf(dto(2, "050")))

        repository.getUserRequestsPage(UserRequestSearchParams(), page(start = 10)).toList()

        assertEquals(setOf(1L, 2L), dao.requestsFlow.value.map { it.id }.toSet())
    }

    @Test
    fun `getUserRequestsPage filtered first page does not wipe the shared cache`() = runTest {
        // cartable and Home read the same table; a ref-code search must not delete their rows.
        dao.requestsFlow.value = listOf(entity(1, "100"), entity(2, "200"))
        remoteDataSource.userRequestsResult = ListData(total = 1, list = listOf(dto(2, "200")))

        val pages = repository.getUserRequestsPage(UserRequestSearchParams(refCode = "200"), page(start = 0)).toList()

        assertEquals(listOf("200"), pages.first().items.map { it.refCode }) // cached slice is filtered too
        assertEquals(setOf(1L, 2L), dao.requestsFlow.value.map { it.id }.toSet())
    }

    @Test
    fun `getUserRequestsPage treats a missing total as unknown`() = runTest {
        // ListData.total defaults to 0; passing 0 on would make the Paginator stop after page one.
        remoteDataSource.userRequestsResult = ListData(list = listOf(dto(1, "100")))

        val page = repository.getUserRequestsPage(UserRequestSearchParams(), page(start = 0)).toList().last()

        assertNull(page.total)
    }

    private fun page(start: Int) = ApiQueryParamDN(page = start / 10, start = start, limit = 10)

    private fun entity(id: Long, refCode: String) = UserRequestEntity(
        id = id,
        refCode = refCode,
        title = null,
        comment = null,
        creationTime = null,
        createByName = null,
        statusCode = null,
        statusDesc = null,
        requestTypeId = null,
        requestTypeTitle = null,
        requestTypeDescription = null,
        referenceId = null,
    )

    private fun dto(id: Long, refCode: String) = UserRequestDTO(
        id = id,
        operation = null,
        createdBy = null,
        creationTime = null,
        lastModifiedBy = null,
        lastModificationTime = null,
        refCode = refCode,
        userName = null,
        status = null,
        title = null,
        comment = null,
        template = null,
        requestType = null,
        deliverCode = null,
        referenceId = null,
        requestDetails = null,
        requestChid = null,
        fullName = null,
        createByName = null,
    )

    private class FakeRemoteDataSource : UserRequestRemoteDataSource {
        var userRequestsResult = ListData<UserRequestDTO>(total = 0, list = emptyList())
        var userRequestsError: Exception? = null
        var lastUserRequestsQuery: ApiQueryParamDN? = null
        var getRequestErrorsResult = ListData<RequestErrorDTO>(total = 0, list = emptyList())
        var getSmartGuideListResult = ListData<SmartGuideDTO>(total = 0, list = emptyList())
        var getUserRequestDetailResult: UserRequestDTO? = null
        var getShortTermStatusResult = ListData<ShortTermRequestStatusDTO>(total = 0, list = emptyList())
        var getShortTermInfoResult = ListData<ShortTermRequestInfoDTO>(total = 0, list = emptyList())
        var getPregnancyStatusResult = ListData<PregnancyLookupDTO>(total = 0, list = emptyList())
        var getPregnancyTypesResult = ListData<PregnancyLookupDTO>(total = 0, list = emptyList())
        var getArticleSixteenResult = ArticleSixteenRequestInfoDTO()
        var getDeferredInstallmentResult = DeferredInstallmentInfoDTO()
        var getFollowUpResult = ListData<FollowUpObjectionHistoryDTO>(total = 0, list = emptyList())
        var downloadDocumentResult = ""

        override suspend fun getUserRequests(query: ApiQueryParamDN): ListData<UserRequestDTO> {
            lastUserRequestsQuery = query
            userRequestsError?.let { throw it }
            return userRequestsResult
        }

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

        override suspend fun getArticleSixteenRequestInfo(objectionNumber: Long) = getArticleSixteenResult

        override suspend fun getDeferredInstallmentInfo(requestId: String) = getDeferredInstallmentResult

        override suspend fun getFollowUpObjectionHistory(referenceId: String) = getFollowUpResult

        override suspend fun downloadDocument(guid: String) = downloadDocumentResult
    }

    private class FakeDao : UserRequestDao {
        val requestsFlow = MutableStateFlow<List<UserRequestEntity>>(emptyList())

        // Same order as the real query: ORDER BY refCode DESC.
        override fun getUserRequests(): Flow<List<UserRequestEntity>> =
            requestsFlow.map { rows -> rows.sortedByDescending { it.refCode } }

        override suspend fun upsertUserRequests(requests: List<UserRequestEntity>) {
            val updated = requestsFlow.value.toMutableList()
            requests.forEach { req ->
                updated.removeAll { it.id == req.id }
                updated.add(req)
            }
            requestsFlow.value = updated
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
