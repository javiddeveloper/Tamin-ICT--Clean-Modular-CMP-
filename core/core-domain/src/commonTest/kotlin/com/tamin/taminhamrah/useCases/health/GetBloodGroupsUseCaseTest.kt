package com.tamin.taminhamrah.useCases.health

import app.cash.turbine.test
import com.tamin.taminhamrah.model.health.BloodGroupDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetBloodGroupsUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: GetBloodGroupsUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = GetBloodGroupsUseCase(repository)
    }

    @Test
    fun `invoke should return blood groups`() = runTest {
        val expected = listOf(BloodGroupDN(key = 1, value = "A+"), BloodGroupDN(key = 2, value = "O-"))
        repository.getBloodGroupsResult = expected

        useCase().test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return empty list when no blood groups`() = runTest {
        repository.getBloodGroupsResult = emptyList()

        useCase().test {
            assertEquals(emptyList(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Server error")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
