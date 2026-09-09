package com.tamin.taminhamrah.feature.history.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.history.ui.HistoryConstants
import com.tamin.taminhamrah.feature.history.ui.HistoryDimens
import com.tamin.taminhamrah.feature.history.ui.model.YearDetailPR
import com.tamin.taminhamrah.ui.components.BarChartSeries
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminBarChart
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarFullBottom
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarFullTop
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarPartialMonthBottom
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarPartialMonthTop
import com.tamin.taminhamrah.ui.theme.TaminHistoryConcurrentBottom
import com.tamin.taminhamrah.ui.theme.TaminHistoryConcurrentTop
import com.tamin.taminhamrah.ui.theme.TaminHistoryIndicatorEnd
import com.tamin.taminhamrah.ui.theme.TaminHistoryIndicatorStart
import com.tamin.taminhamrah.ui.theme.TaminHistoryLegendBg
import com.tamin.taminhamrah.ui.theme.TaminHistorySubChartBgEnd
import com.tamin.taminhamrah.ui.theme.TaminHistorySubChartBgStart
import com.tamin.taminhamrah.ui.theme.TaminHistorySubChartBorder
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** One filter above the month bars: «همه», or a single employer. */
@Immutable
data class SourceChipPR(val label: String, val selected: Boolean)

/** One tappable pill: a filter, a series, or a toggle. */
@Immutable
data class FilterChipPR(val label: String, val selected: Boolean)

/**
 * One employer's own timeline under the chart — «تفکیک کارگاه».
 *
 * [cellOpacities] holds one value per column of the chart above it: a year across «همه», a month
 * inside a single year. Zero means the employer reported nothing in that column, which draws as the
 * empty track rather than as no cell at all, so every row stays the same length and the columns
 * still line up.
 */
@Immutable
data class WorkshopSplitRowPR(
    val label: String,
    val color: Color,
    val totalText: String,
    val cellOpacities: ImmutableList<Float>,
    /** The column the chart has open, ringed on every row so they can be read across. */
    val selectedCell: Int? = null,
)

/** The three points a dense series is read by, instead of a label under every bar. */
@Immutable
data class ChartAxis(val oldest: String, val middle: String, val newest: String)

/**
 * The redesigned chart card for «کلیه سوابق».
 *
 * Features:
 * - Clean white container with rounded corners and border.
 * - Header with vertical gradient indicator bar, title and hint.
 * - Employer (کارگاه) filter row + «تفکیک کارگاه» toggle button.
 * - Metric switcher row (شاخص: هر دو / دستمزد / روزهای کار).
 * - Styled sub-chart cards for each series (wage, days) with max badges and gridlines.
 * - Legend status bar (سال کامل / سال ناقص / بدون سابقه، ستونی ندارد).
 * - Optional workshop timeline breakdown when split mode is active.
 * - Detail footer.
 */
@Composable
fun HistoryChartCard(
    title: String,
    hint: String,
    series: ImmutableList<BarChartSeries>,
    onBarClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    dense: Boolean = false,
    axis: ChartAxis? = null,
    sourceLabel: String = "",
    sourceChips: ImmutableList<SourceChipPR> = persistentListOf(),
    onSourceClick: (Int?) -> Unit = {},
    splitChip: FilterChipPR? = null,
    onSplitClick: () -> Unit = {},
    metricLabel: String = "",
    metricChips: ImmutableList<FilterChipPR> = persistentListOf(),
    onMetricClick: (Int) -> Unit = {},
    rotateLabels: Boolean = false,
    fullLabel: String = "سال کامل",
    partialLabel: String = "سال ناقص",
    barHint: String = "بدون سابقه، ستونی ندارد",
    splitRows: ImmutableList<WorkshopSplitRowPR> = persistentListOf(),
    concurrency: String? = null,
    concurrencyLabel: String = "",
    footer: @Composable ColumnScope.() -> Unit = {},
) {
    val colors = LocalTaminColors.current
    val indicatorBrush = remember {
        Brush.verticalGradient(listOf(TaminHistoryIndicatorStart, TaminHistoryIndicatorEnd))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HistoryDimens.cardCorner))
            .background(colors.bgSurface)
            .border(HistoryDimens.hairline, Color(0xFFEEF1F6), RoundedCornerShape(HistoryDimens.cardCorner))
            .padding(horizontal = HistoryDimens.cardPaddingH, vertical = HistoryDimens.cardPaddingV),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        // Header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                // Vertical accent indicator
                Box(
                    modifier = Modifier
                        .size(width = HistoryDimens.indicatorWidth, height = HistoryDimens.indicatorHeight)
                        .clip(RoundedCornerShape(HistoryDimens.indicatorCorner))
                        .background(indicatorBrush),
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                    color = Color(0xFF0F172A),
                )
            }
            Text(
                text = hint,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.5.sp,
                    color = Color(0xFF64748B),
                ),
            )
        }

        // Workshop chips row
        if (sourceChips.isNotEmpty() || splitChip != null) {
            SourceChipRow(
                label = sourceLabel,
                chips = sourceChips,
                onSourceClick = onSourceClick,
                splitChip = splitChip,
                onSplitClick = onSplitClick,
            )
            DashedDivider(modifier = Modifier.padding(top = 2.dp))
        }

        // Metric chips row
        if (metricChips.isNotEmpty()) {
            MetricChipRow(
                label = metricLabel,
                chips = metricChips,
                onPick = onMetricClick,
            )
        }

        val chartScrollState = rememberScrollState()

        // Sub-chart containers
        series.forEach { plot ->
            SubChartContainer(
                series = plot,
                onBarClick = onBarClick,
                dense = dense,
                rotateLabels = rotateLabels,
                scrollState = chartScrollState,
            )
        }

        // Axis row if dense series
        axis?.let { ChartAxisRow(it) }

        // Legend bar
        LegendBar(
            fullLabel = fullLabel,
            partialLabel = partialLabel,
            hint = barHint,
        )

        // Workshop split timeline rows, ruled off from the legend above them as the design has it.
        if (splitChip?.selected == true && splitRows.isNotEmpty()) {
            DashedDivider(modifier = Modifier.padding(top = 11.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 11.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                splitRows.forEach { row ->
                    WorkshopTimelineRow(row = row)
                }
            }
        }

        // Concurrent employment indicator
        concurrency?.let { note ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val capBrush = remember {
                    Brush.verticalGradient(
                        listOf(TaminHistoryConcurrentTop, TaminHistoryConcurrentBottom),
                    )
                }
                Box(
                    modifier = Modifier
                        .size(width = HistoryDimens.legendSwatchWidth, height = HistoryDimens.legendSwatchHeight)
                        .clip(RoundedCornerShape(HistoryDimens.legendSwatchCorner))
                        .background(capBrush),
                )
                Text(
                    text = concurrencyLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.tealText,
                )
                Text(
                    text = note,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                )
            }
        }

        footer()
    }
}

/** Card container for an individual sub-chart (Wage / Days). */
@Composable
private fun SubChartContainer(
    series: BarChartSeries,
    onBarClick: (String) -> Unit,
    dense: Boolean,
    rotateLabels: Boolean,
    scrollState: androidx.compose.foundation.ScrollState,
) {
    val containerBrush = remember {
        Brush.verticalGradient(listOf(TaminHistorySubChartBgStart, TaminHistorySubChartBgEnd))
    }
    val containerShape = remember { RoundedCornerShape(HistoryDimens.subChartCorner) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(containerShape)
            .background(containerBrush)
            .border(HistoryDimens.hairline, TaminHistorySubChartBorder, containerShape)
            .padding(horizontal = HistoryDimens.subChartPaddingH, vertical = HistoryDimens.subChartPaddingV),
    ) {
        // Sub-chart header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = series.title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                ),
                color = Color(0xFF0F172A),
            )

            if (series.caption.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(HistoryDimens.hairline, Color(0xFFE8EDF5), CircleShape)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = series.caption,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = Color(0xFF64748B),
                    )
                }
            }
        }

        // Plot area with adaptive horizontal scrolling and shared synchronized scroll
        TaminBarChart(
            bars = series.bars,
            onBarClick = onBarClick,
            plotHeight = series.plotHeight,
            dense = dense,
            showLabels = true,
            rotateLabels = rotateLabels,
            animationKey = series.id,
            scrollBehavior = com.tamin.taminhamrah.ui.components.ChartScrollBehavior.Adaptive,
            minBarWidth = 34.dp,
            gap = 6.dp,
            scrollState = scrollState,
            gridLines = com.tamin.taminhamrah.ui.components.ChartGridLines(
                showTop = true,
                showMiddle = true,
                showBaseline = true,
                lineColor = Color(0x120F172A),
                middleLineColor = Color(0x0D0F172A),
                baselineColor = Color(0x240F172A),
            ),
        )
    }
}

/** Status chips legend: Full, Partial, and Empty hint. */
@Composable
private fun LegendBar(
    fullLabel: String,
    partialLabel: String,
    hint: String,
) {
    val fullBrush = remember {
        Brush.verticalGradient(listOf(TaminHistoryBarFullTop, TaminHistoryBarFullBottom))
    }
    val partialBrush = remember {
        Brush.verticalGradient(listOf(TaminHistoryBarPartialMonthTop, TaminHistoryBarPartialMonthBottom))
    }
    val shape = remember { RoundedCornerShape(HistoryDimens.legendCorner) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(TaminHistoryLegendBg)
            .padding(horizontal = HistoryDimens.legendPaddingH, vertical = HistoryDimens.legendPaddingV),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Full cover swatch
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(HistoryDimens.legendDotSize)
                    .clip(RoundedCornerShape(HistoryDimens.legendDotCorner))
                    .background(fullBrush),
            )
            Text(
                text = fullLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = Color(0xFF64748B),
            )
        }

        // Partial cover swatch
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(HistoryDimens.legendDotSize)
                    .clip(RoundedCornerShape(HistoryDimens.legendDotCorner))
                    .background(partialBrush),
            )
            Text(
                text = partialLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = Color(0xFF64748B),
            )
        }

        // Empty hint
        Text(
            text = hint,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            color = Color(0xFF64748B),
        )
    }
}

/** One workshop row in the split timeline view. */
@Composable
private fun WorkshopTimelineRow(row: WorkshopSplitRowPR) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(row.color),
                )
                Text(
                    text = row.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                )
            }
            NumericText(
                text = row.totalText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = Color(0xFF64748B),
            )
        }

        // One cell per column of the chart above: a year in «همه», a month inside one year.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            val cellShape = RoundedCornerShape(3.dp)
            row.cellOpacities.forEachIndexed { index, opacity ->
                val worked = opacity > 0f
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(9.dp)
                        .clip(cellShape)
                        .background(if (worked) row.color.copy(alpha = opacity) else Color(0x120F172A))
                        // Only a column this employer actually worked is worth ringing; ringing an
                        // empty track would read as a bar that is simply very short.
                        .then(
                            if (worked && row.selectedCell == index) {
                                Modifier.border(1.2.dp, Color(0x800F172A), cellShape)
                            } else {
                                Modifier
                            },
                        ),
                )
            }
        }
    }
}

@Composable
private fun ChartAxisRow(axis: ChartAxis) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        NumericText(
            text = axis.oldest,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
        NumericText(
            text = axis.middle,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
        NumericText(
            text = axis.newest,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = colors.blueText,
        )
    }
}

@Composable
private fun SourceChipRow(
    label: String,
    chips: ImmutableList<SourceChipPR>,
    onSourceClick: (Int?) -> Unit,
    splitChip: FilterChipPR?,
    onSplitClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (label.isNotBlank()) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = Color(0xFF64748B),
                )
            }
            chips.forEachIndexed { index, chip ->
                val source = (index - 1).takeIf { it >= 0 }
                HistoryPillChip(
                    label = chip.label,
                    selected = chip.selected,
                    onClick = { onSourceClick(source) },
                )
            }
        }

        splitChip?.let {
            HistorySplitToggleChip(
                label = it.label,
                selected = it.selected,
                onClick = onSplitClick,
            )
        }
    }
}

@Composable
private fun MetricChipRow(
    label: String,
    chips: ImmutableList<FilterChipPR>,
    onPick: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (label.isNotBlank()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = Color(0xFF64748B),
            )
        }
        chips.forEachIndexed { index, chip ->
            HistoryPillChip(
                label = chip.label,
                selected = chip.selected,
                onClick = { onPick(index) },
            )
        }
    }
}

@Composable
private fun HistoryPillChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = remember { CircleShape }
    val bg = if (selected) Color(0xFF173D7E) else Color.White
    val border = if (selected) Color(0xFF173D7E) else Color(0xFFE5E7EB)
    val textColor = if (selected) Color.White else Color(0xFF64748B)

    Box(
        modifier = Modifier
            .clip(shape)
            .background(bg)
            .border(HistoryDimens.hairline, border, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = textColor,
            maxLines = 1,
        )
    }
}

@Composable
private fun HistorySplitToggleChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = remember { CircleShape }
    val bg = if (selected) Color(0xFF173D7E) else Color.White
    val border = if (selected) Color(0xFF173D7E) else Color(0xFFE5E7EB)
    val textColor = if (selected) Color.White else Color(0xFF64748B)

    Row(
        modifier = Modifier
            .clip(shape)
            .background(bg)
            .border(HistoryDimens.hairline, border, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.BarChart,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(12.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = textColor,
            maxLines = 1,
        )
    }
}

/**
 * What one month paid, employer by employer, and the total under it.
 *
 * Shown in place of the single detail line whenever the month had more than one employer — which is
 * the only time the sum is worth printing.
 */
@Composable
fun MonthWageBreakdown(
    detail: YearDetailPR,
    month: Int,
    totalLabel: String,
    rialLabel: String,
    toPersianDigits: (String) -> String,
    formatWage: (String) -> String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    // Walked once per opened month, not per frame of a scroll.
    val rows = remember(detail, month) {
        detail.workshops.mapNotNull { shop ->
            shop.months.firstOrNull { it.monthIndex == month }?.let { shop.name to it }
        }
    }
    val total = remember(rows) {
        rows.sumOf { (_, worked) -> worked.wage.toLongOrNull() ?: 0L }
    }

    Column(
        modifier = modifier.fillMaxWidth().padding(top = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        rows.forEach { (name, worked) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textPrimary,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
                NumericText(
                    text = toPersianDigits(worked.days.toString()),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                )
                WageText(
                    amount = formatWage(worked.wage),
                    rialLabel = rialLabel,
                    color = colors.textPrimary,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(HistoryDimens.totalCorner))
                .background(colors.blueBg)
                .padding(horizontal = Spacing.sm, vertical = Spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = totalLabel,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.blueText,
            )
            WageText(
                amount = formatWage(total.toString()),
                rialLabel = rialLabel,
                color = colors.blueText,
            )
        }
    }
}

/** An amount and its unit, with only the digits forced left-to-right. */
@Composable
fun WageText(amount: String, rialLabel: String, color: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(HistoryDimens.wageGap),
        verticalAlignment = Alignment.Bottom,
    ) {
        NumericText(
            text = amount,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = color,
        )
        Text(
            text = rialLabel,
            style = MaterialTheme.typography.labelSmall,
            color = color.copy(alpha = HistoryConstants.RIAL_ALPHA),
        )
    }
}

