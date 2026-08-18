package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList

/**
 * One column of a [TaminBarChart].
 *
 * [id] is the bar's identity — it is what a tap reports back, so a caller never has to index into
 * the list it passed and can never open the wrong thing after the list changes underneath.
 */
@Immutable
data class BarChartItem(
    val id: String,
    val label: String,
    val value: Int,
    /** Drawn in the accent color, for the bars the page wants to single out. */
    val highlighted: Boolean = false,
)

private val BarWidth = 26.dp
private val MinBarHeight = 4.dp

/**
 * A row of proportional bars, scrolled horizontally, one tappable per [BarChartItem].
 *
 * Deliberately not a charting library: the app carries no plotting dependency, and a comparison of
 * a few dozen totals is a row of rectangles. The bars are laid out lazily because a long career is
 * forty of them, and a `Row` would compose every one to show six.
 *
 * The tallest bar fills the plot; everything else is drawn against it, so the shape of the series
 * reads at a glance without an axis to interpret.
 */
@Composable
fun TaminBarChart(
    bars: ImmutableList<BarChartItem>,
    onBarClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    plotHeight: Dp = 120.dp,
) {
    val colors = LocalTaminColors.current
    val listState = rememberLazyListState()

    // The scale is a property of the series, not of a frame: recomputing it per redraw would walk
    // every bar on every scroll.
    val maxValue = remember(bars) { bars.maxOfOrNull { it.value }?.coerceAtLeast(1) ?: 1 }

    LazyRow(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.Bottom,
    ) {
        items(bars, key = { it.id }) { bar ->
            BarColumn(
                bar = bar,
                fillFraction = bar.value.toFloat() / maxValue,
                plotHeight = plotHeight,
                barColor = if (bar.highlighted) colors.greenText else colors.blueText,
                labelColor = colors.textSecondary,
                onClick = { onBarClick(bar.id) },
            )
        }
    }
}

@Composable
private fun BarColumn(
    bar: BarChartItem,
    fillFraction: Float,
    plotHeight: Dp,
    barColor: Color,
    labelColor: Color,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(BarWidth + Spacing.md)
            .clip(RoundedCornerShape(CornerRadius.chip))
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Box(
            modifier = Modifier
                .width(BarWidth)
                // A year with a handful of days still has to be visible and tappable, so the bar
                // has a floor rather than collapsing to nothing.
                .height((plotHeight * fillFraction).coerceAtLeast(MinBarHeight))
                .clip(RoundedCornerShape(topStart = CornerRadius.chip, topEnd = CornerRadius.chip))
                .background(barColor),
        )
        NumericText(
            text = bar.label,
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
            modifier = Modifier.padding(top = Spacing.xs),
        )
    }
}
