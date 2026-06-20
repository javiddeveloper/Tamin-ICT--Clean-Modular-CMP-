package com.tamin.taminhamrah.useCases.pension

import app.cash.turbine.test
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.repository.pension.FakePensionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPensionerIdUseCaseTest : BaseUseCaseTest() {

    private lateinit var pensionRepository: FakePensionRepository
    private lateinit var useCase: GetPensionerIdUseCase

    @BeforeTest
    fun setup() {
        pensionRepository = FakePensionRepository()
        useCase = GetPensionerIdUseCase(pensionRepository)
    }

    @Test
    fun `invoke should return pensioner id list from repository`() = runTest {
        val expectedList = listOf(
            PensionIdDN(pensionerId = "123")
        )
        pensionRepository.pensionIdResult = expectedList

        useCase().test {
            val result = awaitItem()
            assertEquals(expectedList, result)
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
