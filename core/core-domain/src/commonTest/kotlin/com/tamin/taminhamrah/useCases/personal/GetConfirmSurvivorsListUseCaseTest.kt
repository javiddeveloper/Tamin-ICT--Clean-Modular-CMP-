package com.tamin.taminhamrah.useCases.personal

import app.cash.turbine.test
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDN
import com.tamin.taminhamrah.model.personal.survivorList.RequestModelDN
import com.tamin.taminhamrah.repository.FakePersonalRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetConfirmSurvivorsListUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakePersonalRepository
    private lateinit var useCase: GetConfirmSurvivorsListUseCase

    @BeforeTest
    fun setup() {
        repository = FakePersonalRepository()
        useCase = GetConfirmSurvivorsListUseCase(repository)
    }

    @Test
    fun `invoke should return confirm survivors list from repository`() = runTest {
        val expectedList = listOf(
            ConfirmSurvivorDN(
                request = RequestModelDN(id = 1)
            )
        )
        repository.confirmSurvivorsListResult = expectedList

        useCase(emptyList()).test {
            val result = awaitItem()
            assertEquals(expectedList, result)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase(emptyList()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
