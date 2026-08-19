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
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.layout.layout
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.ui.draw.drawBehind
import com.tamin.taminhamrah.ui.theme.Duration
import com.tamin.taminhamrah.ui.theme.Easing
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

/**
 * One column of a [TaminBarChart].
 *
 * [id] is the bar's identity — it is what a tap reports back, so a caller never indexes into the
 * list it passed and can never open the wrong thing after the list changes underneath.
 *
 * Colors arrive as [Color]s rather than as a `Brush`: a brush is not a stable type, and one on this
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
    /**
     * Turn the labels on their side.
     *
     * For a series whose names are longer than one bar is wide — twelve Jalali months in a phone's
     * width — where the alternative is clipping every one of them to three letters.
     */
    rotateLabels: Boolean = false,
    labelLaneHeight: Dp = RotatedLabelLane,
    /**
     * What identifies the series, for the growth animation.
     *
     * The bars rise again whenever this changes, and only then — pass what makes it a *different*
     * series (the year being shown, a filter), never the bars themselves. Keyed on the list, every
     * selection would replay the whole chart, because selecting a bar changes its own item.
     */
    animationKey: Any? = null,
) {
    val gap = if (dense) DenseGap else Gap
    val corner = if (dense) DenseCorner else Corner
    val growth = rememberBarGrowth(barCount = bars.size, key = animationKey)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(plotHeight),
            horizontalArrangement = Arrangement.spacedBy(gap),
            verticalAlignment = Alignment.Bottom,
        ) {
            bars.forEachIndexed { index, bar ->
                Bar(
                    bar = bar,
                    corner = corner,
                    // A lambda, not a value: the bar reads it while it lays itself out, so a frame
                    // of growth costs no recomposition here.
                    progress = growth.progressOf(index),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    onClick = { onBarClick(bar.id) },
                )
            }
        }

        if (showLabels) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = LabelGap)
                    .then(if (rotateLabels) Modifier.height(labelLaneHeight) else Modifier),
                horizontalArrangement = Arrangement.spacedBy(gap),
                verticalAlignment = if (rotateLabels) Alignment.Top else Alignment.CenterVertically,
            ) {
                bars.forEach { bar ->
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        Text(
                            text = bar.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (bar.labelBold) {
                                FontWeight.ExtraBold
                            } else {
                                FontWeight.SemiBold
                            },
                            color = bar.labelColor,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            modifier = if (rotateLabels) Modifier.rotateVertically() else Modifier,
                        )
                    }
                }
            }
        }
    }
}

/**
 * How far each bar has risen, and the coroutine that raises them.
 *
 * One holder for the whole series rather than an [Animatable] per bar composable: the bars are
 * re-created on every selection, and per-bar state would be thrown away and restarted with them.
 */
@Stable
class BarGrowthState internal constructor(barCount: Int, initial: Float) {
    private val progress = List(barCount) { Animatable(initial) }

    /** Read inside layout, never during composition. */
    fun progressOf(index: Int): () -> Float = { progress.getOrNull(index)?.value ?: 1f }

    internal suspend fun grow() = coroutineScope {
        progress.forEachIndexed { index, animatable ->
            launch {
                delay((index * GrowStaggerMs).milliseconds)
                animatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(GrowDurationMs, easing = Easing.decelerate),
                )
            }
        }
    }
}

/**
 * The series' growth, restarted whenever [key] changes.
 *
 * With reduced motion the bars start at full height and nothing animates — the same contract
 * `staggeredItemEntrance` honours.
 */
@Composable
fun rememberBarGrowth(barCount: Int, key: Any?): BarGrowthState {
    val reducedMotion = isReducedMotionEnabled()
    val state = remember(barCount, key, reducedMotion) {
        BarGrowthState(barCount, initial = if (reducedMotion) 1f else 0f)
    }
    LaunchedEffect(state) { if (!reducedMotion) state.grow() }
    return state
}

@Composable
private fun Bar(
    bar: BarChartItem,
    corner: Dp,
    progress: () -> Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Selection recolors the fill rather than replacing it. Held as State and read in the draw
    // lambda below, so the transition runs without recomposing the bar.
    val fillTop = animateColorAsState(bar.fillTop, tween(Duration.fast), label = "barFillTop")
    val fillBottom = animateColorAsState(bar.fillBottom, tween(Duration.fast), label = "barFillBottom")

    val cap = remember(bar.capTop, bar.capBottom) {
        val top = bar.capTop
        val bottom = bar.capBottom
        if (top != null && bottom != null) Brush.verticalGradient(listOf(top, bottom)) else null
    }

    Column(
        modifier = modifier.clickable(enabled = bar.enabled, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PillLane(pill = bar.pill)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(corner))
                .background(TaminHistoryBarTrack),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .growTo(bar.fraction.coerceIn(MinFraction, 1f), progress)
                    .clip(RoundedCornerShape(corner))
                    .drawBehind {
                        drawRect(
                            Brush.verticalGradient(
                                listOf(fillTop.value, fillBottom.value),
                                startY = 0f,
                                endY = size.height,
                            ),
                        )
                    },
                contentAlignment = Alignment.TopCenter,
            ) {
                cap?.let {
                    Box(modifier = Modifier.fillMaxWidth().height(CapHeight).background(it))
                }
            }
        }
    }
}

/**
 * The fixed lane above the plot.
 *
 * Its own composable so the plain `AnimatedVisibility` resolves: inside the bar's `Column` the
 * `ColumnScope` overload wins, and it is not the one wanted here.
 *
 * The lane keeps its height whether a bubble is in it, so one appearing never shifts the
 * bars beneath. The bubble is wider than its bar and is allowed to overflow — clamped to the
 * column it would be clipped to a single digit.
 */
@Composable
private fun PillLane(pill: String?) {
    Box(
        modifier = Modifier.height(PillLaneHeight).fillMaxWidth().wrapContentWidth(unbounded = true),
        contentAlignment = Alignment.Center,
    ) {
        // A tooltip should arrive rather than blink: it scales up from just under full size as it
        // fades in, and leaves the same way.
        AnimatedVisibility(
            visible = pill != null,
            enter = fadeIn(tween(PillDurationMs)) +
                scaleIn(tween(PillDurationMs), initialScale = PillInitialScale),
            exit = fadeOut(tween(PillDurationMs)) +
                scaleOut(tween(PillDurationMs), targetScale = PillInitialScale),
        ) {
            // Held across the exit so the bubble fades out with its text intact rather than
            // emptying first.
            val text = remember(pill) { pill }
            text?.let { PillLabel(text = it) }
        }
    }
}

/**
 * Takes [fraction] of the height available, scaled by [progress].
 *
 * A layout modifier and not `fillMaxHeight(animatedFraction)`, which would recompose every frame,
 * and not `graphicsLayer { scaleY }`, which would squash the corner radius on the way up. Reading
 * [progress] inside the measure lambda keeps a frame of animation to the layout phase.
 */
private fun Modifier.growTo(fraction: Float, progress: () -> Float): Modifier = layout {
    measurable, constraints ->
    val full = (constraints.maxHeight * fraction).roundToInt()
    val height = (full * progress().coerceIn(0f, 1f)).roundToInt().coerceAtMost(constraints.maxHeight)
    val placeable = measurable.measure(
        constraints.copy(minHeight = height, maxHeight = height),
    )
    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
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

/**
 * Turns a label on its side, occupying the space it actually needs afterward.
 *
 * Rotating alone would leave the text laid out at its horizontal size and overlapping its
 * neighbors, so the measurement is swapped first and the rotation applied after.
 */
private fun Modifier.rotateVertically(): Modifier = this
    .layout { measurable, constraints ->
        val placeable = measurable.measure(
            constraints.copy(
                minWidth = constraints.minHeight,
                maxWidth = constraints.maxHeight,
                minHeight = constraints.minWidth,
                maxHeight = constraints.maxWidth,
            ),
        )
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2),
            )
        }
    }
    .graphicsLayer { rotationZ = QuarterTurn }

/** Counter-clockwise, so a Persian label reads upward rather than upside down. */
private const val QuarterTurn = -90f

/** Room for a rotated month name. */
private val RotatedLabelLane = 62.dp

/** The design's own rise: long enough to read as growth, short enough not to be waited on. */
private const val GrowDurationMs = 400
private const val GrowStaggerMs = 25L

private const val PillDurationMs = 120
private const val PillInitialScale = 0.85f

private val PlotHeight = 164.dp
private val PillLaneHeight = 30.dp
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
