package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.HealthProblemDN
import com.tamin.taminhamrah.model.health.UpdatePatientDN
import com.tamin.taminhamrah.model.health.UpdatePatientRequest
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class UpdatePatientUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: UpdatePatientUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = UpdatePatientUseCase(repository)
    }

    private fun buildRequest() = UpdatePatientRequest(
        patientID = 1, patientNatCode = "1234567890", patientMobile = "09123456789",
        patientEmail = null, patientAddress = "Address", patientArea = null,
        patientCityID = 10, patientBloodGroup = 2, patientMarriage = 1,
        patientJob = "Engineer", patientHeight = 175, patientWeight = 70,
        patientCitizenship = null, patientNationality = null, patientInsurance = null,
        emergencyName = "Jane", emergencyFamily = "Doe", emergencyMobile = "09111111111",
        emergencyEmail = null, emergencyRelation = 1, emergencyAddress = null,
        emergencyArea = null, emergencyCityID = 10
    )

    @Test
    fun `invoke should return updated patient domain model`() = runTest {
        val expected = UpdatePatientDN(
            ptientID = 1, patientNatCode = "1234567890", patientName = "John", patientFamily = "Doe",
            patientMobile = "09123456789", patientAddress = "Address", patientBloodGroup = null,
            patientBloodGroupCode = 2, patientMarriage = null, patientMarriageCode = 1,
            patientJob = "Engineer", patientHeight = "175", patientWeight = "70",
            patientBMI = null, patientCitizenship = null, patientCity = null, patientCityCode = 10,
            patientProvince = null, patientProvinceCode = null, patientEmail = null, patientArea = null,
            emergencyName = "Jane", emergencyFamily = "Doe", emergencyMobile = "09111111111",
            emergencyRelation = null, emergencyRelationshipCode = 1, emergencyAddress = null,
            emergencyCity = null, emergencyCityCode = 10, emergencyProvince = null,
            emergencyProvinceCode = null, emergencyEmail = null, emergencyArea = null, lastUpdateDate = null
        )
        repository.updatePatientResult = expected

        val result = useCase(buildRequest())
        assertEquals(expected, result.data)
        assertTrue(result.problems.isEmpty())
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke should return business problems instead of throwing when backend flags them`() = runTest {
        val expectedProblems = listOf(HealthProblemDN(code = 9001, message = "شناسه رکورد باید بزرگتر از 1 باشد."))
        repository.updatePatientProblems = expectedProblems

        val result = useCase(buildRequest())
        assertEquals(null, result.data)
        assertEquals(expectedProblems, result.problems)
        assertTrue(!result.isSuccess)
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Update failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        val actualException = assertFailsWith<RuntimeException> {
            useCase(buildRequest())
        }
        assertEquals(expectedException.message, actualException.message)
    }
}
