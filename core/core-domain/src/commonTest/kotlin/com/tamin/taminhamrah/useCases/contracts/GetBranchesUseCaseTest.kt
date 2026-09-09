package com.tamin.taminhamrah.useCases.contracts

import app.cash.turbine.test
import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.repository.contracts.FakeContractsRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetBranchesUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: GetBranchesUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = GetBranchesUseCase(repository)
    }

    @Test
    fun `invoke should return branches from repository`() = runTest {
        val expectedBranches = listOf(
            BranchDN(
                code = "001",
                name = "شعبه مرکزی",
                branchAddress = "تهران",
                cityCode = "0101",
                minCode = null,
                maxCode = null,
            ),
        )
        repository.branchesResult = expectedBranches

        useCase("0101").test {
            assertEquals(PagedListDN(items = expectedBranches, total = expectedBranches.size), awaitItem())
            awaitComplete()
        }

        assertEquals("0101", repository.lastBranchCityCode)
        assertEquals(1, repository.lastBranchesPage)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase("0101").test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
