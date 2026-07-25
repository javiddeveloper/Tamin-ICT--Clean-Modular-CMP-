package com.tamin.taminhamrah.useCases.health

import app.cash.turbine.test
import com.tamin.taminhamrah.model.health.PatientVisitDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPatientVisitsUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: GetPatientVisitsUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = GetPatientVisitsUseCase(repository)
    }

    @Test
    fun `invoke should return visits list`() = runTest {
        val expected = listOf(
            PatientVisitDN(
                comments = null, diagCode = null, diagDesc = "Diag", docID = null,
                docSpeciality = "Cardiology", docSpecialityCode = null, doctorName = "Dr",
                healthcareProvider = "Clinic", serviceName = "Visit", serviceProvideType = null,
                serviceResult = null, sourceSystem = 1, visitDate = "14020101", visitType = null
            )
        )
        repository.getPatientVisitsResult = expected

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
            assertEquals(expectedException.message, awaitError().message)
        }
    }
}
