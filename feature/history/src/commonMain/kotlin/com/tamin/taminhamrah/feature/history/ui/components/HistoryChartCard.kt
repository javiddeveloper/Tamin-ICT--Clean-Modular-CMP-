package com.tamin.taminhamrah.feature.history.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
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
import com.tamin.taminhamrah.feature.history.ui.model.YearDetailPR
import com.tamin.taminhamrah.ui.components.BarChartItem
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminBarChart
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHistoryButtonEnd
import com.tamin.taminhamrah.ui.theme.TaminHistoryButtonStart
import com.tamin.taminhamrah.ui.theme.TaminHistoryConcurrentBottom
import com.tamin.taminhamrah.ui.theme.TaminHistoryConcurrentTop
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import com.tamin.taminhamrah.feature.history.ui.HistoryDimens
import com.tamin.taminhamrah.feature.history.ui.HistoryConstants

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
    bars: ImmutableList<BarChartItem>,
    onBarClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    dense: Boolean = false,
    axis: ChartAxis? = null,
    sourceChips: ImmutableList<SourceChipPR> = persistentListOf(),
    onSourceClick: (Int?) -> Unit = {},
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

        if (sourceChips.isNotEmpty()) {
            SourceChipRow(chips = sourceChips, onSourceClick = onSourceClick)
        }

        TaminBarChart(
            bars = bars,
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

@Composable
private fun SourceChipRow(
    chips: ImmutableList<SourceChipPR>,
    onSourceClick: (Int?) -> Unit,
) {
    val colors = LocalTaminColors.current
    val selectedBrush = remember {
        Brush.linearGradient(listOf(TaminHistoryButtonStart, TaminHistoryButtonEnd))
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        items(chips.size, key = { it }) { index ->
            val chip = chips[index]
            // The first chip is «همه»; the rest map onto the employer at their own position.
            val source = (index - 1).takeIf { it >= 0 }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(HistoryDimens.pillCorner))
                    .then(
                        if (chip.selected) {
                            Modifier.background(selectedBrush)
                        } else {
                            Modifier.background(colors.bgPage)
                        },
                    )
                    .border(
                        HistoryDimens.hairline,
                        if (chip.selected) TaminHistoryButtonEnd else colors.border,
                        RoundedCornerShape(HistoryDimens.pillCorner),
                    )
                    .clickable { onSourceClick(source) }
                    .padding(horizontal = Spacing.sm, vertical = HistoryDimens.sourceChipPaddingV),
            ) {
                Text(
                    text = chip.label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (chip.selected) Color.White else colors.textSecondary,
                    maxLines = 1,
                )
            }
        }
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

