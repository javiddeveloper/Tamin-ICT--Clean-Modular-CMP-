package com.tamin.taminhamrah.useCases.treatment

import app.cash.turbine.test
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDN
import com.tamin.taminhamrah.repository.FakeTreatmentRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetDeservedTreatmentUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeTreatmentRepository
    private lateinit var useCase: GetDeservedTreatmentUseCase

    @BeforeTest
    fun setup() {
        repository = FakeTreatmentRepository()
        useCase = GetDeservedTreatmentUseCase(repository)
    }

    @Test
    fun `invoke should return deserved treatment list`() = runTest {
        val expected = listOf(
            DeservedTreatmentDN(
                birthDate = "13700101",
                brhCode = "12",
                brhName = "Branch",
                dependenceType = "Main",
                fatherName = "Father",
                feranshiz = "10%",
                firstName = "John",
                gender = "Male",
                healthBookletDate = 123456L,
                id = 1,
                idNumber = "123",
                insuranceType = "Type",
                lastBookletDate = "13990101",
                lastName = "Doe",
                natCode = "1234567890",
                nationalId = "1234567890",
                parentRisuid = "1",
                provinceCode = "2",
                provinceName = "Province",
                regWorkshopId = "3",
                regWorkshopName = "Workshop",
                risuid = "4",
                message = "Success",
                finalDesc = null,
                illness = "None",
                trackingCode = "999"
            )
        )
        repository.getDeservedTreatmentResult = expected

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
    fun `invoke should emit empty list when no deserved treatment exists`() = runTest {
        repository.getDeservedTreatmentResult = emptyList()

        useCase("1234567890").test {
            assertEquals(emptyList(), awaitItem())
            awaitComplete()
        }
    }
}
