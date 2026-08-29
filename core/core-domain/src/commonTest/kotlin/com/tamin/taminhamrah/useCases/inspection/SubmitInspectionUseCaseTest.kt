package com.tamin.taminhamrah.useCases.inspection

import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestResultDN
import com.tamin.taminhamrah.repository.inspection.FakeInspectionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SubmitInspectionUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeInspectionRepository
    private lateinit var useCase: SubmitInspectionUseCase

    @BeforeTest
    fun setup() {
        repository = FakeInspectionRepository()
        useCase = SubmitInspectionUseCase(repository)
    }

    @Test
    fun invoke_returnsResultFromRepository() = runTest {
        val request = SubmitInspectionRequestDN(
            brchCode = "0010", endDate = 0L, inspectionNumberOld = "",
            insuranceId = "", insuranceJob = "", requestDescription = "",
            startDate = 0L, workshopAddress = "", workshopManager = "",
            workshopName = "", workshopNumber = "", workshopTel = ""
        )
        val expected = SubmitInspectionRequestResultDN(id = 123L)
        repository.submitResult = expected

        val result = useCase(request)

        assertEquals(expected, result)
        assertEquals(request, repository.lastSubmitRequest)
    }

    @Test
    fun invoke_onFailure_propagatesException() = runTest {
        repository.shouldThrowError = true

        assertFailsWith<RuntimeException> {
            useCase(
                SubmitInspectionRequestDN(
                    brchCode = "0010", endDate = 0L, inspectionNumberOld = "",
                    insuranceId = "", insuranceJob = "", requestDescription = "",
                    startDate = 0L, workshopAddress = "", workshopManager = "",
                    workshopName = "", workshopNumber = "", workshopTel = ""
                )
            )
        }
    }
}
