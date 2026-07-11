package com.tamin.taminhamrah.useCases.contracts

import app.cash.turbine.test
import com.tamin.taminhamrah.repository.contracts.FakeContractsRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CalculateOptionalSalaryUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: CalculateOptionalSalaryUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = CalculateOptionalSalaryUseCase(repository)
    }

    @Test
    fun `invoke should return calculated salary from repository`() = runTest {
        repository.calculatedOptionalSalaryResult = 362_592_593L

        useCase("25989368").test {
            assertEquals(362_592_593L, awaitItem())
            awaitComplete()
        }

        assertEquals("25989368", repository.lastPremiumRateCode)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase("25989368").test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
