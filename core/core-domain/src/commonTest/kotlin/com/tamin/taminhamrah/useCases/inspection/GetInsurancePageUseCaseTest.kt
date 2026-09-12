package com.tamin.taminhamrah.useCases.inspection

import app.cash.turbine.test
import com.tamin.taminhamrah.model.inspection.InspectionPerformedDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.inspection.FakeInspectionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetInsurancePageUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeInspectionRepository
    private lateinit var useCase: GetInsurancePageUseCase

    @BeforeTest
    fun setup() {
        repository = FakeInspectionRepository()
        useCase = GetInsurancePageUseCase(repository)
    }

    @Test
    fun invoke_returnsPageAndTotalFromRepository() = runTest {
        val expected = listOf(
            InspectionPerformedDN(
                activityDesc = "تست", branchCode = "", branchdesc = "",
                inspectionDate = 0L, inspectionNo = "", insuranceNo = "",
                objectable = "", relationType = "", workshopName = "",
                workshopNo = "", nationalCode = ""
            )
        )
        repository.insurancePageResult = expected
        repository.insurancePageTotal = 7

        useCase(ApiQueryParamDN()).test {
            val page = awaitItem()
            assertEquals(expected, page.items)
            assertEquals(7, page.total)
            awaitComplete()
        }
    }

    @Test
    fun invoke_passesQueryToRepository() = runTest {
        val query = ApiQueryParamDN(page = 2, start = 20, limit = 10)

        useCase(query).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(query, repository.lastInsuranceQuery)
    }

    @Test
    fun invoke_onFailure_propagatesException() = runTest {
        repository.shouldThrowError = true

        useCase(ApiQueryParamDN()).test {
            awaitError()
        }
    }
}
