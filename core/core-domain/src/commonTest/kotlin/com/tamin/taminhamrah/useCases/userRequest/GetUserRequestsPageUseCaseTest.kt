package com.tamin.taminhamrah.useCases.userRequest

import app.cash.turbine.test
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestSearchParams
import com.tamin.taminhamrah.repository.userRequest.FakeUserRequestRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserRequestsPageUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeUserRequestRepository
    private lateinit var useCase: GetUserRequestsPageUseCase

    @BeforeTest
    fun setup() {
        repository = FakeUserRequestRepository()
        useCase = GetUserRequestsPageUseCase(repository)
    }

    @Test
    fun `invoke passes search and page to the repository and returns its page`() = runTest {
        val search = UserRequestSearchParams(refCode = "3333", requestTypeId = "67")
        val page = ApiQueryParamDN(page = 2, start = 20, limit = 10)
        repository.userRequestsResult = listOf(
            UserRequestDN(
                id = 1L,
                refCode = "3333",
                title = null,
                comment = null,
                creationTime = null,
                createByName = null,
                status = null,
                requestType = null,
                referenceId = null,
            )
        )

        useCase(search, page).test {
            assertEquals(repository.userRequestsResult, awaitItem().items)
            awaitComplete()
        }

        assertEquals(search, repository.lastSearch)
        assertEquals(page, repository.lastPage)
    }

    @Test
    fun `invoke surfaces repository errors`() = runTest {
        repository.shouldThrowError = true
        repository.error = RuntimeException("Failed")

        useCase(UserRequestSearchParams(), ApiQueryParamDN()).test {
            assertEquals("Failed", awaitError().message)
        }
    }
}
