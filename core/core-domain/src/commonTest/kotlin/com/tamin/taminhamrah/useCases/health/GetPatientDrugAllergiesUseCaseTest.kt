package com.tamin.taminhamrah.useCases.health

import app.cash.turbine.test
import com.tamin.taminhamrah.model.health.DrugItemAllergiesDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPatientDrugAllergiesUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: GetPatientDrugAllergiesUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = GetPatientDrugAllergiesUseCase(repository)
    }

    @Test
    fun `invoke should return drug item allergies list`() = runTest {
        val expected = listOf(
            DrugItemAllergiesDN(
                allergyComments = "Comments", drugId = 1, drugName = "Drug"
            )
        )
        repository.getPatientDrugAllergiesResult = expected

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
    fun `invoke should emit empty list when patient has no drug allergies`() = runTest {
        repository.getPatientDrugAllergiesResult = emptyList()

        useCase("1234567890", 1).test {
            assertEquals(emptyList<DrugItemAllergiesDN>(), awaitItem())
            awaitComplete()
        }
    }
}
