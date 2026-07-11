package com.tamin.taminhamrah.useCases.treatment

import app.cash.turbine.test
import com.tamin.taminhamrah.model.treatment.MedicalAuthoritiesDN
import com.tamin.taminhamrah.repository.FakeTreatmentRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetMedicalAuthoritiesUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeTreatmentRepository
    private lateinit var useCase: GetMedicalAuthoritiesUseCase

    @BeforeTest
    fun setup() {
        repository = FakeTreatmentRepository()
        useCase = GetMedicalAuthoritiesUseCase(repository)
    }

    @Test
    fun `invoke should return medical authorities confirmation list`() = runTest {
        val expected = listOf(
            MedicalAuthoritiesDN(
                supportType = "Type", treatmentCenter = "Center", confirmInBranch = "Yes",
                confirmStatus = "Approved", insuranceNumber = "123", nationalCode = "1234567890",
                firstName = "John", lastName = "Doe", outpatientRestStartDate = "14020101",
                outpatientRestEndDate = "14020110", numberOfOutpatientDays = "10",
                hospitalizationStartDate = "14020101", hospitalizationEndDate = "14020105",
                numberOfHospitalizationDays = "5", description = "Desc", branch = "BranchName",
                fromDateNotConfirm = "14020101", toDateNotConfirm = "14020105"
            )
        )
        repository.getConfirmationMedicalAuthoritiesResult = expected

        useCase(emptyList()).test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase(emptyList()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }

    @Test
    fun `invoke should emit empty list when no confirmations exist`() = runTest {
        repository.getConfirmationMedicalAuthoritiesResult = emptyList()

        useCase(emptyList()).test {
            assertEquals(emptyList<MedicalAuthoritiesDN>(), awaitItem())
            awaitComplete()
        }
    }
}
