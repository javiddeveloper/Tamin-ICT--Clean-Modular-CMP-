package com.tamin.taminhamrah.useCases.health

import app.cash.turbine.test
import com.tamin.taminhamrah.model.health.SmokingStatusDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetSmokingStatusUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: GetSmokingStatusUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = GetSmokingStatusUseCase(repository)
    }

    @Test
    fun `invoke should return smoking status list`() = runTest {
        val expected = listOf(SmokingStatusDN(key = 1, value = "Non-smoker"), SmokingStatusDN(key = 2, value = "Light"))
        repository.getSmokingStatusResult = expected

        useCase().test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return empty list when no smoking statuses`() = runTest {
        repository.getSmokingStatusResult = emptyList()

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
