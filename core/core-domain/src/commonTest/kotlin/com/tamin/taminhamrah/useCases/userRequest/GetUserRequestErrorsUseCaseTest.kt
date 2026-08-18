package com.tamin.taminhamrah.useCases.userRequest

import com.tamin.taminhamrah.model.userRequest.RequestErrorDN
import com.tamin.taminhamrah.repository.userRequest.FakeUserRequestRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetUserRequestErrorsUseCaseTest {

    private lateinit var repository: FakeUserRequestRepository
    private lateinit var useCase: GetUserRequestErrorsUseCase

    @BeforeTest
    fun setUp() {
        repository = FakeUserRequestRepository()
        useCase = GetUserRequestErrorsUseCase(repository)
    }

    @Test
    fun `invoke should return request error list from repository`() = runTest {
        val expected = listOf(
            RequestErrorDN(
                id = 101L,
                errorMessage = "نقص مدارک شناسایی",
                errorType = "VALIDATION",
                errorStatus = "FAILED",
                creationTime = 1700000000000L
            )
        )
        repository.requestErrorsResult = expected

        val result = useCase(12345L)

        assertEquals(expected, result)
        assertEquals(12345L, repository.lastRequestId)
    }

    @Test
    fun `invoke should propagate repository error`() = runTest {
        repository.shouldThrowError = true
        repository.error = RuntimeException("Network Error")

        assertFailsWith<RuntimeException> {
            useCase(12345L)
        }
    }
}
