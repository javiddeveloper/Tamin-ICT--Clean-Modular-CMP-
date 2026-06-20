package com.tamin.taminhamrah.useCases.personal

import app.cash.turbine.test
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.repository.FakePersonalRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAgeUseCaseTest : BaseUseCaseTest() {

    private lateinit var personalRepository: FakePersonalRepository
    private lateinit var useCase: GetAgeUseCase

    @BeforeTest
    fun setup() {
        personalRepository = FakePersonalRepository()
        useCase = GetAgeUseCase(personalRepository)
    }

    @Test
    fun `invoke should return age from repository`() = runTest {
        val expectedAge = AgeDN(
            age = "24",
            birthDate = "1379/01/01"
        )
        personalRepository.ageResult = expectedAge

        useCase.invoke(1379L).test {
            val result = awaitItem()
            assertEquals("24", result.age)
            assertEquals("1379/01/01", result.birthDate)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error`() = runTest {
        val expectedException = RuntimeException("get age failed")
        personalRepository.shouldThrowError = true
        personalRepository.error = expectedException

        useCase.invoke(1379L).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
