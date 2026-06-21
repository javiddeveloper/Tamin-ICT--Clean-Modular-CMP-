package com.tamin.taminhamrah.useCases.personal

import app.cash.turbine.test
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionDN
import com.tamin.taminhamrah.repository.FakePersonalRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SubmitFinalSurvivorPensionUseCaseTest : BaseUseCaseTest() {

    private lateinit var personalRepository: FakePersonalRepository
    private lateinit var useCase: SubmitFinalSurvivorPensionUseCase

    @BeforeTest
    fun setup() {
        personalRepository = FakePersonalRepository()
        useCase = SubmitFinalSurvivorPensionUseCase(personalRepository)
    }

    @Test
    fun `invoke should return result from repository`() = runTest {
        val requestId = 123
        val body = SubmitFinalSurvivorPensionDN(id = 1)
        val expectedResult = "Success"
        personalRepository.submitFinalSurvivorPensionResult = expectedResult

        useCase.invoke(requestId, body).test {
            assertEquals(expectedResult, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error`() = runTest {
        val requestId = 123
        val body = SubmitFinalSurvivorPensionDN(id = 1)
        val expectedException = RuntimeException("submission failed")
        personalRepository.shouldThrowError = true
        personalRepository.error = expectedException

        useCase.invoke(requestId, body).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
