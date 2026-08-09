package com.tamin.taminhamrah.useCases.health

import app.cash.turbine.test
import com.tamin.taminhamrah.model.health.PatientGeneralDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPatientGeneralUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: GetPatientGeneralUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = GetPatientGeneralUseCase(repository)
    }

    @Test
    fun `invoke should return patient general info`() = runTest {
        val expected = PatientGeneralDN(
            ptientID = 1, patientName = "John", patientFamily = "Doe",
            patientNatCode = "1234567890", patientAge = "30", patientGender = "Male",
            patientBirthDate = "13700101", patientMobile = "09123456789",
            patientAddress = "Address", patientFather = "Father"
        )
        repository.getPatientGeneralResult = expected

        useCase("1234567890").test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase("1234567890").test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }

    @Test
    fun `invoke should return patient general with null optional fields`() = runTest {
        val expected = PatientGeneralDN(
            ptientID = 0, patientName = "", patientFamily = "", patientNatCode = "",
            patientAge = "", patientGender = "", patientBirthDate = "",
            patientMobile = null, patientAddress = null, patientFather = null
        )
        repository.getPatientGeneralResult = expected

        useCase("1234567890").test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }
}
