package com.tamin.taminhamrah.useCases.contracts

import app.cash.turbine.test
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.util.PagedListDN
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
        repository.freeJobWagesTotal = expectedJobs.size

        useCase(page = 1).test {
            assertEquals(PagedListDN(items = expectedJobs, total = expectedJobs.size), awaitItem())
            awaitComplete()
        }
        assertEquals(1, repository.lastFreeJobWagesPage)
        assertEquals(null, repository.lastFreeJobWagesSearchQuery)
    }

    @Test
    fun `invoke should pass search query to repository`() = runTest {
        repository.freeJobWagesResult = emptyList()
        repository.freeJobWagesTotal = 0

        useCase(page = 2, searchQuery = "برنامه").test {
            assertEquals(PagedListDN(items = emptyList(), total = 0), awaitItem())
            awaitComplete()
        }
        assertEquals(2, repository.lastFreeJobWagesPage)
        assertEquals("برنامه", repository.lastFreeJobWagesSearchQuery)
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
