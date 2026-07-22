package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.IllnessSelfDeclareRequest
import com.tamin.taminhamrah.model.health.SyncIllnessSelfDeclarativesRequest
import com.tamin.taminhamrah.model.health.SyncResultDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SyncIllnessSelfDeclarativesUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: SyncIllnessSelfDeclarativesUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = SyncIllnessSelfDeclarativesUseCase(repository)
    }

    private fun buildRequest() = SyncIllnessSelfDeclarativesRequest(
        natCode = "1234567890",
        patientID = 1,
        illnessSelfDeclareList = listOf(
            IllnessSelfDeclareRequest(illnessID = 1, relation = 0, illnessComments = null),
            IllnessSelfDeclareRequest(illnessID = 2, relation = 1, illnessComments = "Family history")
        )
    )

    @Test
    fun `invoke should return sync result when successful`() = runTest {
        val expected = SyncResultDN(data = "Synced successfully")
        repository.syncIllnessesResult = expected

        val result = useCase(buildRequest())
        assertEquals(expected, result)
    }

    @Test
    fun `invoke should return sync result with null data`() = runTest {
        val expected = SyncResultDN(data = null)
        repository.syncIllnessesResult = expected

        val result = useCase(buildRequest())
        assertEquals(expected, result)
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
