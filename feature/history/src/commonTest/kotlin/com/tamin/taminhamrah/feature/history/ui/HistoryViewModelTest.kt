package com.tamin.taminhamrah.feature.history.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.history.fake.FakeHistoryRepository
import com.tamin.taminhamrah.feature.history.fake.employerUser
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryEvent
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryIntent
import com.tamin.taminhamrah.feature.history.ui.model.HistoryScope
import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoItemDN
import com.tamin.taminhamrah.model.history.UserRoleDN
import com.tamin.taminhamrah.model.history.WageDetailDN
import com.tamin.taminhamrah.useCases.history.DownloadHistoryReportUseCase
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetUserInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetUserRoleUseCase
import com.tamin.taminhamrah.useCases.history.SendHistoryNoticeUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

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
            getUserInfosUseCase = GetUserInfosUseCase(repository),
            sendHistoryNoticeUseCase = SendHistoryNoticeUseCase(repository),
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
     * The data source's catch-all turns anything it does not recognize — a sibling call failing
     * alongside included — into an ordinary failure, so the ordering inside the load is what keeps
     * the two apart: the years are awaited first, and a page that never got them never reaches the
     * wage warning.
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
     * counts, because "did not ask" is the behavior — a message alone would still have asked.
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

    /**
     * The role service has no code for an employer, so the role alone cannot recognize one. Without
     * the insurance number this person reached `talfighinfos`, was rejected, and read the rejection
     * as «خطای اتصال» — an internet problem they did not have.
     */
    @Test
    fun load_whenThePersonIsAnEmployer_refusesWithoutCallingTheEndpoints() =
        runTest(testDispatcher) {
            repository.userInfoResult = employerUser()

            viewModel.sendIntent(HistoryIntent.Load)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state.accessDenied, "an employer has no insured years to show")
            assertEquals(0, repository.talfighCalls, "the years must not be requested")
            assertEquals(0, repository.dastmozdCalls)
            assertNull(state.error, "a refusal is not a failure")
        }

    /** An outage on either lookup must not read as «you are not insured». */
    @Test
    fun load_whenTheIdentityLookupFails_carriesOnRatherThanRefusing() = runTest(testDispatcher) {
        repository.userInfoError = IllegalStateException("userinfos down")
        repository.talfighResult = talfigh(year("1403", days = "30"))

        viewModel.sendIntent(HistoryIntent.Load)
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.accessDenied)
        assertEquals(1, repository.talfighCalls)
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

    /**
     * Production case: `talfighinfos` answered `{"total":0,"list":[]}` while `dastmozdinfos`
     * returned five years for the same person. The page held the whole history and showed «سابقه‌ای
     * یافت نشد», so the years now fold from the wage rows when the merged service gives nothing.
     */
    @Test
    fun load_whenTheMergedServiceIsEmpty_foldsTheYearsFromTheWageRows() = runTest(testDispatcher) {
        repository.talfighResult = TalfighInfoDN(list = emptyList(), total = 0)
        repository.dastmozdResult = wagesWithDays("1404" to 30, "1403" to 10)

        viewModel.sendIntent(HistoryIntent.Load)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(listOf("1404", "1403"), state.years.map { it.year })
        assertEquals(360, state.years.first().totalDays, "twelve months of thirty days")
        assertEquals(480, state.careerTotal.totalDays, "counted from the days themselves")
        assertNull(state.error, "an empty merged service is not a failure")
    }

    /** The fallback is a fallback: when the merged service answers, it decides the years. */
    @Test
    fun load_whenTheMergedServiceAnswers_ignoresTheWageRowsForTheYears() = runTest(testDispatcher) {
        repository.talfighResult = talfigh(year("1402", days = "30"))
        repository.dastmozdResult = wagesWithDays("1399" to 30)

        viewModel.sendIntent(HistoryIntent.Load)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(listOf("1402"), state.years.map { it.year }, "the merged year, not the wage one")
    }

    /** Neither service has anything: still an empty page, not a fabricated one. */
    @Test
    fun load_whenNeitherServiceHasRows_staysEmpty() = runTest(testDispatcher) {
        viewModel.sendIntent(HistoryIntent.Load)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.years.isEmpty())
        assertTrue(viewModel.uiState.value.hasLoadedOnce)
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

    /** «ارسال سابقه» posts on the person's behalf, so it must go through the confirmation. */
    @Test
    fun sendingTheNotice_onlyHappensAfterConfirming() = runTest(testDispatcher) {
        viewModel.sendIntent(HistoryIntent.AskSendNotice)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.showSendConfirm)
        assertEquals(0, repository.noticeCalls, "asking is not sending")

        viewModel.sendIntent(HistoryIntent.ConfirmSendNotice)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, repository.noticeCalls)
        assertEquals(false, state.showSendConfirm, "the confirmation closes behind the send")
        assertTrue(state.showSendSuccess, "the confirmation dialog opens")
        assertEquals("ارسال شد", state.sendSuccessMessage, "the server's own wording is shown")
    }

    /**
     * A service that confirms without wording of its own must still read as a confirmation.
     *
     * The fallback sentence lives in `strings.xml` and is resolved by the screen, so what is
     * pinned here is that the dialog opens with no message of its own to show — the case that
     * used to be a Persian literal in this ViewModel's companion object.
     */
    @Test
    fun sendingTheNotice_whenTheServiceSendsNoWording_stillConfirms() = runTest(testDispatcher) {
        repository.noticeResult = null

        viewModel.sendIntent(HistoryIntent.ConfirmSendNotice)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, repository.noticeCalls)
        assertTrue(state.showSendSuccess, "a wordless confirmation is still a confirmation")
        assertNull(state.sendSuccessMessage, "and carries no wording for the screen to print")
        assertNull(state.error)
    }

    /** Dismissing takes the dialog down and clears what it was showing. */
    @Test
    fun dismissingTheSuccessDialog_closesIt() = runTest(testDispatcher) {
        viewModel.sendIntent(HistoryIntent.ConfirmSendNotice)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.showSendSuccess)

        viewModel.sendIntent(HistoryIntent.DismissSendSuccess)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(false, state.showSendSuccess)
        assertNull(state.sendSuccessMessage)
    }

    @Test
    fun sendingTheNotice_whenItFails_reportsTheErrorRatherThanSuccess() = runTest(testDispatcher) {
        repository.noticeError = IllegalStateException("rejected")

        viewModel.sendIntent(HistoryIntent.ConfirmSendNotice)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.error)
        assertEquals(false, state.showSendSuccess, "a failure must not read as a confirmation")
        assertNull(state.sendSuccessMessage)
        assertEquals(false, state.isSending)
    }

    /** A report for someone with no years is a blank PDF; the menu must refuse rather than open. */
    @Test
    fun openingTheReportMenu_withNoHistory_refusesAndSaysWhy() = runTest(testDispatcher) {
        viewModel.sendIntent(HistoryIntent.Load)
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(HistoryIntent.ShowReportMenu)
            advanceUntilIdle()

            assertTrue(awaitItem() is HistoryEvent.ShowToast)
        }
        assertEquals(false, viewModel.uiState.value.showReportMenu)
    }

    /**
     * The blank PDF a user actually received: years folded from the wage rows, so the page has
     * history, but `talfighinfos` had none — and «سوابق تلفیقی» is generated from exactly that.
     */
    @Test
    fun selectingAReport_whoseServiceHasNoRows_refusesInsteadOfDownloadingABlankFile() =
        runTest(testDispatcher) {
            repository.talfighResult = TalfighInfoDN(list = emptyList(), total = 0)
            repository.dastmozdResult = wagesWithDays("1404" to 30)
            viewModel.sendIntent(HistoryIntent.Load)
            advanceUntilIdle()

            viewModel.events.test {
                viewModel.sendIntent(HistoryIntent.SelectReport(HistoryCertificateType.COMBINED))
                advanceUntilIdle()

                assertTrue(awaitItem() is HistoryEvent.ShowToast, "it says why")
            }
            assertNull(viewModel.uiState.value.selectedReport, "no viewer, no download")
        }

    /**
     * The picker stages a choice and only [HistoryIntent.ApplyYearPicker] spends it.
     *
     * The point of the staging is that a person can land on a year, look at its months and change
     * their mind without the page moving under them — so what is asserted here is as much what does
     * *not* happen on the way as what happens at the end.
     */
    @Test
    fun theYearPickerStagesAChoiceAndOnlyApplyingItMovesThePage() = runTest(testDispatcher) {
        repository.talfighResult = talfigh(year("1402", days = "30"), year("1401", days = "20"))
        viewModel.sendIntent(HistoryIntent.Load)
        advanceUntilIdle()

        viewModel.sendIntent(HistoryIntent.OpenYearPicker)
        viewModel.sendIntent(HistoryIntent.PickerYearSelected("1401"))
        viewModel.sendIntent(HistoryIntent.PickerMonthSelected(4))
        advanceUntilIdle()

        assertEquals("1401", viewModel.uiState.value.pickerYear, "staged")
        assertEquals(4, viewModel.uiState.value.pickerMonth, "staged")
        assertEquals(
            HistoryScope.All,
            viewModel.uiState.value.scope,
            "the page has not moved while the sheet is open",
        )

        viewModel.sendIntent(HistoryIntent.ApplyYearPicker)
        advanceUntilIdle()

        assertEquals(HistoryScope.Year("1401"), viewModel.uiState.value.scope)
        assertEquals(4, viewModel.uiState.value.selectedMonth)
        assertFalse(viewModel.uiState.value.yearPickerOpen, "and the sheet is done")
    }

    /** A year staged after another drops the month staged under the first — ماه ۵ is not the same. */
    @Test
    fun stagingADifferentYearClearsTheMonthStagedUnderTheLastOne() = runTest(testDispatcher) {
        repository.talfighResult = talfigh(year("1402", days = "30"), year("1401", days = "20"))
        viewModel.sendIntent(HistoryIntent.Load)
        advanceUntilIdle()

        viewModel.sendIntent(HistoryIntent.OpenYearPicker)
        viewModel.sendIntent(HistoryIntent.PickerYearSelected("1401"))
        viewModel.sendIntent(HistoryIntent.PickerMonthSelected(4))
        advanceUntilIdle()
        assertEquals(4, viewModel.uiState.value.pickerMonth)

        viewModel.sendIntent(HistoryIntent.PickerYearSelected("1402"))
        advanceUntilIdle()

        assertEquals("1402", viewModel.uiState.value.pickerYear)
        assertNull(viewModel.uiState.value.pickerMonth, "the month did not follow the year")
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
        // A report is only offered when its own service has rows; this test is about what happens
        // after that, so both services answer.
        repository.talfighResult = talfigh(year("1402", days = "30"))
        repository.dastmozdResult = wagesWithDays("1402" to 30)
        viewModel.sendIntent(HistoryIntent.Load)
        advanceUntilIdle()

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
        // The report is only offered once its service has answered with rows.
        repository.talfighResult = talfigh(year("1402", days = "30"))
        viewModel.sendIntent(HistoryIntent.Load)
        advanceUntilIdle()

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
        // «کلیه سوابق» is offered whenever the page has years at all.
        repository.talfighResult = talfigh(year("1402", days = "30"))
        viewModel.sendIntent(HistoryIntent.Load)
        advanceUntilIdle()

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

    /** Wage rows carrying real day counts, which is what the fallback folds years out of. */
    private fun wagesWithDays(vararg yearToDays: Pair<String, Int>) = DastmozdInfoDN(
        list = yearToDays.mapIndexed { index, (year, daysPerMonth) ->
            DastmozdInfoItemDN(
                wageDetails = List(12) {
                    WageDetailDN(month = daysPerMonth.toString(), wage = "1000")
                },
                hisyear = year, id = index, risufname = "", risubirthdate = "",
                risuidserial2 = "", risuidserial1 = "", rwshname = "کارگاه", expcitycode = "",
                brhcode = "", risuidno = "", risudname = "", risuid = "", risulname = "",
                risunatcode = "", brhname = "", historytypedesc = "", rwshid = "$index",
            )
        },
        total = yearToDays.size,
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
