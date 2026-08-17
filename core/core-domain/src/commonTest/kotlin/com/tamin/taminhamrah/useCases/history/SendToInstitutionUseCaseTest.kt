package com.tamin.taminhamrah.useCases.history

import com.tamin.taminhamrah.model.history.HistoryCertificateType
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
    fun `invoke should delegate to repository with all types selected`() = runTest {
        val allTypes = setOf(HistoryCertificateType.ALL, HistoryCertificateType.WAGES, HistoryCertificateType.COMBINED)
        useCase(allTypes)
        assertEquals(allTypes, repository.lastSentTypes)
    }

    @Test
    fun `invoke should pass a single type correctly`() = runTest {
        val types = setOf(HistoryCertificateType.WAGES)
        useCase(types)
        assertEquals(types, repository.lastSentTypes)
    }

    @Test
    fun `invoke should pass empty set correctly`() = runTest {
        useCase(emptySet())
        assertEquals(emptySet(), repository.lastSentTypes)
    }

    @Test
    fun `invoke should propagate exception from repository`() = runTest {
        repository.shouldThrowError = true

        assertFailsWith<RuntimeException> {
            useCase(setOf(HistoryCertificateType.ALL))
        }
    }
}
