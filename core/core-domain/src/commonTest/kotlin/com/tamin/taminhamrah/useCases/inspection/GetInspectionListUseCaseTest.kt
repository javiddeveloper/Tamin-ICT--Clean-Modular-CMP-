package com.tamin.taminhamrah.useCases.inspection

import com.tamin.taminhamrah.model.inspection.InspectionPerformedDN
import com.tamin.taminhamrah.model.inspection.InspectionPerformedListDN
import com.tamin.taminhamrah.repository.inspection.FakeInspectionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetInspectionListUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeInspectionRepository
    private lateinit var useCase: GetInspectionListUseCase

    @BeforeTest
    fun setup() {
        repository = FakeInspectionRepository()
        useCase = GetInspectionListUseCase(repository)
    }

    @Test
    fun invoke_returnsListFromRepository() = runTest {
        val expected = InspectionPerformedListDN(
            total = 1,
            list = listOf(
                InspectionPerformedDN(
                    activityDesc = "تست", branchCode = "", branchdesc = "",
                    inspectionDate = 0L, inspectionNo = "", insuranceNo = "",
                    objectable = "", relationType = "", workshopName = "",
                    workshopNo = "", nationalCode = ""
                )
            )
        )
        repository.allInsuranceResult = expected

        val result = useCase(emptyList())

        assertEquals(expected, result)
        assertEquals(emptyList(), repository.lastAllInsuranceFilters)
    }

    @Test
    fun invoke_onFailure_propagatesException() = runTest {
        repository.shouldThrowError = true

        assertFailsWith<RuntimeException> {
            useCase(emptyList())
        }
    }
}
