package com.tamin.taminhamrah.useCases.health

import app.cash.turbine.test
import com.tamin.taminhamrah.model.health.ProvinceItemDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllProvincesUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: GetAllProvincesUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = GetAllProvincesUseCase(repository)
    }

    @Test
    fun `invoke should return provinces list`() = runTest {
        val expected = listOf(ProvinceItemDN(id = 1, name = "Tehran"), ProvinceItemDN(id = 2, name = "Isfahan"))
        repository.getAllProvincesResult = expected

        useCase().test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return empty list when no provinces`() = runTest {
        repository.getAllProvincesResult = emptyList()

        useCase().test {
            assertEquals(emptyList(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Network error")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
