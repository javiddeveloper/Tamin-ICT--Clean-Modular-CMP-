package com.tamin.taminhamrah.useCases.health

import app.cash.turbine.test
import com.tamin.taminhamrah.model.health.ActFrequencyDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetActFrequenciesUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: GetActFrequenciesUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = GetActFrequenciesUseCase(repository)
    }

    @Test
    fun `invoke should return act frequencies list`() = runTest {
        val expected = listOf(
            ActFrequencyDN(key = 1, value = "Never"),
            ActFrequencyDN(key = 2, value = "Daily")
        )
        repository.getActFrequenciesResult = expected

        useCase().test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return empty list when no act frequencies`() = runTest {
        repository.getActFrequenciesResult = emptyList()

        useCase().test {
            assertEquals(emptyList(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Network error")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
