package com.tamin.taminhamrah.useCases.inspection

import app.cash.turbine.test
import com.tamin.taminhamrah.model.inspection.BranchDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.inspection.FakeInspectionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetBranchPageUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeInspectionRepository
    private lateinit var useCase: GetBranchPageUseCase

    @BeforeTest
    fun setup() {
        repository = FakeInspectionRepository()
        useCase = GetBranchPageUseCase(repository)
    }

    @Test
    fun invoke_returnsPageAndTotalFromRepository() = runTest {
        val expected = listOf(
            BranchDN(
                operation = "", code = "0010", name = "یک تهران",
                minCode = "", maxCode = "", type = "", branchAddress = "",
                cityCode = "", status = ""
            )
        )
        repository.branchPageResult = expected
        repository.branchPageTotal = 3

        useCase(ApiQueryParamDN()).test {
            val page = awaitItem()
            assertEquals(expected, page.items)
            assertEquals(3, page.total)
            awaitComplete()
        }
    }

    @Test
    fun invoke_passesQueryToRepository() = runTest {
        val query = ApiQueryParamDN(page = 1, start = 10, limit = 10)

        useCase(query).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(query, repository.lastBranchQuery)
    }

    @Test
    fun invoke_onFailure_propagatesException() = runTest {
        repository.shouldThrowError = true

        useCase(ApiQueryParamDN()).test {
            awaitError()
        }
    }
}
