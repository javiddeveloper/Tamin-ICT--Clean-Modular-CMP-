package com.tamin.taminhamrah.useCases.health

import app.cash.turbine.test
import com.tamin.taminhamrah.model.health.PatientImagingDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPatientImagingUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: GetPatientImagingUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = GetPatientImagingUseCase(repository)
    }

    @Test
    fun `invoke should return imaging list`() = runTest {
        val expected = listOf(
            PatientImagingDN(
                deliverStatus = null, diagCode = null, diagDesc = null, docSpeciality = null,
                doctorName = "Dr", healthcareProvider = "Imaging Center", imagingName = "MRI",
                itemComments = null, modality = "MR", objectId = 7, resultDesc = "Normal",
                visitDate = "14020101", visitType = null
            )
        )
        repository.getPatientImagingResult = expected

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
