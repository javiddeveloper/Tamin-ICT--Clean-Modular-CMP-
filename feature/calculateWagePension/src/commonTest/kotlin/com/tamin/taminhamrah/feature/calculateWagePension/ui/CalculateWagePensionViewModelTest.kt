package com.tamin.taminhamrah.feature.calculateWagePension.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.calculateWagePension.fake.FakeCalculateWagePensionRepository
import com.tamin.taminhamrah.feature.calculateWagePension.fake.FakeHistoryRepository
import com.tamin.taminhamrah.feature.calculateWagePension.fake.sampleDastmozd
import com.tamin.taminhamrah.feature.calculateWagePension.fake.sampleTalfigh
import com.tamin.taminhamrah.feature.calculateWagePension.ui.contract.CalculateWagePensionEvent
import com.tamin.taminhamrah.feature.calculateWagePension.ui.contract.CalculateWagePensionIntent
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopResultDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.useCases.calculateWagePension.CalculateMultipleWorkshopsPensionUseCase
import com.tamin.taminhamrah.useCases.calculateWagePension.CalculateWagePensionUseCase
import com.tamin.taminhamrah.useCases.calculateWagePension.CheckMultipleWorkshopsUseCase
import com.tamin.taminhamrah.useCases.calculateWagePension.GetMultipleWorkshopPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CalculateWagePensionViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var historyRepository: FakeHistoryRepository
    private lateinit var pensionRepository: FakeCalculateWagePensionRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        historyRepository = FakeHistoryRepository()
        pensionRepository = FakeCalculateWagePensionRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun createViewModel(): CalculateWagePensionViewModel =
        CalculateWagePensionViewModel(
            getTalfighInfosUseCase = GetTalfighInfosUseCase(historyRepository),
            getDastmozdInfosUseCase = GetDastmozdInfosUseCase(historyRepository),
            calculateWagePensionUseCase = CalculateWagePensionUseCase(),
            getMultipleWorkshopPersonalInfoUseCase = GetMultipleWorkshopPersonalInfoUseCase(pensionRepository),
            checkMultipleWorkshopsUseCase = CheckMultipleWorkshopsUseCase(pensionRepository),
            calculateMultipleWorkshopsPensionUseCase = CalculateMultipleWorkshopsPensionUseCase(pensionRepository),
        )

    @Test
    fun load_onInit_populatesCalculationAndDisplayedAmount() = runTest(testDispatcher) {
        historyRepository.talfighResult = sampleTalfigh()
        historyRepository.dastmozdResult = sampleDastmozd()

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertNotNull(state.calculation)
        assertEquals(state.calculation!!.eligibleAmountPension, state.displayedEligibleAmount)
        assertEquals(1, historyRepository.talfighCalls)
        assertEquals(1, historyRepository.dastmozdCalls)
    }

    @Test
    fun load_whenTalfighEmpty_skipsDastmozdAndShowsNothing() = runTest(testDispatcher) {
        historyRepository.talfighResult = TalfighInfoDN(list = emptyList(), total = 0)
        historyRepository.dastmozdResult = sampleDastmozd()

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertNull(state.calculation)
        assertEquals(1, historyRepository.talfighCalls)
        assertEquals(0, historyRepository.dastmozdCalls)
    }

    @Test
    fun load_whenTalfighFails_reportsError() = runTest(testDispatcher) {
        historyRepository.talfighError = IllegalStateException("talfigh down")

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertNull(state.calculation)
        assertEquals(0, historyRepository.dastmozdCalls)
    }

    @Test
    fun retry_afterError_reloadsSuccessfully() = runTest(testDispatcher) {
        historyRepository.talfighError = IllegalStateException("boom")
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.error)

        historyRepository.talfighError = null
        historyRepository.talfighResult = sampleTalfigh()
        historyRepository.dastmozdResult = sampleDastmozd()

        viewModel.sendIntent(CalculateWagePensionIntent.Retry)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
        assertNotNull(viewModel.uiState.value.calculation)
    }

    @Test
    fun multipleWorkshops_whenNotEligible_showsInfoDialogAndKeepsBaseAmount() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()
        val baseAmount = viewModel.uiState.value.displayedEligibleAmount
        pensionRepository.isMultipleResult = MultipleWorkshopResultDN(result = 0)

        viewModel.sendIntent(CalculateWagePensionIntent.MultipleWorkshopsToggled(true))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.showMultipleWorkshopsInfoDialog)
        assertTrue(state.isMultipleWorkshopsEnabled)
        assertFalse(state.isMultipleWorkshopLoading)
        assertEquals(baseAmount, state.displayedEligibleAmount)
        assertEquals(0, pensionRepository.calculateCalls)
    }

    @Test
    fun multipleWorkshops_whenEligible_replacesDisplayedAmount() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()
        pensionRepository.calculateResult = MultipleWorkshopResultDN(result = 25_000_000)

        viewModel.sendIntent(CalculateWagePensionIntent.MultipleWorkshopsToggled(true))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isMultipleWorkshopsEnabled)
        assertFalse(state.isMultipleWorkshopLoading)
        assertFalse(state.showMultipleWorkshopsInfoDialog)
        assertEquals(25_000_000L, state.displayedEligibleAmount)
        assertEquals(1, pensionRepository.calculateCalls)
    }

    @Test
    fun multipleWorkshops_whenMidFlowFails_disablesToggleAndRestoresBaseAmount() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()
        val baseAmount = viewModel.uiState.value.displayedEligibleAmount
        pensionRepository.calculateError = IllegalStateException("calculate failed")

        viewModel.events.test {
            viewModel.sendIntent(CalculateWagePensionIntent.MultipleWorkshopsToggled(true))
            advanceUntilIdle()

            assertTrue(awaitItem() is CalculateWagePensionEvent.ShowToast)
            cancelAndIgnoreRemainingEvents()
        }

        val state = viewModel.uiState.value
        assertFalse(state.isMultipleWorkshopsEnabled)
        assertFalse(state.isMultipleWorkshopLoading)
        assertEquals(baseAmount, state.displayedEligibleAmount)
    }

    @Test
    fun multipleWorkshops_toggleOff_restoresBaseAmount() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()
        val baseAmount = viewModel.uiState.value.displayedEligibleAmount
        pensionRepository.calculateResult = MultipleWorkshopResultDN(result = 25_000_000)

        viewModel.sendIntent(CalculateWagePensionIntent.MultipleWorkshopsToggled(true))
        advanceUntilIdle()
        assertEquals(25_000_000L, viewModel.uiState.value.displayedEligibleAmount)

        viewModel.sendIntent(CalculateWagePensionIntent.MultipleWorkshopsToggled(false))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isMultipleWorkshopsEnabled)
        assertEquals(baseAmount, state.displayedEligibleAmount)
    }

    @Test
    fun dismissMultipleWorkshopsInfo_disablesToggleAndRestoresBaseAmount() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()
        val baseAmount = viewModel.uiState.value.displayedEligibleAmount
        pensionRepository.isMultipleResult = MultipleWorkshopResultDN(result = 0)

        viewModel.sendIntent(CalculateWagePensionIntent.MultipleWorkshopsToggled(true))
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.showMultipleWorkshopsInfoDialog)

        viewModel.sendIntent(CalculateWagePensionIntent.DismissMultipleWorkshopsInfo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.showMultipleWorkshopsInfoDialog)
        assertFalse(state.isMultipleWorkshopsEnabled)
        assertEquals(baseAmount, state.displayedEligibleAmount)
    }
}
