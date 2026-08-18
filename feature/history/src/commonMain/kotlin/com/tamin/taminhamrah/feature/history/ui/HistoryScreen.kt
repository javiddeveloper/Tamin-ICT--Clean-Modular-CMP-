package com.tamin.taminhamrah.feature.history.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.history.ui.components.ChartAxis
import com.tamin.taminhamrah.feature.history.ui.components.HistoryActionCards
import com.tamin.taminhamrah.feature.history.ui.components.HistoryChartCard
import com.tamin.taminhamrah.feature.history.ui.components.HistoryHero
import com.tamin.taminhamrah.feature.history.ui.components.HistorySpanNote
import com.tamin.taminhamrah.feature.history.ui.components.MonthWageBreakdown
import com.tamin.taminhamrah.feature.history.ui.components.NoWorkshops
import com.tamin.taminhamrah.feature.history.ui.components.ReportMenuSheet
import com.tamin.taminhamrah.feature.history.ui.components.WorkshopSummaryRow
import com.tamin.taminhamrah.feature.history.ui.components.YearDetailSheet
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryEvent
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryIntent
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryUiState
import com.tamin.taminhamrah.feature.history.ui.model.DurationLabels
import com.tamin.taminhamrah.feature.history.ui.model.HistoryScope
import com.tamin.taminhamrah.feature.history.ui.model.YearDetailPR
import com.tamin.taminhamrah.feature.history.ui.model.YearHistoryPR
import com.tamin.taminhamrah.feature.history.ui.model.careerDurationChips
import com.tamin.taminhamrah.feature.history.ui.model.detailWith
import com.tamin.taminhamrah.feature.history.ui.model.gapYearCount
import com.tamin.taminhamrah.feature.history.ui.model.monthBars
import com.tamin.taminhamrah.feature.history.ui.model.sourceChips
import com.tamin.taminhamrah.feature.history.ui.model.yearBars
import com.tamin.taminhamrah.feature.history.ui.model.yearChips
import com.tamin.taminhamrah.feature.history.ui.model.yearDurationChips
import com.tamin.taminhamrah.mapper.history.labelRes
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.ShimmerRows
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.ToasterState
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toRialAmount
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res as CoreRes
import taminx.core.core_ui.action_back
import taminx.feature.history.Res as HistoryRes
import taminx.feature.history.history_all_title
import taminx.feature.history.history_chart_hint_all
import taminx.feature.history.history_chart_hint_year
import taminx.feature.history.history_chart_title_all
import taminx.feature.history.history_chart_title_year
import taminx.feature.history.history_combined_empty
import taminx.feature.history.history_combined_empty_title
import taminx.feature.history.history_combined_not_insured
import taminx.feature.history.history_combined_stat_days
import taminx.feature.history.history_combined_stat_months
import taminx.feature.history.history_combined_stat_years
import taminx.feature.history.history_combined_year_days
import taminx.feature.history.history_concurrent_label
import taminx.feature.history.history_concurrent_note
import taminx.feature.history.history_detail_pick_month
import taminx.feature.history.history_detail_pick_year
import taminx.feature.history.history_detail_year_empty
import taminx.feature.history.history_month_wage_total
import taminx.feature.history.history_orb_caption_all
import taminx.feature.history.history_orb_caption_year
import taminx.feature.history.history_orb_days
import taminx.feature.history.history_report_action
import taminx.feature.history.history_rial
import taminx.feature.history.history_scope_all
import taminx.feature.history.history_stat_sources

/**
 * «کلیه سوابق».
 *
 * [onBackClicked] deliberately has no default: it is what «بستن» on a failed load and the hero's
 * chevron both lead to, and a defaulted no-op here is a screen the user cannot leave.
 */
@Composable
fun HistoryScreen(
    onBackClicked: () -> Unit,
    viewModel: HistoryViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val toaster = LocalToaster.current

    LaunchedEffect(Unit) { viewModel.sendIntent(HistoryIntent.Load) }

    HandleHistoryEvents(events = viewModel.events, toaster = toaster)

    HistoryContent(
        uiState = uiState,
        lazyListState = lazyListState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
    )
}

@Composable
fun HandleHistoryEvents(
    events: Flow<HistoryEvent>,
    toaster: ToasterState,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            // Resolved here, where a composition exists to resolve it in.
            is HistoryEvent.ShowToast -> toaster.error(getString(event.message))
        }
    }
}

@Composable
fun HistoryContent(
    uiState: HistoryUiState,
    lazyListState: LazyListState,
    onIntent: (HistoryIntent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val scope = uiState.scope
    val dayLabel = stringResource(HistoryRes.string.history_combined_year_days, PlaceholderDays)

    // Everything drawn is folded here, each piece keyed on exactly what it is folded from, so a
    // scroll costs nothing and a chip tap re-folds only what that chip changed.
    val chips = remember(uiState.years) { uiState.years.yearChips { it.toPersianDigits() } }
    val selectedYear = remember(uiState.years, scope) {
        (scope as? HistoryScope.Year)?.let { picked ->
            uiState.years.firstOrNull { it.year == picked.year }
        }
    }
    val detail = remember(selectedYear, uiState.wageByYear) {
        selectedYear?.detailWith(uiState.wageByYear[selectedYear.year] ?: NoWorkshops)
    }

    val labels = DurationLabels(
        years = stringResource(HistoryRes.string.history_combined_stat_years),
        months = stringResource(HistoryRes.string.history_combined_stat_months),
        days = stringResource(HistoryRes.string.history_combined_stat_days),
        sources = stringResource(HistoryRes.string.history_stat_sources),
    )
    val durations = remember(scope, uiState.careerTotal, detail, labels) {
        if (scope is HistoryScope.All) {
            careerDurationChips(uiState.careerTotal, labels) { it.toString().toPersianDigits() }
        } else {
            yearDurationChips(detail, labels) { it.toString().toPersianDigits() }
        }
    }

    val bars = remember(scope, uiState.years, detail, uiState.selectedSource, uiState.selectedMonth) {
        if (scope is HistoryScope.All) {
            uiState.years.yearBars { it.toPersianDigits() }
        } else {
            val year = (scope as HistoryScope.Year).year.toIntOrNull() ?: 0
            detail.monthBars(
                source = uiState.selectedSource,
                selectedMonth = uiState.selectedMonth,
                daysInMonth = { month -> PersianDateFormatter.daysInMonth(year, month + 1) },
                dayLabel = { days -> dayLabel.replace(PlaceholderDays, days.toString().toPersianDigits()) },
            )
        }
    }
    val dense = scope is HistoryScope.All && bars.size > DenseBarThreshold
    val scopeDays = if (scope is HistoryScope.All) uiState.careerTotal.totalDays else detail?.totalDays ?: 0

    Scaffold(modifier = modifier, containerColor = colors.bgPage) { padding ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding()),
            contentPadding = PaddingValues(bottom = Spacing.xxl),
        ) {
            item(key = HERO_KEY) {
                HistoryHero(
                    title = stringResource(HistoryRes.string.history_all_title),
                    scope = scope,
                    yearChips = chips,
                    allChipLabel = stringResource(HistoryRes.string.history_scope_all),
                    orbDays = scopeDays.toString().toPersianDigits(),
                    orbDaysLabel = stringResource(HistoryRes.string.history_orb_days),
                    caption = when (scope) {
                        is HistoryScope.All -> stringResource(HistoryRes.string.history_orb_caption_all)
                        is HistoryScope.Year -> stringResource(
                            HistoryRes.string.history_orb_caption_year,
                            scope.year.toPersianDigits(),
                        )
                    },
                    durations = durations,
                    onScopeChange = { onIntent(HistoryIntent.SelectScope(it)) },
                    navigationIcon = {
                        TaminTopAppBarButton(
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = stringResource(CoreRes.string.action_back),
                            onClick = onBackClicked,
                            bordered = true,
                        )
                    },
                    action = {
                        TaminTopAppBarButton(
                            icon = Icons.Filled.Download,
                            contentDescription = stringResource(HistoryRes.string.history_report_action),
                            onClick = { onIntent(HistoryIntent.ShowReportMenu) },
                            bordered = true,
                        )
                    },
                )
            }

            if (uiState.years.isNotEmpty()) {
                item(key = CHART_KEY) {
                    HistoryChartCard(
                        title = when (scope) {
                            is HistoryScope.All ->
                                stringResource(HistoryRes.string.history_chart_title_all)

                            is HistoryScope.Year -> stringResource(
                                HistoryRes.string.history_chart_title_year,
                                scope.year.toPersianDigits(),
                            )
                        },
                        hint = stringResource(
                            if (scope is HistoryScope.All) {
                                HistoryRes.string.history_chart_hint_all
                            } else {
                                HistoryRes.string.history_chart_hint_year
                            },
                        ),
                        bars = bars,
                        onBarClick = { id ->
                            if (scope is HistoryScope.All) {
                                onIntent(HistoryIntent.SelectScope(HistoryScope.Year(id)))
                            } else {
                                id.toIntOrNull()?.let { onIntent(HistoryIntent.SelectMonth(it)) }
                            }
                        },
                        modifier = Modifier
                            .offset(y = ChartOverlap)
                            .padding(horizontal = ChartSidePadding),
                        dense = dense,
                        axis = if (dense) {
                            ChartAxis(
                                oldest = bars.last().label,
                                middle = bars[bars.size / 2].label,
                                newest = bars.first().label,
                            )
                        } else {
                            null
                        },
                        sourceChips = detail.sourceChips(
                            selected = uiState.selectedSource,
                            allLabel = stringResource(HistoryRes.string.history_scope_all),
                        ),
                        onSourceClick = { onIntent(HistoryIntent.SelectSource(it)) },
                        concurrency = detail
                            ?.takeIf { it.hasConcurrency && uiState.selectedSource == null }
                            ?.let {
                                stringResource(
                                    HistoryRes.string.history_concurrent_note,
                                    it.workshops.size.toString().toPersianDigits(),
                                )
                            },
                        concurrencyLabel = stringResource(HistoryRes.string.history_concurrent_label),
                    ) {
                        ChartFooter(
                            isAllScope = scope is HistoryScope.All,
                            detail = detail,
                            selectedMonth = uiState.selectedMonth,
                        )
                    }
                }

                item(key = SECTIONS_KEY) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = ChartOverlap)
                            .padding(horizontal = SidePadding),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        if (scope is HistoryScope.All) {
                            HistorySpanNote(
                                yearCount = uiState.years.size,
                                firstYear = uiState.years.first().year,
                                lastYear = uiState.years.last().year,
                                gapYears = remember(uiState.years) { uiState.years.gapYearCount() },
                            )
                        } else {
                            detail?.workshops?.forEach { workshop ->
                                WorkshopSummaryRow(
                                    workshop = workshop,
                                    onClick = {
                                        selectedYear?.let { onIntent(HistoryIntent.SelectYear(it)) }
                                    },
                                )
                            }
                        }

                        HistoryActionCards(
                            onDownload = { onIntent(HistoryIntent.ShowReportMenu) },
                            onSend = { onIntent(HistoryIntent.ShowReportMenu) },
                            modifier = Modifier.padding(top = Spacing.sm),
                        )
                    }
                }
            }

            // Only once a load has returned: an empty list before that means "not known yet", and
            // saying "you have no history" then would be a lie the next frame corrects.
            if (uiState.years.isEmpty() && uiState.hasLoadedOnce && !uiState.isLoading) {
                item(key = EMPTY_STATE_KEY) {
                    EmptyStateMessage(
                        icon = Icons.Outlined.History,
                        title = stringResource(HistoryRes.string.history_combined_empty_title),
                        subtitle = stringResource(HistoryRes.string.history_combined_empty),
                        showIconTile = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SidePadding)
                            .padding(top = Spacing.xl),
                    )
                }
            }

            if (uiState.isLoading && uiState.years.isEmpty()) {
                item(key = SKELETON_KEY) {
                    ShimmerRows(
                        rowHeight = CardHeight,
                        modifier = Modifier.padding(horizontal = SidePadding, vertical = Spacing.md),
                    )
                }
            }
        }
    }

    // Outside the list: a failure is the only thing worth attending to while it is up, and a dialog
    // cannot live in a LazyColumn item.
    //
    // A refusal gets no «تلاش دوباره». Nothing failed and nothing will change on a second attempt —
    // this person simply has no insured years — so the only button is the way out.
    if (uiState.accessDenied) {
        ErrorStateView(
            message = stringResource(HistoryRes.string.history_combined_not_insured),
            onDismiss = onBackClicked,
        )
    } else {
        ErrorStateView(
            message = uiState.error,
            onDismiss = onBackClicked,
            onRetry = { onIntent(HistoryIntent.Load) },
        )
    }

    uiState.selectedYear?.let { year ->
        YearDetailSheet(
            year = year,
            workshops = uiState.wageByYear[year.year] ?: NoWorkshops,
            wagesUnavailable = uiState.wagesUnavailable,
            onDismiss = { onIntent(HistoryIntent.DismissYearDetail) },
        )
    }

    if (uiState.showReportMenu) {
        ReportMenuSheet(
            onSelect = { onIntent(HistoryIntent.SelectReport(it)) },
            onDismiss = { onIntent(HistoryIntent.DismissReportMenu) },
        )
    }

    // The same viewer every downloaded document in the app opens in: it renders a copy already on
    // the device without asking, saves the one it fetches, and refuses a body that is not a PDF.
    uiState.selectedReport?.let { report ->
        TaminPdfViewer(
            fileName = report.fileName(),
            pdf = uiState.reportPdf,
            downloadFailed = uiState.reportDownloadFailed,
            onRequestDownload = { onIntent(HistoryIntent.DownloadReport) },
            onDismiss = { onIntent(HistoryIntent.DismissReport) },
            title = stringResource(report.labelRes()),
        )
    }
}

/**
 * The line under the chart, which says a different thing in each of the four states the design
 * gives it: pick a year, this year is empty, pick a month, or here is what that month paid.
 */
@Composable
private fun ChartFooter(
    isAllScope: Boolean,
    detail: YearDetailPR?,
    selectedMonth: Int?,
) {
    when {
        isAllScope -> DetailLine(stringResource(HistoryRes.string.history_detail_pick_year))

        detail == null -> DetailLine(stringResource(HistoryRes.string.history_detail_year_empty))

        selectedMonth == null ->
            DetailLine(stringResource(HistoryRes.string.history_detail_pick_month))

        else -> MonthWageBreakdown(
            detail = detail,
            month = selectedMonth,
            totalLabel = stringResource(HistoryRes.string.history_month_wage_total),
            rialLabel = stringResource(HistoryRes.string.history_rial),
            toPersianDigits = { it.toPersianDigits() },
            formatWage = { it.toRialAmount(fallback = "").removeSuffix(RialSuffix).toPersianDigits() },
        )
    }
}

@Composable
private fun DetailLine(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = LocalTaminColors.current.textSecondary,
        modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
    )
}

/**
 * What the saved file is called on the device.
 *
 * Stable per report, because [TaminPdfViewer] uses the name to decide whether it already has the
 * file — a name with a timestamp in it would download the same report again every time.
 */
private fun HistoryCertificateType.fileName(): String = when (this) {
    HistoryCertificateType.ALL -> "history_all.pdf"
    HistoryCertificateType.WAGES -> "history_wages.pdf"
    HistoryCertificateType.COMBINED -> "history_combined.pdf"
}

private const val HERO_KEY = "hero"
private const val CHART_KEY = "chart"
private const val SECTIONS_KEY = "sections"
private const val EMPTY_STATE_KEY = "empty"
private const val SKELETON_KEY = "skeleton"

/** Above this many years the design narrows the bars and drops their labels for an axis. */
private const val DenseBarThreshold = 12

/** Stands in for the number while «%s روز» is resolved once instead of per bar. */
private const val PlaceholderDays = "#"

/** `toRialAmount` appends the unit; the bubble and the rows print it themselves. */
private const val RialSuffix = " ریال"

/** What a card measures, so the skeleton stands in for one without the page jumping. */
private val CardHeight = 76.dp

/** The chart card rides up over the hero's rounded edge. */
private val ChartOverlap = (-18).dp

private val SidePadding = 18.dp
private val ChartSidePadding = 14.dp

@PreviewRtlTheme
@Composable
private fun HistoryScreenPreview() {
    PreviewRtlThemeContent {
        HistoryContent(
            uiState = HistoryUiState(hasLoadedOnce = true, years = PreviewYears),
            lazyListState = rememberLazyListState(),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun HistoryScreenYearScopePreview() {
    PreviewRtlThemeContent {
        HistoryContent(
            uiState = HistoryUiState(
                hasLoadedOnce = true,
                years = PreviewYears,
                scope = HistoryScope.Year("1401"),
            ),
            lazyListState = rememberLazyListState(),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun HistoryScreenEmptyPreview() {
    PreviewRtlThemeContent {
        HistoryContent(
            uiState = HistoryUiState(hasLoadedOnce = true),
            lazyListState = rememberLazyListState(),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

/** The one state with no «تلاش دوباره»: nothing failed, so there is nothing to retry. */
@PreviewRtlTheme
@Composable
private fun HistoryScreenAccessDeniedPreview() {
    PreviewRtlThemeContent {
        HistoryContent(
            uiState = HistoryUiState(accessDenied = true),
            lazyListState = rememberLazyListState(),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

/** A full year, a part year, and a year with a gap in the middle. */
private val PreviewYears = persistentListOf(
    YearHistoryPR(
        year = "1400",
        monthDays = persistentListOf(0, 0, 0, 31, 31, 31, 0, 0, 0, 0, 0, 0),
        totalDays = 93,
    ),
    YearHistoryPR(
        year = "1401",
        monthDays = persistentListOf(31, 31, 31, 0, 0, 0, 30, 30, 30, 30, 30, 29),
        totalDays = 272,
    ),
    YearHistoryPR(
        year = "1402",
        monthDays = persistentListOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29),
        totalDays = 365,
    ),
)
