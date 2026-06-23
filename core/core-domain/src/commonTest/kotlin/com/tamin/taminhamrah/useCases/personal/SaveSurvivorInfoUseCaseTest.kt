package com.tamin.taminhamrah.useCases.personal

import app.cash.turbine.test
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoDN
import com.tamin.taminhamrah.repository.FakePersonalRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SaveSurvivorInfoUseCaseTest : BaseUseCaseTest() {

    private lateinit var personalRepository: FakePersonalRepository
    private lateinit var useCase: SaveSurvivorInfoUseCase

    @BeforeTest
    fun setup() {
        personalRepository = FakePersonalRepository()
        useCase = SaveSurvivorInfoUseCase(personalRepository)
    }

    @Test
    fun `invoke should return result from repository`() = runTest {
        val body = SaveSurvivorInfoDN(firstName = "John", lastName = "Doe")
        val expectedResult = "Success"
        personalRepository.saveSurvivorInfoResult = expectedResult

        useCase.invoke(body).test {
            assertEquals(expectedResult, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error`() = runTest {
        val body = SaveSurvivorInfoDN(firstName = "John", lastName = "Doe")
        val expectedException = RuntimeException("saving failed")
        personalRepository.shouldThrowError = true
        personalRepository.error = expectedException

        useCase.invoke(body).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
