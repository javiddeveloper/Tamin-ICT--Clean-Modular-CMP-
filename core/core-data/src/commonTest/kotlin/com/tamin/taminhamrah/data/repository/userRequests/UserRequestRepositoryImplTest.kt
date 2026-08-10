package com.tamin.taminhamrah.data.repository.userRequests

import com.tamin.taminhamrah.data.local.dao.UserRequestDao
import com.tamin.taminhamrah.data.local.entity.UserRequestEntity
import com.tamin.taminhamrah.dataSource.request.UserRequestRemoteDataSource
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.userRequest.RequestErrorDTO
import com.tamin.taminhamrah.model.userRequest.SmartGuideDTO
import com.tamin.taminhamrah.model.userRequest.SmartGuideSearchParams
import com.tamin.taminhamrah.model.userRequest.UserRequestDTO
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDTO
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

    private class FakeRemoteDataSource : UserRequestRemoteDataSource {
        var getRequestErrorsResult = ListData<RequestErrorDTO>(total = 0, list = emptyList())
        var getSmartGuideListResult = ListData<SmartGuideDTO>(total = 0, list = emptyList())

        override suspend fun getUserRequests(query: ApiQueryParamDN): ListData<UserRequestDTO> =
            ListData(total = 0, list = emptyList())

        override suspend fun getRequestTypes(query: ApiQueryParamDN): ListData<UserRequestTypeDTO> =
            ListData(total = 0, list = emptyList())

        override suspend fun getRequestErrors(query: ApiQueryParamDN): ListData<RequestErrorDTO> =
            getRequestErrorsResult

        override suspend fun getSmartGuideList(query: ApiQueryParamDN): ListData<SmartGuideDTO> =
            getSmartGuideListResult
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
