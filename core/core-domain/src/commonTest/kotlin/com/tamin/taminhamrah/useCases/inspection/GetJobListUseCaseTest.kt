package com.tamin.taminhamrah.useCases.inspection

import com.tamin.taminhamrah.model.inspection.JobDN
import com.tamin.taminhamrah.model.inspection.JobListDN
import com.tamin.taminhamrah.repository.inspection.FakeInspectionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetJobListUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeInspectionRepository
    private lateinit var useCase: GetJobListUseCase

    @BeforeTest
    fun setup() {
        repository = FakeInspectionRepository()
        useCase = GetJobListUseCase(repository)
    }

    @Test
    fun invoke_returnsListFromRepository() = runTest {
        val expected = JobListDN(
            total = 1,
            list = listOf(
                JobDN(
                    operation = "", jobCode = "2035", jobDescription = "قرص ساز",
                    status = "", statusDate = ""
                )
            )
        )
        repository.jobsResult = expected

        val result = useCase(emptyList())

        assertEquals(expected, result)
        assertEquals(emptyList(), repository.lastJobsFilters)
    }

    @Test
    fun invoke_onFailure_propagatesException() = runTest {
        repository.shouldThrowError = true

        assertFailsWith<RuntimeException> {
            useCase(emptyList())
        }
    }
}
