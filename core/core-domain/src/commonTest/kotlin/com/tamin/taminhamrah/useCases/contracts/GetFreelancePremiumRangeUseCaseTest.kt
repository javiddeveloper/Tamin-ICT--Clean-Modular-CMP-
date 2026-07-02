package com.tamin.taminhamrah.useCases.contracts

import app.cash.turbine.test
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.repository.contracts.FakeContractsRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetFreelancePremiumRangeUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: GetFreelancePremiumRangeUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = GetFreelancePremiumRangeUseCase(repository)
    }

    @Test
    fun `invoke should return premium range from repository`() = runTest {
        val params = FreelancePremiumRangeParams(
            treatmentSupportCode = "1",
            spcRateCode = "01",
            freeJobCode = "099796",
        )
        val expectedRange = FreelancePremiumRangeDN(
            paymentTabayi = 0L,
            lowPremium = 25_989_368L,
            history = 538,
            highPremium = 139_654_620L,
        )
        repository.freelancePremiumRangeResult = expectedRange

        useCase(params).test {
            assertEquals(expectedRange, awaitItem())
            awaitComplete()
        }

        assertEquals(params, repository.lastFreelancePremiumRangeParams)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase(
            FreelancePremiumRangeParams(
                treatmentSupportCode = "1",
                spcRateCode = "01",
                freeJobCode = "099796",
            ),
        ).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
