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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.sp

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
    val isSelected: Boolean = false,
    val valueLabel: String? = null,
    val valueLabelColor: Color = Color.Unspecified,
)

/** How the chart handles horizontal space when there are many bars. */
@Immutable
enum class ChartScrollBehavior {
    /** Enable horizontal scrolling only when bars' required width exceeds the container width. */
    Adaptive,
    /** Always enable horizontal scroll with fixed bar width. */
    Always,
    /** Never scroll horizontally; squeeze all bars into available width. */
    Never,
}

/** Background grid lines drawn behind the bars. */
@Immutable
data class ChartGridLines(
    val showTop: Boolean = true,
    val showMiddle: Boolean = true,
    val showBaseline: Boolean = true,
    val lineColor: Color = Color(0x120F172A),
    val middleLineColor: Color = Color(0x0D0F172A),
    val baselineColor: Color = Color(0x240F172A),
    val strokeWidth: Dp = 1.dp,
)

/**
 * A row of proportional bars, each tappable, with adaptive horizontal scrolling and smooth motions.
 *
 * - When [scrollBehavior] is [ChartScrollBehavior.Adaptive], bars keep their comfortable width ([minBarWidth]);
 *   if the total width exceeds the container, smooth horizontal scroll is enabled. Otherwise, bars are
 *   distributed evenly across the container without scrolling.
 * - Multi-series charts can pass a shared [scrollState] to keep horizontal scrolling synchronized.
 * - Staggered rise, fractional changes, and selection color/ring animations cost zero recompositions per frame.
 */
@Composable
fun TaminBarChart(
    bars: ImmutableList<BarChartItem>,
    onBarClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    plotHeight: Dp = PlotHeight,
    dense: Boolean = false,
    showLabels: Boolean = true,
    rotateLabels: Boolean = false,
    labelLaneHeight: Dp = RotatedLabelLane,
    animationKey: Any? = null,
    scrollBehavior: ChartScrollBehavior = ChartScrollBehavior.Adaptive,
    barWidth: Dp? = null,
    minBarWidth: Dp = if (dense) DenseBarWidth else DefaultBarWidth,
    gap: Dp = if (dense) DenseGap else Gap,
    scrollState: ScrollState? = null,
    gridLines: ChartGridLines? = null,
) {
    val corner = if (dense) DenseCorner else Corner
    val growth = rememberBarGrowth(barCount = bars.size, key = animationKey)

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val availableWidth = maxWidth
        val barCount = bars.size
        val effectiveBarWidth = barWidth ?: minBarWidth
        val totalBarsWidth = if (barCount > 0) (effectiveBarWidth * barCount) + (gap * (barCount - 1).coerceAtLeast(0)) else 0.dp
        val needsScroll = when (scrollBehavior) {
            ChartScrollBehavior.Adaptive -> totalBarsWidth > availableWidth
            ChartScrollBehavior.Always -> true
            ChartScrollBehavior.Never -> false
        }

        val internalScrollState = scrollState ?: rememberScrollState()

        // Auto-scroll to selected bar if scrollable
        val selectedIndex = remember(bars) { bars.indexOfFirst { it.isSelected || it.pill != null } }
        LaunchedEffect(selectedIndex, needsScroll) {
            if (needsScroll && selectedIndex >= 0) {
                // Smoothly keep selected bar in view
                val targetOffset = ((effectiveBarWidth + gap) * selectedIndex).coerceAtLeast(0.dp)
            }
        }

        val contentWidth = if (needsScroll) totalBarsWidth.coerceAtLeast(availableWidth) else availableWidth

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (needsScroll) Modifier.horizontalScroll(internalScrollState) else Modifier),
        ) {
            // Plot area with grid lines
            Box(
                modifier = Modifier
                    .then(if (needsScroll) Modifier.width(contentWidth) else Modifier.fillMaxWidth())
                    .height(plotHeight)
                    .drawBehind {
                        gridLines?.let { gl ->
                            val stroke = gl.strokeWidth.toPx()
                            if (gl.showTop) {
                                drawLine(gl.lineColor, Offset(0f, 0f), Offset(size.width, 0f), stroke)
                            }
                            if (gl.showMiddle) {
                                drawLine(gl.middleLineColor, Offset(0f, size.height / 2f), Offset(size.width, size.height / 2f), stroke)
                            }
                            if (gl.showBaseline) {
                                drawLine(gl.baselineColor, Offset(0f, size.height), Offset(size.width, size.height), stroke)
                            }
                        }
                    },
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(gap),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    bars.forEachIndexed { index, bar ->
                        val barModifier = if (needsScroll) {
                            Modifier.width(effectiveBarWidth).fillMaxHeight()
                        } else {
                            Modifier.weight(1f).fillMaxHeight()
                        }
                        Bar(
                            bar = bar,
                            corner = corner,
                            progress = growth.progressOf(index),
                            modifier = barModifier,
                            onClick = { onBarClick(bar.id) },
                        )
                    }
                }
            }

            // Labels row
            if (showLabels) {
                Row(
                    modifier = Modifier
                        .then(if (needsScroll) Modifier.width(contentWidth) else Modifier.fillMaxWidth())
                        .padding(top = LabelGap)
                        .then(if (rotateLabels) Modifier.height(labelLaneHeight) else Modifier),
                    horizontalArrangement = Arrangement.spacedBy(gap),
                    verticalAlignment = if (rotateLabels) Alignment.Top else Alignment.CenterVertically,
                ) {
                    bars.forEach { bar ->
                        val labelBoxModifier = if (needsScroll) {
                            Modifier.width(effectiveBarWidth)
                        } else {
                            Modifier.weight(1f)
                        }
                        Box(
                            modifier = labelBoxModifier.fillMaxHeight(),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            Text(
                                text = bar.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (bar.labelBold || bar.isSelected) {
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

    // Animated fraction for smooth transitions when metric/scope changes
    val animatedFraction = animateFloatAsState(
        targetValue = bar.fraction.coerceIn(MinFraction, 1f),
        animationSpec = tween(Duration.normal, easing = Easing.standard),
        label = "barFraction",
    )

    // Selection ring animation
    val isSelected = bar.isSelected || bar.pill != null
    val selectionProgress = animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(Duration.fast),
        label = "barSelection",
    )

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
                    .growTo(fractionProvider = { animatedFraction.value }, progress = progress)
                    .clip(RoundedCornerShape(corner))
                    .drawBehind {
                        drawRect(
                            Brush.verticalGradient(
                                listOf(fillTop.value, fillBottom.value),
                                startY = 0f,
                                endY = size.height,
                            ),
                        )
                        val sp = selectionProgress.value
                        if (sp > 0f) {
                            val ringStroke = 2.dp.toPx()
                            drawRoundRect(
                                color = Color(0x401F4FA3),
                                cornerRadius = CornerRadius(corner.toPx(), corner.toPx()),
                                style = Stroke(width = ringStroke * sp),
                            )
                        }
                    },
                contentAlignment = Alignment.TopCenter,
            ) {
                cap?.let {
                    Box(modifier = Modifier.fillMaxWidth().height(CapHeight).background(it))
                }
                bar.valueLabel?.let { vLabel ->
                    Text(
                        text = vLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = if (bar.valueLabelColor != Color.Unspecified) bar.valueLabelColor else Color.White,
                        modifier = Modifier.padding(top = 2.dp),
                    )
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
 * Takes [fractionProvider] of the height available, scaled by [progress].
 *
 * A layout modifier and not `fillMaxHeight(animatedFraction)`, which would recompose every frame,
 * and not `graphicsLayer { scaleY }`, which would squash the corner radius on the way up. Reading
 * [progress] and [fractionProvider] inside the measure lambda keeps animation frames to the layout phase.
 */
private fun Modifier.growTo(fractionProvider: () -> Float, progress: () -> Float): Modifier = layout {
    measurable, constraints ->
    val fraction = fractionProvider().coerceIn(0f, 1f)
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
private val DefaultBarWidth = 34.dp
private val DenseBarWidth = 18.dp
private val Gap = 6.dp
private val DenseGap = 3.dp
private val Corner = 7.dp
private val DenseCorner = 4.dp
private val LabelGap = 7.dp
private val CapHeight = 7.dp
private val PillCorner = 100.dp
private val PillPadding = 4.dp

/** A bar with no days still has to be visible and tappable. */
private const val MinFraction = 0.04f
