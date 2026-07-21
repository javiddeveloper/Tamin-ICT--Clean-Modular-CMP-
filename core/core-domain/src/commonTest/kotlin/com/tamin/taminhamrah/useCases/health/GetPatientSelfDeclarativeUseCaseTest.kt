package com.tamin.taminhamrah.useCases.health

import app.cash.turbine.test
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPatientSelfDeclarativeUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: GetPatientSelfDeclarativeUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = GetPatientSelfDeclarativeUseCase(repository)
    }

    @Test
    fun `invoke should return patient self declarative info`() = runTest {
        val expected = PatientSelfDeclarativeDN(
            alcoholDesc = "No", alcoholUsage = 0, alcoholUsageTitle = "None",
            exerciseDesc = "Yes", exerciseFreq = 3, exerciseFreqTitle = "Weekly",
            lastUpdateDate = "14020101", objectID = 1, smokingDesc = "No",
            smokingStatus = 0, smokingStatusTitle = "None", substanceDesc = "No",
            substanceUsage = 0, substanceUsageTitle = "None"
        )
        repository.getPatientSelfDeclarativeResult = expected

        useCase("1234567890", 1).test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase("1234567890", 1).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }

    @Test
    fun `invoke should return self declarative with null fields`() = runTest {
        val expected = PatientSelfDeclarativeDN(
            alcoholDesc = null, alcoholUsage = null, alcoholUsageTitle = null,
            exerciseDesc = null, exerciseFreq = null, exerciseFreqTitle = null,
            lastUpdateDate = null, objectID = 0, smokingDesc = null,
            smokingStatus = null, smokingStatusTitle = null, substanceDesc = null,
            substanceUsage = null, substanceUsageTitle = null
        )
        repository.getPatientSelfDeclarativeResult = expected

        useCase("1234567890", 1).test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }
}
