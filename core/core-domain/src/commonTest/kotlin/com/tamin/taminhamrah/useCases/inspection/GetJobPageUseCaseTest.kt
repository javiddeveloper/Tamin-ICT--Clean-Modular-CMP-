package com.tamin.taminhamrah.useCases.inspection

import app.cash.turbine.test
import com.tamin.taminhamrah.model.inspection.JobDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.inspection.FakeInspectionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetJobPageUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeInspectionRepository
    private lateinit var useCase: GetJobPageUseCase

    @BeforeTest
    fun setup() {
        repository = FakeInspectionRepository()
        useCase = GetJobPageUseCase(repository)
    }

    @Test
    fun invoke_returnsPageAndTotalFromRepository() = runTest {
        val expected = listOf(
            JobDN(
                operation = "", jobCode = "2035", jobDescription = "قرص ساز",
                status = "", statusDate = ""
            )
        )
        repository.jobPageResult = expected
        repository.jobPageTotal = 1

        useCase(ApiQueryParamDN()).test {
            val page = awaitItem()
            assertEquals(expected, page.items)
            assertEquals(1, page.total)
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

        assertEquals(query, repository.lastJobQuery)
    }

    @Test
    fun invoke_onFailure_propagatesException() = runTest {
        repository.shouldThrowError = true

        useCase(ApiQueryParamDN()).test {
            awaitError()
        }
    }
}
