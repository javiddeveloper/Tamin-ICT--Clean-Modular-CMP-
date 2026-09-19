package com.tamin.taminhamrah.useCases.contracts

import app.cash.turbine.test
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

class UpdateContractUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: UpdateContractUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = UpdateContractUseCase(repository)
    }

    @Test
    fun `invoke routes non-optional contracts to freelance update`() = runTest {
        val params = sampleParams()

        useCase(isOptionalInsurance = false, params = params).test {
            assertEquals(Unit, awaitItem())
            awaitComplete()
        }

        assertEquals(params, repository.lastUpdateFreelanceContractParams)
        assertTrue(repository.updateFreelanceContractCalled)
        assertFalse(repository.updateOptionalContractCalled)
        assertFalse(repository.makeFreelanceContractCalled)
        assertFalse(repository.makeContractCalled)
    }

    @Test
    fun `invoke routes optional contracts to optional update`() = runTest {
        val params = sampleParams()

        useCase(isOptionalInsurance = true, params = params).test {
            assertEquals(Unit, awaitItem())
            awaitComplete()
        }

        assertEquals(params.monthlyPremium, repository.lastUpdateOptionalPremium)
        assertTrue(repository.updateOptionalContractCalled)
        assertFalse(repository.updateFreelanceContractCalled)
        assertFalse(repository.makeContractCalled)
        assertFalse(repository.makeFreelanceContractCalled)
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
