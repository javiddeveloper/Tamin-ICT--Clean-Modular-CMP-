package com.tamin.taminhamrah.useCases.pension

import app.cash.turbine.test
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDN
import com.tamin.taminhamrah.repository.pension.FakePensionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckRetirementStatusUseCaseTest : BaseUseCaseTest() {

    private lateinit var pensionRepository: FakePensionRepository
    private lateinit var useCase: CheckRetirementStatusUseCase

    @BeforeTest
    fun setup() {
        pensionRepository = FakePensionRepository()
        useCase = CheckRetirementStatusUseCase(pensionRepository)
    }

    @Test
    fun `invoke should return retirement status from repository`() = runTest {
        val expected = RetirementStatusDN(
            requestId = "123",
            requestStatusCode = "1"
        )
        pensionRepository.retirementStatusResult = expected

        useCase().test {
            val result = awaitItem()
            assertEquals(expected, result)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        pensionRepository.shouldThrowError = true
        pensionRepository.error = expectedException

        useCase().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
