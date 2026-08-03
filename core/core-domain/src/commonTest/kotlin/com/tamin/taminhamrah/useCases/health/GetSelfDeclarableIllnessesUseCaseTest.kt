package com.tamin.taminhamrah.useCases.health

import app.cash.turbine.test
import com.tamin.taminhamrah.model.health.IllnessItemDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetSelfDeclarableIllnessesUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: GetSelfDeclarableIllnessesUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = GetSelfDeclarableIllnessesUseCase(repository)
    }

    @Test
    fun `invoke should return list of declarable illnesses`() = runTest {
        val expected = listOf(IllnessItemDN(illnessID = 1, illnessDesc = "Diabetes"), IllnessItemDN(illnessID = 2, illnessDesc = "Hypertension"))
        repository.getSelfDeclarableIllnessesResult = expected

        useCase().test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return empty list when no illnesses`() = runTest {
        repository.getSelfDeclarableIllnessesResult = emptyList()

        useCase().test {
            assertEquals(emptyList(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Network error")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
