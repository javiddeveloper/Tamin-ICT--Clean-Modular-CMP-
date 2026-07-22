package com.tamin.taminhamrah.useCases.health

import app.cash.turbine.test
import com.tamin.taminhamrah.model.health.PatientHospitalizationsDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPatientHospitalizationsUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: GetPatientHospitalizationsUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = GetPatientHospitalizationsUseCase(repository)
    }

    @Test
    fun `invoke should return hospitalizations list`() = runTest {
        val expected = listOf(
            PatientHospitalizationsDN(
                admId = 1, admSource = null, admType = null, comments = null, docID = null,
                docSpeciality = null, doctorName = "Dr", finalDiagCode = null, finalDiagDesc = "Diag",
                firstDiagCode = null, firstDiagDesc = null, healthcareProvider = "Hospital",
                hospitalizedDays = 3, hospitalizedEndDate = "14020105", hospitalizedStartDate = "14020102",
                outcomeDesc = null, referDocId = null, referDocName = null, referDocSpeciality = null,
                referHealthcareProvider = null
            )
        )
        repository.getPatientHospitalizationsResult = expected

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
