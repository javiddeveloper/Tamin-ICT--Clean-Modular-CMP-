package com.tamin.taminhamrah.useCases.pension

import app.cash.turbine.test
import com.tamin.taminhamrah.model.pension.EdictPensionerInboxDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.pension.FakePensionRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SendEdictPensionerToMyInboxUseCaseTest {

    private lateinit var fakePensionRepository: FakePensionRepository
    private lateinit var useCase: SendEdictPensionerToMyInboxUseCase

    @BeforeTest
    fun setUp() {
        fakePensionRepository = FakePensionRepository()
        useCase = SendEdictPensionerToMyInboxUseCase(fakePensionRepository)
    }

    @Test
    fun `invoke should return success message from repository`() = runTest {
        val expectedResult = EdictPensionerInboxDN(message = "Success")
        fakePensionRepository.sendEdictPensionerToMyInboxResult = expectedResult

        useCase(emptyList()).test {
            assertEquals(expectedResult, awaitItem())
            awaitComplete()
        }
    }
}
