package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarTrack
import com.tamin.taminhamrah.ui.theme.TaminHistoryPillBg
import kotlinx.collections.immutable.ImmutableList

/**
 * One column of a [TaminBarChart].
 *
 * [id] is the bar's identity — it is what a tap reports back, so a caller never indexes into the
 * list it passed and can never open the wrong thing after the list changes underneath.
 *
 * Colours arrive as [Color]s rather than as a `Brush`: a brush is not a stable type, and one on this
 * model would make every bar unskippable. The gradient is built from them inside the bar.
 */
@Immutable
data class BarChartItem(
    val id: String,
    val label: String,
    /** How much of the plot this bar fills, 0f..1f. */
    val fraction: Float,
    val fillTop: Color,
    val fillBottom: Color,
    val labelColor: Color,
    /** Painted across the top of the fill — the design marks overlapping employment this way. */
    val capTop: Color? = null,
    val capBottom: Color? = null,
    /** Shown in a bubble above the bar while it is the selected one. */
    val pill: String? = null,
    val labelBold: Boolean = false,
    /** A bar with nothing behind it: still drawn, but it does not answer a tap. */
    val enabled: Boolean = true,
)

/**
 * A row of proportional bars, each tappable, sized to the width it is given.
 *
 * Deliberately not a charting library: the app carries no plotting dependency and a comparison of a
 * few dozen totals is a row of rectangles. Every bar shares the plot's height and shows its own
 * share of it, so the shape of a career reads without an axis to interpret.
 *
 * [dense] is for a series too long to label every bar — the columns narrow, the corners tighten and
 * the caller draws its own axis instead.
 */
@Composable
fun TaminBarChart(
    bars: ImmutableList<BarChartItem>,
    onBarClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    plotHeight: Dp = PlotHeight,
    dense: Boolean = false,
    showLabels: Boolean = true,
) {
    val gap = if (dense) DenseGap else Gap
    val corner = if (dense) DenseCorner else Corner

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(plotHeight),
            horizontalArrangement = Arrangement.spacedBy(gap),
            verticalAlignment = Alignment.Bottom,
        ) {
            bars.forEach { bar ->
                Bar(
                    bar = bar,
                    corner = corner,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    onClick = { onBarClick(bar.id) },
                )
            }
        }

        if (showLabels) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = LabelGap),
                horizontalArrangement = Arrangement.spacedBy(gap),
            ) {
                bars.forEach { bar ->
                    Text(
                        text = bar.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (bar.labelBold) FontWeight.ExtraBold else FontWeight.SemiBold,
                        color = bar.labelColor,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun Bar(
    bar: BarChartItem,
    corner: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Built here from the two stable colours, and only when they change.
    val fill = remember(bar.fillTop, bar.fillBottom) {
        Brush.verticalGradient(listOf(bar.fillTop, bar.fillBottom))
    }
    val cap = remember(bar.capTop, bar.capBottom) {
        val top = bar.capTop
        val bottom = bar.capBottom
        if (top != null && bottom != null) Brush.verticalGradient(listOf(top, bottom)) else null
    }

    Column(
        modifier = modifier.clickable(enabled = bar.enabled, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // A fixed lane above the plot, so a bubble appearing never shifts the bars under it.
        Box(
            modifier = Modifier.height(PillLane).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            bar.pill?.let { PillLabel(text = it) }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(corner))
                .background(TaminHistoryBarTrack),
            contentAlignment = Alignment.BottomCenter,
        ) {
            // fillMaxHeight(fraction) rather than a measured Dp: the plot's height is whatever the
            // row gives it, and the bar takes its share at layout time.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(bar.fraction.coerceIn(MinFraction, 1f))
                    .clip(RoundedCornerShape(corner))
                    .background(fill),
                contentAlignment = Alignment.TopCenter,
            ) {
                cap?.let {
                    Box(modifier = Modifier.fillMaxWidth().height(CapHeight).background(it))
                }
            }
        }
    }
}

@Composable
private fun PillLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.ExtraBold,
        color = Color.White,
        maxLines = 1,
        modifier = Modifier
            .clip(RoundedCornerShape(PillCorner))
            .background(TaminHistoryPillBg)
            .padding(horizontal = Spacing.sm, vertical = PillPadding),
    )
}

private val PlotHeight = 164.dp
private val PillLane = 30.dp
private val Gap = 5.dp
private val DenseGap = 2.dp
private val Corner = 9.dp
private val DenseCorner = 4.dp
private val LabelGap = 7.dp
private val CapHeight = 7.dp
private val PillCorner = 100.dp
private val PillPadding = 4.dp

/** A bar with no days still has to be visible and tappable. */
private const val MinFraction = 0.04f
