package com.tamin.taminhamrah.useCases.contracts

import app.cash.turbine.test
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.repository.contracts.FakeContractsRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetFreeJobWagesUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: GetFreeJobWagesUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = GetFreeJobWagesUseCase(repository)
    }

    @Test
    fun `invoke should return free jobs from repository`() = runTest {
        val expectedJobs = listOf(
            FreeJobDN(
                discrioption = "تاسیساتی",
                endDate = null,
                fixRank = null,
                id = 1,
                iscoCode = null,
                jobCode = "099796",
                startDate = null,
                status = "1",
            ),
        )
        repository.freeJobWagesResult = expectedJobs

        useCase().test {
            assertEquals(expectedJobs, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
