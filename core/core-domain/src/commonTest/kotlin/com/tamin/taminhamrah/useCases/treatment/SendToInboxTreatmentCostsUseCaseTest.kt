package com.tamin.taminhamrah.useCases.treatment

import app.cash.turbine.test
import com.tamin.taminhamrah.repository.FakeTreatmentRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SendToInboxTreatmentCostsUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeTreatmentRepository
    private lateinit var useCase: SendToInboxTreatmentCostsUseCase

    @BeforeTest
    fun setup() {
        repository = FakeTreatmentRepository()
        useCase = SendToInboxTreatmentCostsUseCase(repository)
    }

    @Test
    fun `invoke should return response string`() = runTest {
        val expected = "SUCCESS"
        repository.sendToInboxTreatmentCostsResult = expected

        useCase("repId").test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase("repId").test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }

    @Test
    fun `invoke should emit empty response when repository returns empty string`() = runTest {
        repository.sendToInboxTreatmentCostsResult = ""

        useCase("repId").test {
            assertEquals("", awaitItem())
            awaitComplete()
        }
    }
}
