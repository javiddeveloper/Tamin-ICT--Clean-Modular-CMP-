package com.tamin.taminhamrah.useCases.health

import app.cash.turbine.test
import com.tamin.taminhamrah.model.health.ProvinceCityItemDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetProvinceCitiesUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: GetProvinceCitiesUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = GetProvinceCitiesUseCase(repository)
    }

    @Test
    fun `invoke should return cities for given province`() = runTest {
        val expected = listOf(ProvinceCityItemDN(id = 10, name = "Tehran"), ProvinceCityItemDN(id = 11, name = "Karaj"))
        repository.getProvinceCitiesResult = expected

        useCase(provinceID = 1).test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return empty list when no cities`() = runTest {
        repository.getProvinceCitiesResult = emptyList()

        useCase(provinceID = 99).test {
            assertEquals(emptyList(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Network error")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase(provinceID = 1).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
