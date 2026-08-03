package com.tamin.taminhamrah.useCases.health

import app.cash.turbine.test
import com.tamin.taminhamrah.model.health.MaritalStatusDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetMaritalStatusUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: GetMaritalStatusUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = GetMaritalStatusUseCase(repository)
    }

    @Test
    fun `invoke should return marital status list`() = runTest {
        val expected = listOf(MaritalStatusDN(key = 1, value = "Single"), MaritalStatusDN(key = 2, value = "Married"))
        repository.getMaritalStatusResult = expected

        useCase().test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return empty list when no marital statuses`() = runTest {
        repository.getMaritalStatusResult = emptyList()

        useCase().test {
            assertEquals(emptyList(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Server error")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
