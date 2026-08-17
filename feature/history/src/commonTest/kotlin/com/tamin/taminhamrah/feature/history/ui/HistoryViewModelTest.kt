package com.tamin.taminhamrah.feature.history.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.history.fake.FakeHistoryRepository
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryEvent
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryIntent
import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoItemDN
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeHistoryRepository
    private lateinit var viewModel: HistoryViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeHistoryRepository()
        viewModel = HistoryViewModel(
            getTalfighInfosUseCase = GetTalfighInfosUseCase(repository),
            getDastmozdInfosUseCase = GetDastmozdInfosUseCase(repository),
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun load_mergesTheYearsAndGroupsTheWorkshopsUnderThem() = runTest(testDispatcher) {
        repository.talfighResult = talfigh(year("1400", days = "10"), year("1400", days = "5"))
        repository.dastmozdResult = dastmozd("1400", "1400", "1399")

        viewModel.sendIntent(HistoryIntent.Load)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.years.size, "one card per year")
        assertEquals(180, state.years.first().totalDays)
        assertEquals(2, state.wageByYear["1400"]?.size, "both 1400 workshops")
        assertTrue(state.hasLoadedOnce)
        assertNull(state.error)
    }

    /** The two calls do not depend on each other, so the load takes the slower one, not the sum. */
    @Test
    fun load_runsBothCallsAtOnce() = runTest(testDispatcher) {
        repository.talfighDelayMs = 1_000
        repository.dastmozdDelayMs = 1_000

        val start = currentTime
        viewModel.sendIntent(HistoryIntent.Load)
        advanceUntilIdle()

        assertTrue(
            currentTime - start < 2_000,
            "sequential calls would take 2000ms; took ${currentTime - start}ms",
        )
    }

    @Test
    fun load_whenTheYearsFail_reportsTheErrorAndStopsLoading() = runTest(testDispatcher) {
        repository.talfighError = IllegalStateException("boom")

        viewModel.sendIntent(HistoryIntent.Load)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.error, "the page has nothing to show, so it must say so")
        assertTrue(state.years.isEmpty())
        assertEquals(false, state.isLoading)
    }

    /** A wage failure costs the sheets their workshops; it must not cost the page its years. */
    @Test
    fun load_whenOnlyTheWagesFail_keepsTheYearsAndWarns() = runTest(testDispatcher) {
        repository.talfighResult = talfigh(year("1402", days = "30"))
        repository.dastmozdError = IllegalStateException("wages down")

        viewModel.events.test {
            viewModel.sendIntent(HistoryIntent.Load)
            advanceUntilIdle()

            assertTrue(awaitItem() is HistoryEvent.ShowToast, "the failure is announced")
        }

        val state = viewModel.uiState.value
        assertEquals(1, state.years.size, "the years survive a wage failure")
        assertTrue(state.wageByYear.isEmpty())
        assertNull(state.error, "a wage failure is not a page failure")
    }

    @Test
    fun load_whenNothingComesBack_isEmptyRatherThanFailed() = runTest(testDispatcher) {
        viewModel.sendIntent(HistoryIntent.Load)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.years.isEmpty())
        assertTrue(state.hasLoadedOnce, "the empty state may only be trusted after a load returned")
        assertNull(state.error)
    }

    /**
     * `BaseViewModel` merges intents, so an impatient retry would otherwise start a second load
     * over the first and let whichever finished last write the screen.
     */
    @Test
    fun load_whileAlreadyLoading_isIgnored() = runTest(testDispatcher) {
        repository.talfighDelayMs = 1_000
        repository.talfighResult = talfigh(year("1402", days = "30"))

        viewModel.sendIntent(HistoryIntent.Load)
        viewModel.sendIntent(HistoryIntent.Load)
        viewModel.sendIntent(HistoryIntent.Load)
        advanceUntilIdle()

        // Asserted on the endpoint, not on the result: three loads would produce the same one year,
        // so only the call count can tell a guarded retry from three racing ones.
        assertEquals(1, repository.talfighCalls, "the second and third taps must not re-request")
        assertEquals(1, viewModel.uiState.value.years.size)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun selectingAYearCarriesThatYearAndDismissingClearsIt() = runTest(testDispatcher) {
        repository.talfighResult = talfigh(year("1402", days = "30"), year("1401", days = "20"))
        viewModel.sendIntent(HistoryIntent.Load)
        advanceUntilIdle()

        val second = viewModel.uiState.value.years[1]
        viewModel.sendIntent(HistoryIntent.SelectYear(second))
        advanceUntilIdle()
        assertEquals("1401", viewModel.uiState.value.selectedYear?.year)

        viewModel.sendIntent(HistoryIntent.DismissYearDetail)
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.selectedYear)
    }

    private fun talfigh(vararg rows: TalfighInfoItemDN) =
        TalfighInfoDN(list = rows.toList(), total = rows.size)

    private fun year(year: String, days: String) = TalfighInfoItemDN(
        months = List(12) { days },
        risuid = "1",
        historyYears = 1,
        historyMonths = 0,
        sumYear = 0,
        historyDays = 0,
        sumHistoryYears = 0,
        id = 0,
        hisYear = year,
    )

    private fun dastmozd(vararg years: String) = DastmozdInfoDN(
        list = years.mapIndexed { index, year ->
            DastmozdInfoItemDN(
                wageDetails = emptyList(),
                hisyear = year,
                id = index,
                risufname = "",
                risubirthdate = "",
                risuidserial2 = "",
                risuidserial1 = "",
                rwshname = "کارگاه $index",
                expcitycode = "",
                brhcode = "",
                risuidno = "",
                risudname = "",
                risuid = "",
                risulname = "",
                risunatcode = "",
                brhname = "",
                historytypedesc = "",
                rwshid = "$index",
            )
        },
        total = years.size,
    )
}
