package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.DrugAllergyRequest
import com.tamin.taminhamrah.model.health.HealthProblemDN
import com.tamin.taminhamrah.model.health.SyncDrugAllergiesRequest
import com.tamin.taminhamrah.model.health.SyncResultDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SyncDrugAllergiesUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: SyncDrugAllergiesUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = SyncDrugAllergiesUseCase(repository)
    }

    private fun buildRequest() = SyncDrugAllergiesRequest(
        natCode = "1234567890",
        patientID = 1,
        drugAllergyList = listOf(
            DrugAllergyRequest(drugId = 10, allergyComments = "Rash"),
            DrugAllergyRequest(drugId = 20, allergyComments = null)
        )
    )

    @Test
    fun `invoke should return sync result when successful`() = runTest {
        val expected = SyncResultDN(data = "OK")
        repository.syncDrugAllergiesResult = expected

        val result = useCase(buildRequest())
        assertEquals(expected, result.data)
        assertTrue(result.problems.isEmpty())
    }

    @Test
    fun `invoke should return sync result with null data`() = runTest {
        val expected = SyncResultDN(data = null)
        repository.syncDrugAllergiesResult = expected

        val result = useCase(buildRequest())
        assertEquals(expected, result.data)
    }

    @Test
    fun `invoke should return business problems instead of throwing when backend flags them`() = runTest {
        val expectedProblems = listOf(HealthProblemDN(code = 9005, message = "شناسه دارو نامعتبر است."))
        repository.syncDrugAllergiesProblems = expectedProblems

        val result = useCase(buildRequest())
        assertEquals(null, result.data)
        assertEquals(expectedProblems, result.problems)
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Sync failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        val actualException = assertFailsWith<RuntimeException> {
            useCase(buildRequest())
        }
        assertEquals(expectedException.message, actualException.message)
    }
}
