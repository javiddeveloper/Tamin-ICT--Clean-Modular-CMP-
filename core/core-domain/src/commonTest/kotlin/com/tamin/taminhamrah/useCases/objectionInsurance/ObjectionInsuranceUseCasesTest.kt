package com.tamin.taminhamrah.useCases.objectionInsurance

import app.cash.turbine.test
import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryDN
import com.tamin.taminhamrah.repository.objectionInsurance.ObjectionInsuranceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeObjectionInsuranceRepository : ObjectionInsuranceRepository {
    var checkStatusConflictResult: Boolean = false
    var historiesResult: List<ObjectionInsuranceHistoryDN> = emptyList()
    var saveConflictResult: String? = null
    var confirmConflictResult: Boolean = true
    var finalConfirmConflictResult: String = "TRK"
    var lastSaveItems: List<ObjectionInsuranceHistoryDN>? = null
    var lastConfirmDescription: String? = null

    override fun checkStatusConflict(): Flow<Boolean> = flowOf(checkStatusConflictResult)

    override fun getConflictHistories(): Flow<List<ObjectionInsuranceHistoryDN>> =
        flowOf(historiesResult)

    override fun saveConflict(items: List<ObjectionInsuranceHistoryDN>): Flow<String?> {
        lastSaveItems = items
        return flowOf(saveConflictResult)
    }

    override fun confirmConflict(description: String?): Flow<Boolean> {
        lastConfirmDescription = description
        return flowOf(confirmConflictResult)
    }

    override fun finalConfirmConflict(): Flow<String> = flowOf(finalConfirmConflictResult)
}

class ObjectionInsuranceUseCasesTest {

    @Test
    fun checkStatusConflictUseCase_returnsRepositoryValue() = runTest {
        val repository = FakeObjectionInsuranceRepository().apply {
            checkStatusConflictResult = true
        }
        val useCase = CheckObjectionInsuranceStatusConflictUseCase(repository)

        useCase().test {
            assertTrue(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun getHistoriesUseCase_returnsList() = runTest {
        val repository = FakeObjectionInsuranceRepository().apply {
            historiesResult = listOf(ObjectionInsuranceHistoryDN(year = "1402"))
        }
        val useCase = GetObjectionInsuranceHistoriesUseCase(repository)

        useCase().test {
            assertEquals("1402", awaitItem().first().year)
            awaitComplete()
        }
    }

    @Test
    fun saveConflictUseCase_forwardsItems() = runTest {
        val repository = FakeObjectionInsuranceRepository().apply {
            saveConflictResult = null
        }
        val useCase = SaveObjectionInsuranceConflictUseCase(repository)
        val items = listOf(ObjectionInsuranceHistoryDN(year = "1401"))

        useCase(items).test {
            assertNull(awaitItem())
            awaitComplete()
        }
        assertEquals(items, repository.lastSaveItems)
    }

    @Test
    fun confirmConflictUseCase_forwardsDescription() = runTest {
        val repository = FakeObjectionInsuranceRepository()
        val useCase = ConfirmObjectionInsuranceConflictUseCase(repository)

        useCase("توضیح").test {
            assertTrue(awaitItem())
            awaitComplete()
        }
        assertEquals("توضیح", repository.lastConfirmDescription)
    }

    @Test
    fun finalConfirmConflictUseCase_returnsTrackingNumber() = runTest {
        val repository = FakeObjectionInsuranceRepository().apply {
            finalConfirmConflictResult = "TRK-1"
        }
        val useCase = FinalConfirmObjectionInsuranceConflictUseCase(repository)

        useCase().test {
            assertEquals("TRK-1", awaitItem())
            awaitComplete()
        }
    }
}
