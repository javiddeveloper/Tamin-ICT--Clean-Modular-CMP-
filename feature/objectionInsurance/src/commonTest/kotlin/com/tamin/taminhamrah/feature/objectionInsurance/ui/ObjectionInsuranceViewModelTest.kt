package com.tamin.taminhamrah.feature.objectionInsurance.ui

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.objectionInsurance.fake.FakeObjectionInsuranceRepository
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionInsuranceIntent
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionInsuranceUiState
import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryDN
import com.tamin.taminhamrah.useCases.objectionInsurance.CheckObjectionInsuranceStatusConflictUseCase
import com.tamin.taminhamrah.useCases.objectionInsurance.ConfirmObjectionInsuranceConflictUseCase
import com.tamin.taminhamrah.useCases.objectionInsurance.FinalConfirmObjectionInsuranceConflictUseCase
import com.tamin.taminhamrah.useCases.objectionInsurance.GetObjectionInsuranceHistoriesUseCase
import com.tamin.taminhamrah.useCases.objectionInsurance.SaveObjectionInsuranceConflictUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ObjectionInsuranceViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeObjectionInsuranceRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeObjectionInsuranceRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun createViewModel(): ObjectionInsuranceViewModel = ObjectionInsuranceViewModel(
        checkStatusConflictUseCase = CheckObjectionInsuranceStatusConflictUseCase(repository),
        getObjectionInsuranceHistoriesUseCase = GetObjectionInsuranceHistoriesUseCase(repository),
        saveObjectionInsuranceConflictUseCase = SaveObjectionInsuranceConflictUseCase(repository),
        confirmObjectionInsuranceConflictUseCase = ConfirmObjectionInsuranceConflictUseCase(repository),
        finalConfirmObjectionInsuranceConflictUseCase = FinalConfirmObjectionInsuranceConflictUseCase(repository),
    )

    @Test
    fun load_withActiveRequest_showsGateDialogAndSkipsRecords() = runTest(testDispatcher) {
        repository.hasActiveRequest = true
        val viewModel = createViewModel()

        viewModel.uiState.test {
            viewModel.sendIntent(ObjectionInsuranceIntent.Load)
            val state = awaitUntil { !it.isLoading }
            assertTrue(state.hasActiveRequest)
            assertTrue(state.showActiveRequestDialog)
            assertTrue(state.records.isEmpty())
        }
    }

    @Test
    fun load_withoutActiveRequest_groupsRecordsIntoYearCards() = runTest(testDispatcher) {
        repository.histories = listOf(
            ObjectionInsuranceHistoryDN(year = "1403", workshopName = "کارگاه الف"),
            ObjectionInsuranceHistoryDN(year = "1403", workshopName = "کارگاه ب"),
            ObjectionInsuranceHistoryDN(year = "1404", workshopName = "کارگاه ج"),
        )
        val viewModel = createViewModel()

        viewModel.uiState.test {
            viewModel.sendIntent(ObjectionInsuranceIntent.Load)
            val state = awaitUntil { !it.isLoading }
            assertEquals(3, state.records.size)
            assertEquals(2, state.yearCards.size)

            val card1403 = state.yearCards.first { card ->
                state.records[card.recordIndices.first()].year == "1403"
            }
            assertEquals(2, card1403.recordIndices.size)
        }
    }

    @Test
    fun submitClicked_withoutStagedEdits_showsError() = runTest(testDispatcher) {
        repository.histories = listOf(ObjectionInsuranceHistoryDN(year = "1403"))
        val viewModel = createViewModel()

        viewModel.uiState.test {
            viewModel.sendIntent(ObjectionInsuranceIntent.Load)
            awaitUntil { !it.isLoading }

            viewModel.sendIntent(ObjectionInsuranceIntent.OnSubmitClicked)
            val state = awaitUntil { it.error != null }
            assertNotNull(state.error)
            assertTrue(!state.showSubmitConfirmationDialog)
        }
    }

    @Test
    fun stageEditThenSubmitConfirmed_success_showsTrackingNumber() = runTest(testDispatcher) {
        repository.histories = listOf(ObjectionInsuranceHistoryDN(year = "1403", oldMonth1 = "20"))
        repository.confirmResult = true
        repository.finalConfirmResult = "987654"
        val viewModel = createViewModel()

        viewModel.uiState.test {
            viewModel.sendIntent(ObjectionInsuranceIntent.Load)
            awaitUntil { !it.isLoading }

            viewModel.sendIntent(ObjectionInsuranceIntent.OnYearCardClicked(persistentListOf(0)))
            awaitUntil { it.detailRecordIndex == 0 }

            viewModel.sendIntent(ObjectionInsuranceIntent.OnMonthValueChanged(month = 0, value = "25"))
            awaitUntil { it.detailDraft[0] == "25" }

            viewModel.sendIntent(ObjectionInsuranceIntent.OnDetailConfirmClicked)
            awaitUntil { it.detailRecordIndex == null }

            viewModel.sendIntent(ObjectionInsuranceIntent.OnSubmitClicked)
            awaitUntil { it.showSubmitConfirmationDialog }

            viewModel.sendIntent(ObjectionInsuranceIntent.OnSubmitConfirmed)
            val state = awaitUntil { it.trackingNumber != null }
            assertEquals("987654", state.trackingNumber)
            assertEquals("25", repository.lastSavedItems?.single()?.newMonth1)
        }
    }

    private suspend fun ReceiveTurbine<ObjectionInsuranceUiState>.awaitUntil(
        predicate: (ObjectionInsuranceUiState) -> Boolean,
    ): ObjectionInsuranceUiState {
        var state = awaitItem()
        while (!predicate(state)) {
            state = awaitItem()
        }
        return state
    }
}
