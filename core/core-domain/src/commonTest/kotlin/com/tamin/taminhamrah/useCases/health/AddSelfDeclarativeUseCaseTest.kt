package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.AddSelfDeclarativeDN
import com.tamin.taminhamrah.model.health.AddSelfDeclarativeRequest
import com.tamin.taminhamrah.model.health.HealthProblemDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AddSelfDeclarativeUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: AddSelfDeclarativeUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = AddSelfDeclarativeUseCase(repository)
    }

    private fun buildRequest() = AddSelfDeclarativeRequest(
        natCode = "1234567890", patientID = 1, smoking = 1,
        alcoholUse = 0,  substanceUse = 0,
        exerciseFrequency = 2,
    )

    @Test
    fun `invoke should return added self declarative result`() = runTest {
        val expected = AddSelfDeclarativeDN(
            objectID = 5, smokingStatus = 1, smokingStatusTitle = "Light", smokingDesc = "Light",
            alcoholUsage = 0, alcoholUsageTitle = "None", alcoholDesc = null,
            substanceUsage = 0, substanceUsageTitle = "None", substanceDesc = null,
            exerciseFreq = 2, exerciseFreqTitle = "3 times/week", exerciseDesc = "3 times/week",
            lastUpdateDate = "1403/01/01"
        )
        repository.addSelfDeclarativeResult = expected

        val result = useCase(buildRequest())
        assertEquals(expected, result.data)
        assertTrue(result.problems.isEmpty())
    }

    @Test
    fun `invoke should return business problems instead of throwing when backend flags them`() = runTest {
        val expectedProblems = listOf(HealthProblemDN(code = 9002, message = "این خوداظهاری قبلا ثبت شده است."))
        repository.addSelfDeclarativeProblems = expectedProblems

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
