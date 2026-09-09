package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList

/**
 * One plotted series inside a [TaminBarChartGroup].
 *
 * [caption] is where a series states the scale it is drawn against. A bar chart without one is a
 * set of proportions of an unstated number — fine when the yardstick is fixed and obvious (a full
 * year of days), misleading when it is the data's own maximum (money, which climbs every year), so
 * a series that scales to itself is expected to say so.
 *
 * [id] identifies the *series*, not its contents: it is what the growth animation restarts on, so
 * it must change when this becomes a different series and stay put when a bar is merely selected.
 */
@Immutable
data class BarChartSeries(
    val id: String,
    val title: String,
    val caption: String,
    val bars: ImmutableList<BarChartItem>,
    val plotHeight: Dp,
)

/**
 * Several bar series stacked under one another, each with its own heading, scale note and height.
 *
 * Two series of the same categories — earnings and days over the same years — are read against each
 * other, so they share a card, a column count and a tap. Drawing them as one chart with two units
 * would be the alternative, and a bar cannot be tall in rials and short in days at once.
 *
 * The bars all answer the same [onBarClick] because they are the same categories: tapping ۱۴۰۴ in
 * either chart means the same thing. Hoist that lambda in the caller — an inline one is a new
 * instance every recomposition and costs the whole group its skippability.
 *
 * Everything animated lives inside [TaminBarChart], which reads its growth in the draw phase, so a
 * frame of a chart forming costs no recomposition here.
 */
@Composable
fun TaminBarChartGroup(
    series: ImmutableList<BarChartSeries>,
    onBarClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    dense: Boolean = false,
    rotateLabels: Boolean = false,
    /** Labels are drawn once, under the last series — the columns are shared. */
    showLabels: Boolean = true,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        series.forEachIndexed { index, plot ->
            BarChartSeriesHeader(title = plot.title, caption = plot.caption)
            TaminBarChart(
                bars = plot.bars,
                onBarClick = onBarClick,
                plotHeight = plot.plotHeight,
                dense = dense,
                // Only the bottom series carries the category names: the columns line up, so
                // repeating them under every plot would say the same thing twice and cost the
                // charts the height to say it.
                showLabels = showLabels && index == series.lastIndex,
                rotateLabels = rotateLabels,
                animationKey = plot.id,
            )
        }
    }
}

/** A series' name, and the scale it is drawn against. */
@Composable
private fun BarChartSeriesHeader(title: String, caption: String) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
        )
        if (caption.isNotBlank()) {
            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
        }
    }
}
