package com.tamin.taminhamrah.useCases.personal

import app.cash.turbine.test
import com.tamin.taminhamrah.model.personal.GirlSurvivorConditionDN
import com.tamin.taminhamrah.repository.FakePersonalRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckGirlSurvivorConditionsUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakePersonalRepository
    private lateinit var useCase: CheckGirlSurvivorConditionsUseCase

    @BeforeTest
    fun setup() {
        repository = FakePersonalRepository()
        useCase = CheckGirlSurvivorConditionsUseCase(repository)
    }

    @Test
    fun `invoke should return survivor condition from repository`() = runTest {
        val expectedCondition = GirlSurvivorConditionDN(condition = "Eligible")
        repository.girlSurvivorConditionResult = expectedCondition

        useCase("1234567890", "987654321").test {
            val result = awaitItem()
            assertEquals(expectedCondition, result)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Network Error")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase("1234567890", "987654321").test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
