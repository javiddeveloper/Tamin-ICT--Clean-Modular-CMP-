package com.tamin.taminhamrah.useCases.contracts

import app.cash.turbine.test
import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import com.tamin.taminhamrah.repository.contracts.FakeContractsRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetSpcPremiumRatesUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: GetSpcPremiumRatesUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = GetSpcPremiumRatesUseCase(repository)
    }

    @Test
    fun `invoke should return premium rates from repository`() = runTest {
        val expectedRates = listOf(
            PremiumRateDN(
                govermentPercent = null,
                insurDpercent = "12",
                payrespitelOne = null,
                payrespitelTwo = null,
                selfIsuTypeCode = "01",
                spcLowDayWage = null,
                spcrateCode = "01",
                spcrateDescription = "صاحبان حرف  ومشاغل ازاد12درصد",
                status = null,
                statusStDate = null,
                treatmentPercap = null,
            ),
        )
        repository.spcPremiumRatesResult = expectedRates

        useCase().test {
            assertEquals(expectedRates, awaitItem())
            awaitComplete()
        }
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
}
