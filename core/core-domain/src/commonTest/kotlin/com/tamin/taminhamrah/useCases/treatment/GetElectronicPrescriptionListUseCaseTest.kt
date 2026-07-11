package com.tamin.taminhamrah.useCases.treatment

import app.cash.turbine.test
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDN
import com.tamin.taminhamrah.repository.FakeTreatmentRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetElectronicPrescriptionListUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeTreatmentRepository
    private lateinit var useCase: GetElectronicPrescriptionListUseCase

    @BeforeTest
    fun setup() {
        repository = FakeTreatmentRepository()
        useCase = GetElectronicPrescriptionListUseCase(repository)
    }

    @Test
    fun `invoke should return prescription list`() = runTest {
        val expected = listOf(
            ElectronicPrescriptionDN(
                id = "1", docId = "doc1", docName = "Doctor", flagSata = "1",
                location = "Location", noteHeadEprescID = 100L, patientID = "patient1",
                patientName = "Patient", prescDate = "14020101", prescName = "Prescription",
                specDesc = "Specialty", prescType = "1", trackingCode = 200L
            )
        )
        repository.getElectronicPrescriptionListResult = expected

        useCase("1", "1234567890", "0987654321", "startDate", "endDate").test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase("1", "1234567890", "0987654321", "startDate", "endDate").test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }

    @Test
    fun `invoke should emit empty list when no prescriptions exist`() = runTest {
        repository.getElectronicPrescriptionListResult = emptyList()

        useCase("1", "1234567890", "0987654321", "startDate", "endDate").test {
            assertEquals(emptyList<ElectronicPrescriptionDN>(), awaitItem())
            awaitComplete()
        }
    }
}
