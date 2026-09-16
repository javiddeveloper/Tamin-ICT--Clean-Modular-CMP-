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

class GetWorkshopInspectionsPageUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeInspectionRepository
    private lateinit var useCase: GetWorkshopInspectionsPageUseCase

    @BeforeTest
    fun setup() {
        repository = FakeInspectionRepository()
        useCase = GetWorkshopInspectionsPageUseCase(repository)
    }

    @Test
    fun invoke_returnsPageAndTotalFromRepository() = runTest {
        val expected = listOf(
            InspectionPerformedDN(
                activityDesc = "تست کارگاه", branchCode = "", branchdesc = "",
                inspectionDate = 0L, inspectionNo = "", insuranceNo = "",
                objectable = "", relationType = "", workshopName = "",
                workshopNo = "", nationalCode = ""
            )
        )
        repository.workshopInspectionsPageResult = expected
        repository.workshopInspectionsPageTotal = 5

        useCase(ApiQueryParamDN()).test {
            val page = awaitItem()
            assertEquals(expected, page.items)
            assertEquals(5, page.total)
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

        assertEquals(query, repository.lastWorkshopInspectionsQuery)
    }

    @Test
    fun invoke_onFailure_propagatesException() = runTest {
        repository.shouldThrowError = true

        useCase(ApiQueryParamDN()).test {
            awaitError()
        }
    }
}
