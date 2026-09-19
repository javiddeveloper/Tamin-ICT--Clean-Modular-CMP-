package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.calculate

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.fake.FakeIllDaysRepository
import com.tamin.taminhamrah.useCases.requestPaymentForIllDays.CalcIllnessAmountUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class IllDaysCalculateViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeIllDaysRepository
    private lateinit var viewModel: IllDaysCalculateViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeIllDaysRepository()
        viewModel = IllDaysCalculateViewModel(
            calcIllnessAmountUseCase = CalcIllnessAmountUseCase(repository),
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun calculate_endBeforeStart_showsToast() = runTest(testDispatcher) {
        viewModel.sendIntent(IllDaysCalculateIntent.StartDatePicked(millis = 2_000L, label = "start"))
        viewModel.sendIntent(IllDaysCalculateIntent.EndDatePicked(millis = 1_000L, label = "end"))
        viewModel.sendIntent(IllDaysCalculateIntent.SelectMarital(IllDaysMaritalStatus.Single))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(IllDaysCalculateIntent.Calculate)
            assertIs<IllDaysCalculateEvent.ShowToast>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertNull(viewModel.uiState.value.result)
    }

    @Test
    fun calculate_blankPayable_showsToast() = runTest(testDispatcher) {
        repository.calcResult = listOf("error message", "")
        val start = 1_700_000_000_000L
        val end = start + MILLIS_PER_DAY

        viewModel.sendIntent(IllDaysCalculateIntent.StartDatePicked(millis = start, label = "start"))
        viewModel.sendIntent(IllDaysCalculateIntent.EndDatePicked(millis = end, label = "end"))
        viewModel.sendIntent(IllDaysCalculateIntent.SelectMarital(IllDaysMaritalStatus.Single))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(IllDaysCalculateIntent.Calculate)
            assertIs<IllDaysCalculateEvent.ShowToast>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertNull(viewModel.uiState.value.result)
    }

    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
    }
}
