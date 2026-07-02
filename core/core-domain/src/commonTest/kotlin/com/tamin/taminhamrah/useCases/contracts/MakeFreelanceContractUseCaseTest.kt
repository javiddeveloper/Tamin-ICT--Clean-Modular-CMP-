package com.tamin.taminhamrah.useCases.contracts

import app.cash.turbine.test
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractParams
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractRequestDN
import com.tamin.taminhamrah.repository.contracts.FakeContractsRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MakeFreelanceContractUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: MakeFreelanceContractUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = MakeFreelanceContractUseCase(repository)
    }

    @Test
    fun `invoke should return contract result from repository`() = runTest {
        val params = sampleParams()
        val expectedResult = FreelanceContractResultDN(
            contractNumber = 478_176_975L,
            contractDate = 1_782_132_474_000L,
        )
        repository.makeContractResult = expectedResult

        useCase(params).test {
            assertEquals(expectedResult, awaitItem())
            awaitComplete()
        }

        assertEquals(params, repository.lastMakeContractParams)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase(sampleParams()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }

    private fun sampleParams() = FreelanceMakeContractParams(
        monthlyPremium = 60_300_000L,
        request = FreelanceMakeContractRequestDN(
            brchCodeNew = "001",
            cityCode = "0101",
            cntDrmn = "1",
            cntFreeJobCode = "099796",
            guid = "a4769aa8-b9af-4183-83b9-367dc9f52511",
            guidName = "مدرک",
            premiumRateCode = "01",
            provinceCode = "01",
        ),
    )
}
