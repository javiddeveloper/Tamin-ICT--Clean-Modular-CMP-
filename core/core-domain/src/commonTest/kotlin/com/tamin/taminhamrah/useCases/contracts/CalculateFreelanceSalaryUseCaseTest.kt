package com.tamin.taminhamrah.useCases.contracts

import app.cash.turbine.test
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.repository.contracts.FakeContractsRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CalculateFreelanceSalaryUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: CalculateFreelanceSalaryUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = CalculateFreelanceSalaryUseCase(repository)
    }

    @Test
    fun `invoke should return calculated salary from repository`() = runTest {
        val params = FreelanceCalculateSalaryParams(
            monthlyPremium = 60_300_000L,
            treatmentSupportCode = "1",
            spcRateCode = "01",
        )
        repository.calculatedSalaryResult = 502_500_000L

        useCase(params).test {
            assertEquals(502_500_000L, awaitItem())
            awaitComplete()
        }

        assertEquals(params, repository.lastFreelanceCalculateParams)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase(
            FreelanceCalculateSalaryParams(
                monthlyPremium = 60_300_000L,
                treatmentSupportCode = "1",
                spcRateCode = "01",
            ),
        ).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
