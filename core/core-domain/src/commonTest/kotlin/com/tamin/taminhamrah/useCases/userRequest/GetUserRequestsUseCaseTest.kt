package com.tamin.taminhamrah.useCases.userRequest

import app.cash.turbine.test
import com.tamin.taminhamrah.model.request.UserRequestDN
import com.tamin.taminhamrah.model.request.UserRequestStatusDN
import com.tamin.taminhamrah.model.request.UserRequestTypeDN
import com.tamin.taminhamrah.repository.userRequest.FakeUserRequestRepository
import com.tamin.taminhamrah.model.userRequest.UserRequestSearchParams
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserRequestsUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeUserRequestRepository
    private lateinit var useCase: GetUserRequestsUseCase

    @BeforeTest
    fun setup() {
        repository = FakeUserRequestRepository()
        useCase = GetUserRequestsUseCase(repository)
    }

    @Test
    fun `invoke should return user requests from repository`() = runTest {
        val expectedList = listOf(sampleUserRequest())
        repository.userRequestsResult = expectedList

        useCase().test {
            val result = awaitItem()
            assertEquals(expectedList, result)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should pass search params to repository`() = runTest {
        val search = UserRequestSearchParams(refCode = "3333", requestTypeId = "67")
        repository.userRequestsResult = emptyList()

        useCase(search).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(search, repository.lastSearch)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }

    private fun sampleUserRequest() = UserRequestDN(
        id = 478176975L,
        refCode = "1073555545",
        title = "انعقاد قرارداد بيمه اختياري",
        comment = "قرارداد ايجاد شد",
        creationTime = 1780398668987L,
        createByName = "حميد چيداز",
        status = UserRequestStatusDN(
            requestCode = "2903",
            requestDesc = "انعقاد قرارداد",
        ),
        requestType = UserRequestTypeDN(
            id = 35L,
            title = "قرارداد بيمه اختياري",
            description = "قرارداد بيمه اختياري",
        ),
        referenceId = "478176974",
    )
}
