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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.tamin.taminhamrah.feature.history.ui.HistoryConstants
import com.tamin.taminhamrah.feature.history.ui.HistoryDimens
import com.tamin.taminhamrah.feature.history.ui.model.YearDetailPR
import com.tamin.taminhamrah.ui.components.BarChartSeries
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminBarChartGroup
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHistoryButtonEnd
import com.tamin.taminhamrah.ui.theme.TaminHistoryButtonStart
import com.tamin.taminhamrah.ui.theme.TaminHistoryConcurrentBottom
import com.tamin.taminhamrah.ui.theme.TaminHistoryConcurrentTop
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** One filter above the month bars: «همه», or a single employer. */
@Immutable
data class SourceChipPR(val label: String, val selected: Boolean)

/**
 * The chart and everything read off it.
 *
 * The card is the same in both scopes — only its title, its bars and what sits under them change —
 * so it is one composable rather than two that would drift apart.
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
    /** The تفکیک کارگاه toggle, or null where there is only one employer to split. */
    splitChip: FilterChipPR? = null,
    onSplitClick: () -> Unit = {},
    metricLabel: String = "",
    metricChips: ImmutableList<FilterChipPR> = persistentListOf(),
    onMetricClick: (Int) -> Unit = {},
    rotateLabels: Boolean = false,
    concurrency: String? = null,
    concurrencyLabel: String = "",
    footer: @Composable ColumnScope.() -> Unit = {},
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HistoryDimens.cardCorner))
            .background(colors.bgSurface)
            .border(HistoryDimens.hairline, colors.border, RoundedCornerShape(HistoryDimens.cardCorner))
            .padding(horizontal = HistoryDimens.cardPaddingH, vertical = HistoryDimens.cardPaddingV),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = colors.textPrimary,
            )
            Text(
                text = hint,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
        }

        if (sourceChips.isNotEmpty() || splitChip != null) {
            SourceChipRow(
                label = sourceLabel,
                chips = sourceChips,
                onSourceClick = onSourceClick,
                splitChip = splitChip,
                onSplitClick = onSplitClick,
            )
        }

        if (metricChips.isNotEmpty()) {
            MetricChipRow(label = metricLabel, chips = metricChips, onPick = onMetricClick)
        }

        TaminBarChartGroup(
            series = series,
            onBarClick = onBarClick,
            dense = dense,
            showLabels = !dense,
            rotateLabels = rotateLabels,
        )

        axis?.let { ChartAxisRow(it) }

        concurrency?.let { note ->
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
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

/** The three points a dense series is read by, instead of a label under every bar. */
@Immutable
data class ChartAxis(val oldest: String, val middle: String, val newest: String)

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

/** One tappable pill: a filter, a series, or a toggle. */
@Immutable
data class FilterChipPR(val label: String, val selected: Boolean)

/**
 * «کارگاه» — which employer the bars count, and whether they are split out per employer.
 *
 * The split toggle sits at the far end of the same row because it answers the same question from
 * the other side: filter *down to* one, or show them all *apart*.
 */
@Composable
private fun SourceChipRow(
    label: String,
    chips: ImmutableList<SourceChipPR>,
    onSourceClick: (Int?) -> Unit,
    splitChip: FilterChipPR?,
    onSplitClick: () -> Unit,
) {
    ChipLane(label = label) {
        chips.forEachIndexed { index, chip ->
            // The first chip is «همه»; the rest map onto the employer at their own position.
            val source = (index - 1).takeIf { it >= 0 }
            HistoryPillChip(
                label = chip.label,
                selected = chip.selected,
                onClick = { onSourceClick(source) },
            )
        }
        splitChip?.let {
            HistoryPillChip(label = it.label, selected = it.selected, onClick = onSplitClick)
        }
    }
}

/** «شاخص» — which series the chart plots. */
@Composable
private fun MetricChipRow(
    label: String,
    chips: ImmutableList<FilterChipPR>,
    onPick: (Int) -> Unit,
) {
    ChipLane(label = label) {
        chips.forEachIndexed { index, chip ->
            HistoryPillChip(
                label = chip.label,
                selected = chip.selected,
                onClick = { onPick(index) },
            )
        }
    }
}

/**
 * A named row of pills.
 *
 * A plain scrolling [Row] rather than a `LazyRow`: these lanes hold three or four chips, and a lazy
 * list would add a scroll container and its own item bookkeeping to save composing nothing.
 */
@Composable
private fun ChipLane(label: String, content: @Composable RowScope.() -> Unit) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (label.isNotBlank()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
        }
        content()
    }
}

/**
 * One pill.
 *
 * The selected brush is hoisted to a file-level value: it never varies, and building a [Brush] per
 * chip per recomposition is an allocation on every frame of a chart forming.
 */
@Composable
private fun HistoryPillChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = LocalTaminColors.current
    val shape = remember { RoundedCornerShape(HistoryDimens.pillCorner) }
    Box(
        modifier = Modifier
            .clip(shape)
            .then(
                if (selected) Modifier.background(SelectedChipBrush) else Modifier.background(colors.bgPage),
            )
            .border(
                HistoryDimens.hairline,
                if (selected) TaminHistoryButtonEnd else colors.border,
                shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.sm, vertical = HistoryDimens.sourceChipPaddingV),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.White else colors.textSecondary,
            maxLines = 1,
        )
    }
}

private val SelectedChipBrush =
    Brush.linearGradient(listOf(TaminHistoryButtonStart, TaminHistoryButtonEnd))

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

