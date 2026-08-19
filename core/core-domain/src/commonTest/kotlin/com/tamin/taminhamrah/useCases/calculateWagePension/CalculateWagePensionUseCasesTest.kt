package com.tamin.taminhamrah.useCases.calculateWagePension

import app.cash.turbine.test
import com.tamin.taminhamrah.repository.calculateWagePension.FakeCalculateWagePensionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CalculateWagePensionUseCasesTest : BaseUseCaseTest() {

    private lateinit var repository: FakeCalculateWagePensionRepository

    @BeforeTest
    fun setup() {
        repository = FakeCalculateWagePensionRepository()
    }

    @Test
    fun `get personal info returns organization mapping from repository`() = runTest {
        val useCase = GetMultipleWorkshopPersonalInfoUseCase(repository)

        useCase().test {
            val result = awaitItem()
            assertEquals("12345", result.branchCode)
            assertEquals("9876543210", result.insuranceNumber)
            awaitComplete()
        }
    }

    @Test
    fun `check multiple workshops forwards params and result`() = runTest {
        val useCase = CheckMultipleWorkshopsUseCase(repository)

        useCase("b1", "ins1").test {
            val result = awaitItem()
            assertEquals(1, result.result)
            assertEquals(true, result.isMultiple)
            awaitComplete()
        }
        assertEquals("b1", repository.lastBranchCode)
        assertEquals("ins1", repository.lastInsuranceNumber)
    }

    @Test
    fun `calculate multiple workshops pension returns amount`() = runTest {
        val useCase = CalculateMultipleWorkshopsPensionUseCase(repository)

        useCase("b1", "ins1").test {
            val result = awaitItem()
            assertEquals(25_000_000, result.result)
            assertEquals(25_000_000L, result.pensionAmount)
            awaitComplete()
        }
    }

    @Test
    fun `get personal info returns error when repository fails`() = runTest {
        repository.shouldThrowError = true
        repository.error = RuntimeException("failed")
        val useCase = GetMultipleWorkshopPersonalInfoUseCase(repository)

        useCase().test {
            val actual = awaitError()
            assertEquals("failed", actual.message)
        }
    }
}
