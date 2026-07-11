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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MakeContractUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: MakeContractUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = MakeContractUseCase(repository)
    }

    @Test
    fun `invoke routes non-optional contracts to freelance endpoint`() = runTest {
        val params = sampleParams()
        val expectedResult = FreelanceContractResultDN(contractNumber = 123L, contractDate = 456L)
        repository.makeContractResult = expectedResult

        useCase(isOptionalInsurance = false, params = params).test {
            assertEquals(expectedResult, awaitItem())
            awaitComplete()
        }

        assertEquals(params, repository.lastMakeContractParams)
        assertFalse(repository.makeContractCalled)
    }

    @Test
    fun `invoke routes optional contracts to standard endpoint`() = runTest {
        val params = sampleParams()
        val expectedResult = FreelanceContractResultDN(contractNumber = 789L, contractDate = 101L)
        repository.makeContractResult = expectedResult

        useCase(isOptionalInsurance = true, params = params).test {
            assertEquals(expectedResult, awaitItem())
            awaitComplete()
        }

        assertEquals(params, repository.lastMakeContractParams)
        assertTrue(repository.makeContractCalled)
    }

    private fun sampleParams() = FreelanceMakeContractParams(
        monthlyPremium = 25_989_368L,
        request = FreelanceMakeContractRequestDN(
            brchCodeNew = "0360",
            cityCode = "2442",
            cntDrmn = "1",
            cntFreeJobCode = "099796",
            guid = "00",
            guidName = "00",
            premiumRateCode = "01",
            provinceCode = "33",
        ),
    )
}
