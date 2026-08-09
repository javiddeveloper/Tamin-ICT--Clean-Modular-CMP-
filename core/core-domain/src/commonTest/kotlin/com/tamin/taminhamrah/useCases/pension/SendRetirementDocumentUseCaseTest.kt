package com.tamin.taminhamrah.useCases.pension

import app.cash.turbine.test
import com.tamin.taminhamrah.model.pension.retirement.RetirementSaveDocumentDN
import com.tamin.taminhamrah.repository.pension.FakePensionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SendRetirementDocumentUseCaseTest : BaseUseCaseTest() {

    private lateinit var pensionRepository: FakePensionRepository
    private lateinit var useCase: SendRetirementDocumentUseCase

    @BeforeTest
    fun setup() {
        pensionRepository = FakePensionRepository()
        useCase = SendRetirementDocumentUseCase(pensionRepository)
    }

    @Test
    fun `invoke should return success from repository`() = runTest {
        val expectedResult = "Success"
        pensionRepository.sendRetirementDocumentResult = expectedResult

        val request = RetirementSaveDocumentDN(null,null)

        useCase.invoke("requestId", request).test {
            val result = awaitItem()
            assertEquals(expectedResult, result)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Request failed")
        pensionRepository.shouldThrowError = true
        pensionRepository.error = expectedException

        val request = RetirementSaveDocumentDN(null,null)

        useCase.invoke("requestId", request).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
