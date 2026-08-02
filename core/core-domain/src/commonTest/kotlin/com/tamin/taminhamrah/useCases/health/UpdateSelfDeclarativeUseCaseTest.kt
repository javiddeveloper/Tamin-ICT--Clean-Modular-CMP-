package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.HealthProblemDN
import com.tamin.taminhamrah.model.health.UpdateSelfDeclarativeDN
import com.tamin.taminhamrah.model.health.UpdateSelfDeclarativeRequest
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class UpdateSelfDeclarativeUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: UpdateSelfDeclarativeUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = UpdateSelfDeclarativeUseCase(repository)
    }

    private fun buildRequest() = UpdateSelfDeclarativeRequest(
        patientID = 1, objectID = 5, smoking = 2, smokeDesc = "Heavy",
        alcoholUse = 0, alcoholUseDesc = null, substanceUse = 0, substanceUseDesc = null,
        exerciseFrequency = 1, exerciseDesc = "Once/week"
    )

    @Test
    fun `invoke should return updated self declarative result`() = runTest {
        val expected = UpdateSelfDeclarativeDN(
            objectID = 5, smokingStatus = 2, smokingStatusTitle = "Heavy", smokingDesc = "Heavy",
            alcoholUsage = 0, alcoholUsageTitle = "None", alcoholDesc = null,
            substanceUsage = 0, substanceUsageTitle = "None", substanceDesc = null,
            exerciseFreq = 1, exerciseFreqTitle = "Once/week", exerciseDesc = "Once/week",
            lastUpdateDate = "1403/01/15"
        )
        repository.updateSelfDeclarativeResult = expected

        val result = useCase(buildRequest())
        assertEquals(expected, result.data)
        assertTrue(result.problems.isEmpty())
    }

    @Test
    fun `invoke should return business problems instead of throwing when backend flags them`() = runTest {
        val expectedProblems = listOf(HealthProblemDN(code = 9003, message = "شناسه رکورد یافت نشد."))
        repository.updateSelfDeclarativeProblems = expectedProblems

        val result = useCase(buildRequest())
        assertEquals(null, result.data)
        assertEquals(expectedProblems, result.problems)
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Server error")
        repository.shouldThrowError = true
        repository.error = expectedException

        val actualException = assertFailsWith<RuntimeException> {
            useCase(buildRequest())
        }
        assertEquals(expectedException.message, actualException.message)
    }
}
