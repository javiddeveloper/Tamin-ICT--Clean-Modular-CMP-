package com.tamin.taminhamrah.useCases.pension

import app.cash.turbine.test
import com.tamin.taminhamrah.model.pension.PayRollInboxDN
import com.tamin.taminhamrah.repository.pension.FakePensionRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SendPayRollToInboxUseCaseTest {

    private lateinit var fakePensionRepository: FakePensionRepository
    private lateinit var useCase: SendPayRollToInboxUseCase

    @BeforeTest
    fun setUp() {
        fakePensionRepository = FakePensionRepository()
        useCase = SendPayRollToInboxUseCase(fakePensionRepository)
    }

    @Test
    fun `invoke should return success message from repository`() = runTest {
        val expectedResult = PayRollInboxDN(message = "Success")
        fakePensionRepository.sendPayRollToInboxResult = expectedResult

        useCase(emptyList()).test {
            assertEquals(expectedResult, awaitItem())
            awaitComplete()
        }
    }
}
