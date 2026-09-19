package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.intro

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.fake.FakeIllDaysRepository
import com.tamin.taminhamrah.useCases.requestPaymentForIllDays.GetIllDaysInsuredMainInfoUseCase
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
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class IllDaysIntroViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeIllDaysRepository
    private lateinit var viewModel: IllDaysIntroViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeIllDaysRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun buildViewModel(): IllDaysIntroViewModel = IllDaysIntroViewModel(
        getIllDaysInsuredMainInfoUseCase = GetIllDaysInsuredMainInfoUseCase(repository),
    )

    @Test
    fun load_success_populatesInsuredInfo() = runTest(testDispatcher) {
        viewModel = buildViewModel()

        viewModel.uiState.test {
            val state = awaitUntil { !it.isLoading }
            assertNotNull(state.insuredInfo)
            assertEquals("Ali", state.insuredInfo?.firstName)
            assertNull(state.errorMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun startRequest_withoutLoadedInfo_showsToast() = runTest(testDispatcher) {
        repository.insuredInfo = null
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(IllDaysIntroIntent.StartRequest)
            assertIs<IllDaysIntroEvent.ShowToast>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun startRequest_withLoadedInfo_navigatesToWizard() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(IllDaysIntroIntent.StartRequest)
            assertIs<IllDaysIntroEvent.NavigateToWizard>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private suspend fun ReceiveTurbine<IllDaysIntroUiState>.awaitUntil(
        predicate: (IllDaysIntroUiState) -> Boolean,
    ): IllDaysIntroUiState {
        var state = awaitItem()
        while (!predicate(state)) {
            state = awaitItem()
        }
        return state
    }
}
