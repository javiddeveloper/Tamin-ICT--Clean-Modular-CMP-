package com.tamin.taminhamrah.feature.history.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.history.fake.FakeHistoryRepository
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryEvent
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryIntent
import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoItemDN
import com.tamin.taminhamrah.model.history.UserRoleDN
import com.tamin.taminhamrah.useCases.history.DownloadHistoryReportUseCase
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetUserRoleUseCase
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
            getUserRoleUseCase = GetUserRoleUseCase(repository),
            downloadHistoryReportUseCase = DownloadHistoryReportUseCase(repository),
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
        assertEquals(false, state.wagesUnavailable, "the wages arrived")
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
        assertTrue(state.wagesUnavailable, "the sheet must not read the blank as 'none recorded'")
        assertNull(state.error, "a wage failure is not a page failure")
    }

    /**
     * A failed page must not also complain about the workshops.
     *
     * `safeCall` converts anything it does not recognize — a cancelled sibling call included — into
     * an ordinary failure, so the ordering inside the load is what keeps the two apart: the years
     * are awaited first, and a page that never got them never reaches the wage warning.
     */
    @Test
    fun load_whenTheYearsFail_doesNotAlsoWarnAboutTheWages() = runTest(testDispatcher) {
        repository.talfighError = IllegalStateException("boom")
        repository.dastmozdError = IllegalStateException("cancelled with it")

        viewModel.events.test {
            viewModel.sendIntent(HistoryIntent.Load)
            advanceUntilIdle()

            expectNoEvents()
        }

        assertNotNull(viewModel.uiState.value.error, "the page failure is still reported")
    }

    /**
     * The whole point of the gate: a مستمری‌بگیر or کارفرما never reaches the two endpoints.
     *
     * They have no insured years, and the service answers 500 rather than an empty list, so the
     * previous app decided this from the user's own record before asking. Asserted on the call
     * counts, because "did not ask" is the behaviour — a message alone would still have asked.
     */
    @Test
    fun load_whenThePersonCannotHaveHistory_refusesWithoutCallingTheEndpoints() =
        runTest(testDispatcher) {
            repository.userRoleResult = UserRoleDN.PENSIONER

            viewModel.sendIntent(HistoryIntent.Load)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state.accessDenied, "the screen must say why, not show a server error")
            assertEquals(0, repository.talfighCalls, "the years must not be requested")
            assertEquals(0, repository.dastmozdCalls, "the wages must not be requested either")
            assertNull(state.error, "a refusal is not a failure")
            assertEquals(false, state.isLoading)
        }

    @Test
    fun load_whenThePersonIsInsured_goesOnToLoad() = runTest(testDispatcher) {
        repository.talfighResult = talfigh(year("1404", days = "30"))

        viewModel.sendIntent(HistoryIntent.Load)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(false, state.accessDenied)
        assertEquals(1, repository.talfighCalls)
        assertEquals(1, state.years.size)
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

    /**
     * A gate exists to explain a service, not to guard it. If the role call is the thing that is
     * down, refusing would keep an insured person off a page they can use — so the load carries on
     * and the history endpoints answer for themselves.
     */
    @Test
    fun load_whenTheRoleCheckFails_carriesOnRatherThanRefusing() = runTest(testDispatcher) {
        repository.userRoleError = IllegalStateException("logininfo down")
        repository.talfighResult = talfigh(year("1403", days = "30"))

        viewModel.sendIntent(HistoryIntent.Load)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(false, state.accessDenied, "an outage on the gate is not a refusal")
        assertEquals(1, repository.talfighCalls, "the years are still requested")
        assertEquals(1, state.years.size)
    }

    @Test
    fun selectingAReportOpensTheViewerForThatReportAndDismissingClosesIt() =
        runTest(testDispatcher) {
            viewModel.sendIntent(HistoryIntent.ShowReportMenu)
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.showReportMenu)

            viewModel.sendIntent(HistoryIntent.SelectReport(HistoryCertificateType.WAGES))
            advanceUntilIdle()

            val opened = viewModel.uiState.value
            assertEquals(HistoryCertificateType.WAGES, opened.selectedReport)
            assertEquals(false, opened.showReportMenu, "choosing closes the menu")
            assertNull(opened.reportPdf, "the previous report's bytes must not be reused")

            viewModel.sendIntent(HistoryIntent.DismissReport)
            advanceUntilIdle()
            assertNull(viewModel.uiState.value.selectedReport)
        }

    /** The viewer asks for the bytes, so the report it asks for must be the one it is showing. */
    @Test
    fun downloadingAReportFetchesTheOneOnScreen() = runTest(testDispatcher) {
        viewModel.sendIntent(HistoryIntent.SelectReport(HistoryCertificateType.COMBINED))
        advanceUntilIdle()

        viewModel.sendIntent(HistoryIntent.DownloadReport)
        advanceUntilIdle()

        assertEquals(HistoryCertificateType.COMBINED, repository.lastReportRequested)
        assertNotNull(viewModel.uiState.value.reportPdf)
        assertEquals(false, viewModel.uiState.value.reportDownloadFailed)
    }

    /**
     * A failed download is the viewer's own business: it shows "file unavailable" itself, and an
     * error dialog over an open viewer would bury the thing it is talking about.
     */
    @Test
    fun aFailedReportDownloadTellsTheViewerRatherThanThePage() = runTest(testDispatcher) {
        repository.reportError = IllegalStateException("report down")
        viewModel.sendIntent(HistoryIntent.SelectReport(HistoryCertificateType.ALL))
        advanceUntilIdle()

        viewModel.sendIntent(HistoryIntent.DownloadReport)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.reportDownloadFailed, "the viewer must stop waiting")
        assertNull(state.error, "the page did not fail")
    }

    @Test
    fun downloadingWithNoReportOpenAsksForNothing() = runTest(testDispatcher) {
        viewModel.sendIntent(HistoryIntent.DownloadReport)
        advanceUntilIdle()

        assertNull(repository.lastReportRequested)
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
