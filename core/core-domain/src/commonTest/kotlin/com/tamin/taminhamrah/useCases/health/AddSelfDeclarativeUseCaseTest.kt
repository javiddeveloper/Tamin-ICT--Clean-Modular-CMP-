package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.AddSelfDeclarativeDN
import com.tamin.taminhamrah.model.health.AddSelfDeclarativeRequest
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AddSelfDeclarativeUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: AddSelfDeclarativeUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = AddSelfDeclarativeUseCase(repository)
    }

    private fun buildRequest() = AddSelfDeclarativeRequest(
        natCode = "1234567890", patientID = 1, smoking = 1, smokeDesc = "Light",
        alcoholUse = 0, alcoholUseDesc = null, substanceUse = 0, substanceUseDesc = null,
        exerciseFrequency = 2, exerciseDesc = "3 times/week"
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
        assertEquals(expected, result)
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
