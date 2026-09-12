package com.tamin.taminhamrah.feature.history.ui

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryIntent
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryUiState
import com.tamin.taminhamrah.feature.history.ui.model.CareerTotalPR
import com.tamin.taminhamrah.feature.history.ui.model.HistoryScope
import com.tamin.taminhamrah.feature.history.ui.model.YearHistoryPR
import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR
import com.tamin.taminhamrah.model.history.WageDetailPR
import com.tamin.taminhamrah.ui.digitsOnly
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList

/**
 * «کلیه سوابق» as it looks with a real career behind it, without a real account.
 *
 * The page needs an insured person with several years, two employers in one of them and a scheme
 * row that carries no workshop name — the shape that has caused every visual defect found so far.
 * A test account rarely has all three, and the one this was built against returns nothing at all
 * from `talfighinfos`, so the chart, the sheet and the dialogs could only be seen by hand-editing
 * the screen. This exists so that stops being necessary.
 *
 * Shaped from a real production response, so what it renders is what the service actually sends.
 */
object HistoryFixtures {

    /** A full year, a part year, and a year with a gap in the middle. */
    val years = persistentListOf(
        YearHistoryPR(
            year = "1402",
            monthDays = persistentListOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29),
            totalDays = 365,
        ),
        YearHistoryPR(
            year = "1403",
            monthDays = persistentListOf(31, 31, 31, 0, 0, 0, 30, 30, 30, 30, 30, 29),
            totalDays = 272,
        ),
        YearHistoryPR(
            year = "1404",
            monthDays = persistentListOf(0, 0, 0, 4, 31, 31, 30, 30, 30, 30, 30, 29),
            totalDays = 245,
        ),
    )

    /**
     * Two employers in ۱۴۰۴, overlapping in فروردین and اردیبهشت so the concurrency cap and its
     * legend both appear, and the second carrying no `rwshname` — the scheme row whose name has to
     * come from its history type instead.
     */
    val wageRows = persistentListOf(
        wageRow(
            id = 1,
            year = "1404",
            name = "شركت صنايع دما بخار مشهد",
            type = "كاركرد عادي ليست",
            branch = "هفت مشهد، توس",
            workshopId = "6393610019",
            days = listOf(0, 0, 0, 4, 31, 31, 30, 30, 30, 30, 30, 29),
            wage = "135082560",
        ),
        wageRow(
            id = 2,
            year = "1404",
            name = "",
            type = "بيمه هاي اجتماعي کارگران ساختماني مصوب 1386",
            branch = "چناران",
            workshopId = "",
            days = listOf(31, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            wage = "64000000",
        ),
    )

    /** The page as a load would leave it. */
    val state = HistoryUiState(
        hasLoadedOnce = true,
        years = years,
        careerTotal = CareerTotalPR(years = 12, months = 4, days = 18, totalDays = 4518),
        wageByYear = persistentMapOf("1404" to wageRows),
        nationalId = "0946168113",
    )

    private fun wageRow(
        id: Int,
        year: String,
        name: String,
        type: String,
        branch: String,
        workshopId: String,
        days: List<Int>,
        wage: String,
    ) = DastmozdInfoItemPR(
        wageDetails = days.map {
            WageDetailPR(month = it.toString(), wage = if (it > 0) wage else "0")
        }.toImmutableList(),
        hisyear = year,
        id = id,
        risufname = "",
        risubirthdate = "",
        risuidserial2 = "",
        risuidserial1 = "",
        rwshname = name,
        expcitycode = "",
        brhcode = "",
        risuidno = "",
        risudname = "",
        risuid = "",
        risulname = "",
        risunatcode = "",
        brhname = branch,
        historytypedesc = type,
        rwshid = workshopId,
    )
}

/**
 * The page driven entirely by [HistoryFixtures], with no ViewModel and no network.
 *
 * Every interaction the screen offers works — switching scope, opening a month, filtering by
 * employer, the year sheet, the download menu and both dialogs — because the state is reduced right
 * here. That makes the whole surface reachable on a device or in a preview without an account that
 * happens to have the right history.
 *
 * It is not wired into navigation: nothing reaches it unless something calls it, so it cannot be
 * opened by accident in a release build. To use it on a device, call it in place of
 * [HistoryScreen] from the nav graph behind whatever debug check the app already has.
 */
@Composable
fun HistoryFixtureScreen(onBackClicked: () -> Unit = {}) {
    var state by remember { mutableStateOf(HistoryFixtures.state) }

    HistoryContent(
        uiState = state,
        lazyListState = rememberLazyListState(),
        onIntent = { intent -> state = state.reduceForFixture(intent) },
        onBackClicked = onBackClicked,
    )
}

/**
 * The ViewModel's reductions, minus everything that talks to a service.
 *
 * Deliberately a copy rather than a call into [HistoryViewModel]: the point is to run the screen
 * with no repository at all, and a fixture that needed the real reducer would need the real
 * dependencies with it.
 */
private fun HistoryUiState.reduceForFixture(intent: HistoryIntent): HistoryUiState = when (intent) {
    is HistoryIntent.SelectScope ->
        copy(scope = intent.scope, selectedMonth = null, selectedSource = null)

    is HistoryIntent.SelectMonth ->
        copy(selectedMonth = intent.month.takeIf { it != selectedMonth })

    is HistoryIntent.SelectSource -> copy(selectedSource = intent.source)

    is HistoryIntent.SelectMetric -> copy(metric = intent.metric)

    // Same rule as the ViewModel: splitting already shows every employer, so a filter down to one
    // of them is the same question asked twice, and is cleared rather than left to contradict the
    // bars.
    HistoryIntent.ToggleSplit -> copy(
        splitBySource = !splitBySource,
        selectedSource = if (!splitBySource) null else selectedSource,
    )


    // «انتخاب سال و ماه», reduced the same way the ViewModel does so the fixture exercises the real
    // staging rules — including that a new year drops the month staged under the last one.
    HistoryIntent.OpenYearPicker -> copy(
        yearPickerOpen = true,
        yearQuery = "",
        pickerYear = (scope as? HistoryScope.Year)?.year,
        pickerMonth = (scope as? HistoryScope.Year)?.let { selectedMonth },
    )

    HistoryIntent.DismissYearPicker -> copy(yearPickerOpen = false)

    is HistoryIntent.YearQueryChanged -> copy(yearQuery = intent.query.digitsOnly())

    is HistoryIntent.PickerYearSelected -> copy(pickerYear = intent.year, pickerMonth = null)

    is HistoryIntent.PickerMonthSelected -> copy(pickerMonth = intent.month)

    HistoryIntent.ApplyYearPicker -> pickerYear?.let { year ->
        copy(
            scope = HistoryScope.Year(year),
            selectedMonth = pickerMonth,
            selectedSource = null,
            yearPickerOpen = false,
        )
    } ?: this


    is HistoryIntent.AskSendNotice -> copy(showSendConfirm = true)

    is HistoryIntent.DismissSendConfirm -> copy(showSendConfirm = false)

    is HistoryIntent.ConfirmSendNotice -> copy(
        showSendConfirm = false,
        showSendSuccess = true,
        sendSuccessMessage = "سوابق شما با موفقیت برای موسسات ارسال شد.",
    )

    is HistoryIntent.DismissSendSuccess -> copy(showSendSuccess = false, sendSuccessMessage = null)

    is HistoryIntent.ShowReportMenu -> copy(showReportMenu = true)

    is HistoryIntent.DismissReportMenu -> copy(showReportMenu = false)

    is HistoryIntent.SelectReport -> copy(showReportMenu = false, selectedReport = intent.type)

    // No bytes behind a fixture, so the viewer shows its "nothing came back" state rather than
    // pretending a PDF arrived — faking one would prove nothing about the real download.
    is HistoryIntent.DownloadReport -> copy(reportDownloadFailed = true)

    is HistoryIntent.DismissReport -> copy(selectedReport = null, reportDownloadFailed = false)

    is HistoryIntent.Load -> this
}
