package com.tamin.taminhamrah.useCases.health

import app.cash.turbine.test
import com.tamin.taminhamrah.model.health.PatientLabDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPatientLabsUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: GetPatientLabsUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = GetPatientLabsUseCase(repository)
    }

    @Test
    fun `invoke should return labs list`() = runTest {
        val expected = listOf(
            PatientLabDN(
                deliveredQty = 1, diagCode = null, diagDesc = null, docSpeciality = null,
                docSpecialityCode = null, doctorName = "Dr", examName = "CBC",
                healthcareProvider = "Lab", itemComments = null, objectId = 5, prescribedQty = 1,
                resultDesc = "Normal", resultValue = "12", serviceProvideType = 1, sourceSystem = 1,
                visitDate = "14020101", visitType = null
            )
        )
        repository.getPatientLabsResult = expected

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
