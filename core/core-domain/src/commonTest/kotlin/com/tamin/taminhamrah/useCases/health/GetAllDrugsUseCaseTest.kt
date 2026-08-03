package com.tamin.taminhamrah.useCases.health

import app.cash.turbine.test
import com.tamin.taminhamrah.model.health.DrugItemDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllDrugsUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: GetAllDrugsUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = GetAllDrugsUseCase(repository)
    }

    @Test
    fun `invoke should return drugs list`() = runTest {
        val expected = listOf(DrugItemDN(drugID = 1, drugName = "Aspirin"), DrugItemDN(drugID = 2, drugName = "Penicillin"))
        repository.getAllDrugsResult = expected

        useCase().test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return empty list when no drugs`() = runTest {
        repository.getAllDrugsResult = emptyList()

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
