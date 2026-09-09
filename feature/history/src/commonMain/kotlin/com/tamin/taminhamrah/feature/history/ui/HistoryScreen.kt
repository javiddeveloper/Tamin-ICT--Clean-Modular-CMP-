package com.tamin.taminhamrah.feature.history.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.history.ui.components.DashedDivider
import com.tamin.taminhamrah.feature.history.ui.components.DurationCardPR
import com.tamin.taminhamrah.feature.history.ui.components.DurationPart
import com.tamin.taminhamrah.feature.history.ui.components.FilterChipPR
import com.tamin.taminhamrah.feature.history.ui.components.HistoryActionCards
import com.tamin.taminhamrah.feature.history.ui.components.HistoryChartCard
import com.tamin.taminhamrah.feature.history.ui.components.HistorySpanNote
import com.tamin.taminhamrah.feature.history.ui.components.HistoryTopArea
import com.tamin.taminhamrah.feature.history.ui.components.ManyWorkshopsBanner
import com.tamin.taminhamrah.feature.history.ui.components.MonthWageBreakdown
import com.tamin.taminhamrah.feature.history.ui.components.NoWorkshops
import com.tamin.taminhamrah.feature.history.ui.components.ReportMenuSheet
import com.tamin.taminhamrah.feature.history.ui.components.WageText
import com.tamin.taminhamrah.feature.history.ui.components.WorkshopSplitRowPR
import com.tamin.taminhamrah.feature.history.ui.components.WorkshopSummaryRow
import com.tamin.taminhamrah.feature.history.ui.components.YearMonthPickerSheet
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryEvent
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryIntent
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryUiState
import com.tamin.taminhamrah.feature.history.ui.model.DurationLabels
import com.tamin.taminhamrah.feature.history.ui.model.HistoryMetric
import com.tamin.taminhamrah.feature.history.ui.model.HistoryScope
import com.tamin.taminhamrah.feature.history.ui.model.YearDetailPR
import com.tamin.taminhamrah.feature.history.ui.model.YearHistoryPR
import com.tamin.taminhamrah.feature.history.ui.model.detailWith
import com.tamin.taminhamrah.feature.history.ui.model.displayName
import com.tamin.taminhamrah.feature.history.ui.model.gapYearCount
import com.tamin.taminhamrah.feature.history.ui.model.maxMonthWage
import com.tamin.taminhamrah.feature.history.ui.model.maxYearWage
import com.tamin.taminhamrah.feature.history.ui.model.monthBars
import com.tamin.taminhamrah.feature.history.ui.model.pickerMonthRows
import com.tamin.taminhamrah.feature.history.ui.model.pickerYearRows
import com.tamin.taminhamrah.feature.history.ui.model.sourceChips
import com.tamin.taminhamrah.feature.history.ui.model.span
import com.tamin.taminhamrah.feature.history.ui.model.splitCandidates
import com.tamin.taminhamrah.feature.history.ui.model.wageMonthBars
import com.tamin.taminhamrah.feature.history.ui.model.wageYearBars
import com.tamin.taminhamrah.feature.history.ui.model.yearBars
import com.tamin.taminhamrah.feature.history.ui.model.yearChips
import com.tamin.taminhamrah.mapper.history.labelRes
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.BarChartSeries
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.ShimmerRows
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.rememberCollapsingHeaderState
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.reservedHeight
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.ToasterState
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHistoryWorkshopPalette
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.ui.toRialAmount
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res as CoreRes
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.btn_understood
import taminx.core.core_ui.history_legend_no_bar
import taminx.core.core_ui.history_metric_label
import taminx.core.core_ui.history_picker_apply_month
import taminx.core.core_ui.history_picker_apply_none
import taminx.core.core_ui.history_picker_apply_year
import taminx.core.core_ui.history_picker_no_history
import taminx.core.core_ui.history_scope_all_pill
import taminx.core.core_ui.history_scope_year_pill
import taminx.core.core_ui.history_series_days
import taminx.core.core_ui.history_series_max_days
import taminx.core.core_ui.history_series_max_wage
import taminx.core.core_ui.history_series_wage
import taminx.core.core_ui.history_split_chip
import taminx.core.core_ui.history_split_label
import taminx.feature.history.Res as HistoryRes
import taminx.feature.history.history_action_send_title
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
import taminx.feature.history.history_month_no_record
import taminx.feature.history.history_month_wage_total
import taminx.feature.history.history_rial
import taminx.feature.history.history_scheme_construction
import taminx.feature.history.history_scheme_optional
import taminx.feature.history.history_scope_all
import taminx.feature.history.history_send_confirm_action
import taminx.feature.history.history_send_confirm_body
import taminx.feature.history.history_send_success_title
import taminx.feature.history.history_stat_sources
import taminx.feature.history.history_year_full
import taminx.feature.history.history_year_incomplete

/**
 * «کلیه سوابق».
 *
 * [onBackClicked] deliberately has no default: it is what «بستن» on a failed load and the hero's
 * chevron both lead to, and a defaulted no-op here is a screen the user cannot leave.
 */
@Composable
fun HistoryScreen(
    onBackClicked: () -> Unit,
    onOpenYearWorkshops: (year: String) -> Unit,
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
        onOpenYearWorkshops = onOpenYearWorkshops,
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
    /** «کارگاه‌های سال» — a destination now, so the year travels rather than being held in state. */
    onOpenYearWorkshops: (year: String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val scope = uiState.scope
    // The viewer opens out of this page, so it wears this page's head — in whichever theme.
    val viewerHeaderBrush = colors.heroBrush
    val dayLabel = stringResource(HistoryRes.string.history_combined_year_days, HistoryConstants.PLACEHOLDER_DAYS)

    // Everything drawn is folded here, each piece keyed on exactly what it is folded from, so a
    // scroll costs nothing and a chip tap re-folds only what that chip changed.
    val chips = remember(uiState.years) { uiState.years.yearChips { it.toPersianDigits() } }
    val selectedYear = remember(uiState.years, scope) {
        (scope as? HistoryScope.Year)?.let { picked ->
            uiState.years.firstOrNull { it.year == picked.year }
        }
    }
    // The two schemes that arrive without a workshop name of their own.
    val optionalScheme = stringResource(HistoryRes.string.history_scheme_optional)
    val constructionScheme = stringResource(HistoryRes.string.history_scheme_construction)
    val detail = remember(selectedYear, uiState.wageByYear, optionalScheme, constructionScheme) {
        selectedYear?.detailWith(
            rows = uiState.wageByYear[selectedYear.year] ?: NoWorkshops,
            optionalSchemeName = optionalScheme,
            constructionSchemeName = constructionScheme,
        )
    }

    val labels = DurationLabels(
        years = stringResource(HistoryRes.string.history_combined_stat_years),
        months = stringResource(HistoryRes.string.history_combined_stat_months),
        days = stringResource(HistoryRes.string.history_combined_stat_days),
        sources = stringResource(HistoryRes.string.history_stat_sources),
    )
    val allScopePillFormat = stringResource(CoreRes.string.history_scope_all_pill, PLACEHOLDER, PLACEHOLDER_2)
    val yearScopePillFormat = stringResource(CoreRes.string.history_scope_year_pill, PLACEHOLDER, PLACEHOLDER_2)
    val allChipLabel = stringResource(HistoryRes.string.history_scope_all)

    val scopeKey = when (scope) {
        is HistoryScope.All -> "all"
        is HistoryScope.Year -> scope.year
    }
    val recentYears = remember(uiState.years) {
        uiState.years.map { it.year }.asReversed()
    }
    val navSeq = remember(recentYears) {
        listOf("all") + recentYears
    }
    val seqIndex = remember(navSeq, scopeKey) {
        val idx = navSeq.indexOf(scopeKey)
        if (idx >= 0) idx else 0
    }
    val hasOlder = seqIndex < navSeq.size - 1
    val hasNewer = seqIndex > 0

    val onStepOlder: () -> Unit = remember(hasOlder, seqIndex, navSeq, onIntent) {
        {
            if (hasOlder) {
                val next = navSeq[seqIndex + 1]
                if (next == "all") {
                    onIntent(HistoryIntent.SelectScope(HistoryScope.All))
                } else {
                    onIntent(HistoryIntent.SelectScope(HistoryScope.Year(next)))
                }
            }
        }
    }
    val onStepNewer: () -> Unit = remember(hasNewer, seqIndex, navSeq, onIntent) {
        {
            if (hasNewer) {
                val prev = navSeq[seqIndex - 1]
                if (prev == "all") {
                    onIntent(HistoryIntent.SelectScope(HistoryScope.All))
                } else {
                    onIntent(HistoryIntent.SelectScope(HistoryScope.Year(prev)))
                }
            }
        }
    }

    val durationCardModel = remember(
        scope,
        uiState.careerTotal,
        detail,
        uiState.years,
        hasOlder,
        hasNewer,
        labels,
        allScopePillFormat,
        yearScopePillFormat,
        allChipLabel,
    ) {
        if (scope is HistoryScope.All) {
            val span = uiState.years.span()
            val pill = if (span != null) {
                allScopePillFormat
                    .replace(PLACEHOLDER, span.oldest.toPersianDigits())
                    .replace(PLACEHOLDER_2, span.newest.toPersianDigits())
            } else {
                allChipLabel
            }
            DurationCardPR(
                pillText = pill,
                part1 = DurationPart(
                    number = uiState.careerTotal.years.toString().toPersianDigits(),
                    unit = labels.years,
                ),
                part2 = if (uiState.careerTotal.months > 0) {
                    DurationPart(
                        number = uiState.careerTotal.months.toString().toPersianDigits(),
                        unit = labels.months,
                    )
                } else null,
                part3 = if (uiState.careerTotal.days > 0) {
                    DurationPart(
                        number = uiState.careerTotal.days.toString().toPersianDigits(),
                        unit = labels.days,
                    )
                } else null,
                hasOlder = hasOlder,
                hasNewer = hasNewer,
            )
        } else {
            val sDays = detail?.totalDays ?: 0
            val sCount = detail?.workshops?.size ?: 0
            val pill = if (detail != null) {
                yearScopePillFormat
                    .replace(PLACEHOLDER, sCount.toString().toPersianDigits())
                    .replace(PLACEHOLDER_2, sDays.toString().toPersianDigits())
            } else {
                labels.sources
            }
            val months = sDays / HistoryConstants.DAYS_IN_MONTH
            val days = sDays % HistoryConstants.DAYS_IN_MONTH
            DurationCardPR(
                pillText = pill,
                part1 = DurationPart(
                    number = (scope as HistoryScope.Year).year.toPersianDigits(),
                    unit = "",
                ),
                part2 = if (sDays > 0) {
                    DurationPart(
                        number = months.toString().toPersianDigits(),
                        unit = labels.months,
                    )
                } else null,
                part3 = if (days > 0) {
                    DurationPart(
                        number = days.toString().toPersianDigits(),
                        unit = labels.days,
                    )
                } else null,
                hasOlder = hasOlder,
                hasNewer = hasNewer,
            )
        }
    }

    val dayBars = remember(scope, uiState.years, detail, uiState.selectedSource, uiState.selectedMonth) {
        if (scope is HistoryScope.All) {
            uiState.years.yearBars { it.toPersianDigits() }
        } else {
            val year = (scope as HistoryScope.Year).year.toIntOrNull() ?: 0
            detail.monthBars(
                source = uiState.selectedSource,
                selectedMonth = uiState.selectedMonth,
                daysInMonth = { month -> PersianDateFormatter.daysInMonth(year, month + 1) },
                dayLabel = { days -> dayLabel.replace(HistoryConstants.PLACEHOLDER_DAYS, days.toString().toPersianDigits()) },
            )
        }
    }

    // The wage rows for whichever scope is on screen: every year at once, or one year's employers.
    val scopeWages = remember(scope, uiState.wageByYear) {
        when (scope) {
            is HistoryScope.All -> null
            is HistoryScope.Year -> uiState.wageByYear[scope.year]
        }
    }

    val wageBars = remember(scope, uiState.years, uiState.wageByYear, scopeWages, uiState.selectedSource, uiState.selectedMonth, detail) {
        if (scope is HistoryScope.All) {
            uiState.years.wageYearBars(uiState.wageByYear) { it.toPersianDigits() }
        } else {
            val year = (scope as HistoryScope.Year).year.toIntOrNull() ?: 0
            scopeWages.wageMonthBars(
                source = uiState.selectedSource,
                selectedMonth = uiState.selectedMonth,
                wageLabel = { wage -> wage.toString().toPriceFormat().toPersianDigits() },
                isMonthFull = { month ->
                    val daysInMonth = PersianDateFormatter.daysInMonth(year, month + 1)
                    val workedDays = detail?.monthDays?.getOrNull(month) ?: 0
                    workedDays >= daysInMonth
                },
            )
        }
    }

    // How tall each plot is, and what its «بیشینه» says. Both series scale differently on purpose
    // — see WageBars.kt — so each states its own ceiling rather than sharing one axis.
    val maxWage = remember(scope, uiState.years, uiState.wageByYear, scopeWages, uiState.selectedSource) {
        if (scope is HistoryScope.All) {
            uiState.years.maxYearWage(uiState.wageByYear)
        } else {
            scopeWages.maxMonthWage(uiState.selectedSource)
        }
    }
    val maxDayLabel = stringResource(
        CoreRes.string.history_series_max_days,
        (if (scope is HistoryScope.All) {
            HistoryConstants.DAYS_IN_LEAP_YEAR.toInt()
        } else {
            HistoryConstants.DAYS_IN_MONTH
        }).toString().toPersianDigits(),
    )
    val maxWageLabel = stringResource(
        CoreRes.string.history_series_max_wage,
        (maxWage / MILLION).toString().toPriceFormat().toPersianDigits(),
    )
    val wageTitle = stringResource(CoreRes.string.history_series_wage)
    val daysTitle = stringResource(CoreRes.string.history_series_days)
    val metric = uiState.metric

    // One list, rebuilt only when something it draws changes. Handing the group a freshly built
    // list every recomposition would cost it — and both charts under it — their skippability.
    val series = remember(metric, wageBars, dayBars, maxWageLabel, maxDayLabel, wageTitle, daysTitle, scope) {
        val both = metric == HistoryMetric.BOTH
        buildList {
            if (metric.showsWage) {
                add(
                    BarChartSeries(
                        id = "wage:$scope",
                        title = wageTitle,
                        caption = maxWageLabel,
                        bars = wageBars,
                        plotHeight = if (both) HistoryDimens.plotHeightPaired else HistoryDimens.plotHeightSingle,
                    )
                )
            }
            if (metric.showsDays) {
                add(
                    BarChartSeries(
                        id = "days:$scope",
                        title = daysTitle,
                        caption = maxDayLabel,
                        bars = dayBars,
                        plotHeight = if (both) HistoryDimens.plotHeightSecondary else HistoryDimens.plotHeightSingle,
                    )
                )
            }
        }.toImmutableList()
    }

    // Hoisted so the card and the group under it keep their skippability: an inline lambda is a
    // new instance on every recomposition, and these are handed to a chart that redraws per frame.
    val onBarClick: (String) -> Unit = remember(scope, onIntent) {
        { id ->
            if (scope is HistoryScope.All) {
                onIntent(HistoryIntent.SelectScope(HistoryScope.Year(id)))
            } else {
                id.toIntOrNull()?.let { onIntent(HistoryIntent.SelectMonth(it)) }
            }
        }
    }
    val onSourceClick: (Int?) -> Unit = remember(onIntent) {
        { source -> onIntent(HistoryIntent.SelectSource(source)) }
    }
    val onSplitClick: () -> Unit = remember(onIntent) { { onIntent(HistoryIntent.ToggleSplit) } }

    // Captures the year, not the state: the callback is handed to rows that must stay skippable,
    // and the year a row was drawn for cannot change under it.
    val scopeYear = (scope as? HistoryScope.Year)?.year
    val openYearWorkshops: () -> Unit = remember(scopeYear, onOpenYearWorkshops) {
        { scopeYear?.let(onOpenYearWorkshops) ?: Unit }
    }
    val onMetricClick: (Int) -> Unit = remember(onIntent) {
        { index -> onIntent(HistoryIntent.SelectMetric(HistoryMetric.entries[index])) }
    }

    val metricLabels = HistoryMetric.entries.map { stringResource(it.label) }
    val metricChips = remember(metric, metricLabels) {
        HistoryMetric.entries
            .mapIndexed { index, entry -> FilterChipPR(metricLabels[index], entry == metric) }
            .toImmutableList()
    }

    /*
     * «تفکیک کارگاه» — offered wherever there is more than one employer to pull apart.
     *
     * In «همه» that means the whole career, not one year: a person with two employers across
     * nineteen years can split their chart, which is the design's own rule. Reading it off the
     * open year's detail — which is null in «همه» — is why the chip was missing there.
     */
    val splitSources = remember(uiState.wageByYear, scope, optionalScheme, constructionScheme) {
        splitCandidates(
            wageByYear = uiState.wageByYear,
            year = (scope as? HistoryScope.Year)?.year,
            nameOf = { it.displayName(optionalScheme, constructionScheme) },
        )
    }
    val splitLabel = stringResource(CoreRes.string.history_split_chip)
    val splitChip = remember(splitLabel, uiState.splitBySource, splitSources) {
        FilterChipPR(splitLabel, uiState.splitBySource).takeIf { splitSources.size > 1 }
    }

    val splitRows = remember(uiState.splitBySource, detail, dayLabel) {
        if (!uiState.splitBySource || detail == null || detail.workshops.size <= 1) {
            persistentListOf()
        } else {
            detail.workshops.mapIndexed { index, workshop ->
                val color = TaminHistoryWorkshopPalette[index % TaminHistoryWorkshopPalette.size]
                val maxMonthDays = workshop.months.maxOfOrNull { it.days }?.coerceAtLeast(1) ?: 30
                val opacities = (0 until HistoryConstants.MONTHS_IN_YEAR).map { monthIdx ->
                    val worked = workshop.months.firstOrNull { it.monthIndex == monthIdx }
                    if (worked != null && worked.days > 0) {
                        (0.4f + 0.6f * (worked.days.toFloat() / maxMonthDays.toFloat())).coerceIn(0f, 1f)
                    } else {
                        0f
                    }
                }.toImmutableList()

                val totalWage = workshop.months.sumOf { it.wage.toLongOrNull() ?: 0L }
                val daysStr = dayLabel.replace(HistoryConstants.PLACEHOLDER_DAYS, workshop.totalDays.toString().toPersianDigits())
                val wageStr = if (totalWage > 0L) {
                    " · ${(totalWage / MILLION).toString().toPriceFormat().toPersianDigits()} م ریال"
                } else ""

                WorkshopSplitRowPR(
                    label = workshop.name,
                    color = color,
                    totalText = daysStr + wageStr,
                    monthOpacities = opacities,
                )
            }.toImmutableList()
        }
    }

    // ── «انتخاب سال و ماه» ───────────────────────────────────────────────────────
    // Folded from the chips the strip already uses, so the sheet lists exactly the years the strip
    // does, in the same order, including the gaps.
    val dayLabelOf: (String) -> String = remember(dayLabel) {
        { days -> dayLabel.replace(HistoryConstants.PLACEHOLDER_DAYS, days) }
    }
    val noHistoryLabel = stringResource(CoreRes.string.history_picker_no_history)
    val pickerYearRows = remember(chips, uiState.years, uiState.yearQuery, uiState.pickerYear, dayLabelOf, noHistoryLabel) {
        chips.pickerYearRows(
            years = uiState.years,
            query = uiState.yearQuery,
            picked = uiState.pickerYear,
            dayLabel = dayLabelOf,
            noHistoryLabel = noHistoryLabel,
            toPersian = { it.toPersianDigits() },
        )
    }
    val stagedYear = remember(uiState.years, uiState.pickerYear) {
        uiState.pickerYear?.let { picked -> uiState.years.firstOrNull { it.year == picked } }
    }
    val pickerMonthRows = remember(stagedYear, uiState.pickerMonth, dayLabelOf) {
        stagedYear.pickerMonthRows(
            picked = uiState.pickerMonth,
            monthNames = PersianDateFormatter.monthNames,
            dayLabel = dayLabelOf,
            toPersian = { it.toPersianDigits() },
        )
    }
    val applyNone = stringResource(CoreRes.string.history_picker_apply_none)
    val applyWholeYear = stringResource(CoreRes.string.history_picker_apply_year, PLACEHOLDER)
    val applyMonth = stringResource(CoreRes.string.history_picker_apply_month, PLACEHOLDER, PLACEHOLDER_2)
    val applyLabel = remember(uiState.pickerYear, uiState.pickerMonth, applyNone, applyWholeYear, applyMonth) {
        val year = uiState.pickerYear
        when {
            year == null -> applyNone
            uiState.pickerMonth == null -> applyWholeYear.replace(PLACEHOLDER, year.toPersianDigits())
            else -> applyMonth
                .replace(PLACEHOLDER, PersianDateFormatter.monthNames[uiState.pickerMonth])
                .replace(PLACEHOLDER_2, year.toPersianDigits())
        }
    }
    val yearRange = remember(uiState.years) {
        uiState.years.span()
            ?.let { "${it.oldest.toPersianDigits()} – ${it.newest.toPersianDigits()}" }
            .orEmpty()
    }
    val onQueryChange: (String) -> Unit = remember(onIntent) {
        { query -> onIntent(HistoryIntent.YearQueryChanged(query)) }
    }
    val onPickYear: (String) -> Unit = remember(onIntent) {
        { year -> onIntent(HistoryIntent.PickerYearSelected(year)) }
    }
    val onPickMonth: (Int?) -> Unit = remember(onIntent) {
        { month -> onIntent(HistoryIntent.PickerMonthSelected(month)) }
    }
    val onApplyPick: () -> Unit = remember(onIntent) { { onIntent(HistoryIntent.ApplyYearPicker) } }
    val onPickAllYears: () -> Unit = remember(onIntent) {
        {
            onIntent(HistoryIntent.SelectScope(HistoryScope.All))
            onIntent(HistoryIntent.DismissYearPicker)
        }
    }
    val onDismissPicker: () -> Unit = remember(onIntent) {
        { onIntent(HistoryIntent.DismissYearPicker) }
    }

    val collapse = rememberCollapsingHeaderState(HistoryDimens.heroCollapseDistance)
    var headerHeightPx by remember { mutableIntStateOf(0) }

    Scaffold(modifier = modifier, containerColor = colors.bgPage) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding()),
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(collapse.nestedScrollConnection),
                contentPadding = PaddingValues(bottom = Spacing.xxl),
                // The same give the rest of the app scrolls with.
                overscrollEffect = rememberJellyOverscroll(),
            ) {
                // Leading spacer reserving live height of floating header
                item(key = "header_spacer") {
                    Spacer(modifier = Modifier.reservedHeight { headerHeightPx })
                }

                if (uiState.years.isNotEmpty()) {
                    item(key = HistoryConstants.CHART_KEY) {
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
                        series = series,
                        onBarClick = onBarClick,
                        modifier = Modifier
                            .padding(top = 14.dp)
                            .padding(horizontal = HistoryDimens.chartSidePadding),
                        dense = false,
                        // Twelve full month names never fit side by side; the year labels do.
                        rotateLabels = scope is HistoryScope.Year,
                        axis = null,
                        sourceLabel = stringResource(CoreRes.string.history_split_label),
                        sourceChips = if (uiState.splitBySource) {
                            persistentListOf()
                        } else {
                            detail.sourceChips(
                                selected = uiState.selectedSource,
                                allLabel = stringResource(HistoryRes.string.history_scope_all),
                            )
                        },
                        onSourceClick = onSourceClick,
                        splitChip = splitChip,
                        onSplitClick = onSplitClick,
                        metricLabel = stringResource(CoreRes.string.history_metric_label),
                        metricChips = metricChips,
                        onMetricClick = onMetricClick,
                        fullLabel = stringResource(HistoryRes.string.history_year_full),
                        partialLabel = stringResource(HistoryRes.string.history_year_incomplete),
                        barHint = stringResource(CoreRes.string.history_legend_no_bar),
                        splitRows = splitRows,
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
                        DashedDivider(modifier = Modifier.padding(top = Spacing.sm))
                        ChartFooter(
                            isAllScope = scope is HistoryScope.All,
                            detail = detail,
                            selectedMonth = uiState.selectedMonth,
                        )
                    }
                }

                item(key = HistoryConstants.SECTIONS_KEY) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .padding(horizontal = HistoryDimens.sidePadding),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        if (scope is HistoryScope.All) {
                            val span = uiState.years.span()
                            if (span != null) {
                                HistorySpanNote(
                                    yearCount = uiState.years.size,
                                    firstYear = span.oldest,
                                    lastYear = span.newest,
                                    gapYears = remember(uiState.years) { uiState.years.gapYearCount() },
                                )
                            }
                        } else {
                            if ((detail?.workshops?.size ?: 0) > 1) {
                                ManyWorkshopsBanner(
                                    workshopCount = detail?.workshops?.size ?: 0,
                                    totalDays = detail?.totalDays ?: 0,
                                    onClick = openYearWorkshops,
                                )
                            }
                            detail?.workshops?.forEach { workshop ->
                                WorkshopSummaryRow(
                                    workshop = workshop,
                                    onClick = openYearWorkshops,
                                )
                            }
                        }

                        HistoryActionCards(
                            onDownload = { onIntent(HistoryIntent.ShowReportMenu) },
                            onSend = { onIntent(HistoryIntent.AskSendNotice) },
                            modifier = Modifier.padding(top = Spacing.sm),
                        )
                    }
                }
            }

            // Only once a load has returned: an empty list before that means "not known yet", and
            // saying "you have no history" then would be a lie the next frame corrects.
            if (uiState.years.isEmpty() && uiState.hasLoadedOnce && !uiState.isLoading) {
                item(key = HistoryConstants.EMPTY_STATE_KEY) {
                    EmptyStateMessage(
                        icon = Icons.Outlined.History,
                        title = stringResource(HistoryRes.string.history_combined_empty_title),
                        subtitle = stringResource(HistoryRes.string.history_combined_empty),
                        showIconTile = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = HistoryDimens.sidePadding)
                            .padding(top = Spacing.xl),
                    )
                }
            }

            if (uiState.isLoading && uiState.years.isEmpty()) {
                item(key = HistoryConstants.SKELETON_KEY) {
                    ShimmerRows(
                        rowHeight = HistoryDimens.cardHeight,
                        modifier = Modifier.padding(horizontal = HistoryDimens.sidePadding, vertical = Spacing.md),
                    )
                }
            }
        }

        // Floating top area with curved gradient hero and collapsible duration card
        HistoryTopArea(
            title = stringResource(HistoryRes.string.history_all_title),
            scope = scope,
            yearChips = chips,
            allChipLabel = allChipLabel,
            onScopeChange = { onIntent(HistoryIntent.SelectScope(it)) },
            onSearchClick = { onIntent(HistoryIntent.OpenYearPicker) },
            onDownloadClick = { onIntent(HistoryIntent.ShowReportMenu) },
            onMoreClick = { onIntent(HistoryIntent.OpenYearPicker) },
            onBackClicked = onBackClicked,
            durationCardModel = durationCardModel,
            onStepOlder = onStepOlder,
            onStepNewer = onStepNewer,
            collapseProgress = collapse.progressProvider,
            hasYears = uiState.years.isNotEmpty(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onSizeChanged { headerHeightPx = it.height },
        )
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

    if (uiState.yearPickerOpen) {
        YearMonthPickerSheet(
            query = uiState.yearQuery.toPersianDigits(),
            yearRows = pickerYearRows,
            monthRows = pickerMonthRows,
            // «کل سال» is on only once a year is staged for it to mean anything.
            wholeYearSelected = uiState.pickerMonth == null && uiState.pickerYear != null,
            applyLabel = applyLabel,
            applyEnabled = uiState.pickerYear != null,
            yearRange = yearRange,
            onQueryChange = onQueryChange,
            onYearClick = onPickYear,
            onMonthClick = onPickMonth,
            onApply = onApplyPick,
            onAllYears = onPickAllYears,
            onDismiss = onDismissPicker,
        )
    }

    // Confirmed before it is sent, because it posts on the person's behalf and the service has no
    // undo — the design asks them to look at their history first for exactly that reason.
    if (uiState.showSendConfirm) {
        TaminConfirmationDialog(
            title = stringResource(HistoryRes.string.history_action_send_title),
            description = stringResource(HistoryRes.string.history_send_confirm_body),
            icon = Icons.AutoMirrored.Filled.Send,
            iconTint = colors.orangeText,
            iconBackground = colors.orangeBg,
            onDismissRequest = { onIntent(HistoryIntent.DismissSendConfirm) },
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(HistoryRes.string.history_send_confirm_action),
                    onClick = { onIntent(HistoryIntent.ConfirmSendNotice) },
                    enabled = !uiState.isSending,
                    background = SolidColor(colors.blueText),
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {
                TaminOutlinedButton(
                    text = stringResource(CoreRes.string.action_cancel),
                    onClick = { onIntent(HistoryIntent.DismissSendConfirm) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
        )
    }

    // The service's own wording when it sends any, so the person reads what تأمین said, not our
    // paraphrase of it.
    uiState.sendSuccessMessage?.let { message ->
        TaminConfirmationDialog(
            title = stringResource(HistoryRes.string.history_send_success_title),
            description = message,
            icon = Icons.Filled.Check,
            iconTint = colors.greenText,
            iconBackground = colors.greenBg,
            onDismissRequest = { onIntent(HistoryIntent.DismissSendSuccess) },
            confirmButton = {},
            dismissButton = {
                TaminFilledButton(
                    text = stringResource(CoreRes.string.btn_understood),
                    onClick = { onIntent(HistoryIntent.DismissSendSuccess) },
                    background = SolidColor(colors.blueText),
                    modifier = Modifier.fillMaxWidth(),
                )
            },
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
            fileName = report.fileName(uiState.nationalId),
            pdf = uiState.reportPdf,
            downloadFailed = uiState.reportDownloadFailed,
            onRequestDownload = { onIntent(HistoryIntent.DownloadReport) },
            onDismiss = { onIntent(HistoryIntent.DismissReport) },
            title = stringResource(report.labelRes()),
            // The viewer opens out of this page, so it wears this page's head rather than the
            // app-wide teal — the document and the screen that asked for it read as one thing.
            background = viewerHeaderBrush,
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

        // One employer needs no breakdown and no total: the design prints the month, its days and
        // what it paid on a single line, and keeps the table for months that were shared.
        detail.workshops.count { shop -> shop.months.any { it.monthIndex == selectedMonth } } <= 1 ->
            SingleSourceMonthLine(detail = detail, month = selectedMonth)

        else -> MonthWageBreakdown(
            detail = detail,
            month = selectedMonth,
            totalLabel = stringResource(HistoryRes.string.history_month_wage_total),
            rialLabel = stringResource(HistoryRes.string.history_rial),
            toPersianDigits = { it.toPersianDigits() },
            formatWage = { it.toRialAmount(fallback = "").removeSuffix(HistoryConstants.RIAL_SUFFIX).toPersianDigits() },
        )
    }
}

/** «آذر ۱۴۰۴ · ۳۰ روز» and the wage beside it — the design's one-employer month. */
@Composable
private fun SingleSourceMonthLine(detail: YearDetailPR, month: Int) {
    val colors = LocalTaminColors.current
    val worked = remember(detail, month) {
        detail.workshops.firstNotNullOfOrNull { shop ->
            shop.months.firstOrNull { it.monthIndex == month }
        }
    }
    val days = worked?.days ?: 0

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = PersianDateFormatter.monthNames[month] + HistoryConstants.SEPARATOR + detail.year.toPersianDigits() +
                HistoryConstants.SEPARATOR +
                if (days > 0) {
                    stringResource(
                        HistoryRes.string.history_combined_year_days,
                        days.toString().toPersianDigits(),
                    )
                } else {
                    stringResource(HistoryRes.string.history_month_no_record)
                },
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
        )
        worked?.wage?.takeIf { (it.toLongOrNull() ?: 0L) > 0L }?.let { wage ->
            WageText(
                amount = wage.toRialAmount(fallback = "").removeSuffix(HistoryConstants.RIAL_SUFFIX).toPersianDigits(),
                rialLabel = stringResource(HistoryRes.string.history_rial),
                color = colors.textPrimary,
            )
        }
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
 * Stable per report and per person, because [TaminPdfViewer] uses the name to decide whether it
 * already has the file — a name with a timestamp in it would download the same report every time.
 */
private fun HistoryCertificateType.fileName(nationalId: String?): String {
    val kind = when (this) {
        HistoryCertificateType.ALL -> "history_all"
        HistoryCertificateType.WAGES -> "history_wages"
        HistoryCertificateType.COMBINED -> "history_combined"
    }
    // Whose history it is, so several people's files on one device stay apart — and so the name in
    // the download notification says something. ASCII digits: this is a file name, not a label.
    return listOfNotNull(kind, nationalId?.takeIf { it.isNotBlank() }).joinToString("_") + ".pdf"
}

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

/** The page with a real career behind it — the chart, the note and the action cards. */
@PreviewRtlTheme
@Composable
private fun HistoryFixturePreview() {
    PreviewRtlThemeContent { HistoryFixtureScreen() }
}

/** One year: month bars, the source chips and the concurrency legend. */
@PreviewRtlTheme
@Composable
private fun HistoryFixtureYearScopePreview() {
    PreviewRtlThemeContent {
        HistoryContent(
            uiState = HistoryFixtures.state.copy(scope = HistoryScope.Year("1404")),
            lazyListState = rememberLazyListState(),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

/** Wages are reported in rials; the chart's «بیشینه» states them in millions, as the design does. */
private const val MILLION = 1_000_000L

/**
 * Stand-ins for the picker's two format arguments.
 *
 * `stringResource` needs its arguments at composition; the label is folded in a `remember` keyed on
 * the staged pick, so the resource is read once with placeholders and filled in there instead of
 * being re-read on every tap.
 */
private const val PLACEHOLDER = "%%1"
private const val PLACEHOLDER_2 = "%%2"

/**
 * Offsets the composable vertically by [overlap] and reduces its measured height by the same amount,
 * behaving like `margin-top: -overlap` without leaving trailing empty layout space.
 */
private fun Modifier.overlapTop(overlap: Dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val overlapPx = overlap.roundToPx()
    layout(placeable.width, (placeable.height - overlapPx).coerceAtLeast(0)) {
        placeable.placeRelative(0, -overlapPx)
    }
}

