package com.tamin.taminhamrah.useCases.history

import com.tamin.taminhamrah.repository.history.FakeHistoryRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SendToInstitutionUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHistoryRepository
    private lateinit var useCase: SendToInstitutionUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHistoryRepository()
        useCase = SendToInstitutionUseCase(repository)
    }

    @Test
    fun `invoke should delegate to repository with all params true`() = runTest {
        useCase(type1 = true, type2 = true, type3 = true)

        assertEquals(true, repository.lastSentType1)
        assertEquals(true, repository.lastSentType2)
        assertEquals(true, repository.lastSentType3)
    }

    @Test
    fun `invoke should pass mixed params correctly`() = runTest {
        useCase(type1 = false, type2 = true, type3 = false)

        assertEquals(false, repository.lastSentType1)
        assertEquals(true, repository.lastSentType2)
        assertEquals(false, repository.lastSentType3)
    }

    @Test
    fun `invoke should pass all params false`() = runTest {
        useCase(type1 = false, type2 = false, type3 = false)

        assertEquals(false, repository.lastSentType1)
        assertEquals(false, repository.lastSentType2)
        assertEquals(false, repository.lastSentType3)
    }

    @Test
    fun `invoke should propagate exception from repository`() = runTest {
        repository.shouldThrowError = true

        assertFailsWith<RuntimeException> {
            useCase(type1 = true, type2 = true, type3 = true)
        }
    }
}
