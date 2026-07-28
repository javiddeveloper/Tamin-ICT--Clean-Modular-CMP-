package com.tamin.taminhamrah.useCases.common

import app.cash.turbine.test
import com.tamin.taminhamrah.repository.common.FakeCommonRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetRegistrationDeclarationFormUseCaseTest : BaseUseCaseTest() {

    private lateinit var commonRepository: FakeCommonRepository
    private lateinit var useCase: GetRegistrationDeclarationFormUseCase

    @BeforeTest
    fun setup() {
        commonRepository = FakeCommonRepository()
        useCase = GetRegistrationDeclarationFormUseCase(commonRepository)
    }

    @Test
    fun `invoke should return byte array from repository`() = runTest {
        val expectedData = byteArrayOf(1, 2, 3, 4)
        commonRepository.registrationDeclarationFormResult = expectedData

        useCase.invoke().test {
            val result = awaitItem()
            assertTrue(expectedData.contentEquals(result))
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("PDF Download Error")
        commonRepository.shouldThrowError = true
        commonRepository.registrationDeclarationFormError = expectedException

        useCase.invoke().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
