package com.tamin.taminhamrah.feature.history.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import kotlin.math.roundToInt
import com.tamin.taminhamrah.feature.history.ui.HistoryDimens
import com.tamin.taminhamrah.ui.components.collapseAway
import com.tamin.taminhamrah.ui.components.collapseHeightAway
import com.tamin.taminhamrah.ui.components.collapsingBottomPadding
import com.tamin.taminhamrah.ui.components.collapsingVerticalPadding
import com.tamin.taminhamrah.ui.components.scaleOnCollapse
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationStripeEnd
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationStripeStart
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.history_step_newer
import taminx.core.core_ui.history_step_older
import taminx.core.core_ui.Res as CoreRes

/** Holds the pre-formatted digits and units to avoid formatting inside composition. */
@Immutable
data class DurationPart(
    val number: String,
    val unit: String,
)

@Immutable
data class DurationCardPR(
    val pillText: String,
    val part1: DurationPart,
    val part2: DurationPart? = null,
    val part3: DurationPart? = null,
    val hasOlder: Boolean = false,
    val hasNewer: Boolean = false,
)

/**
 * The overlapping career duration card.
 *
 * Positioned below the hero header, with a top gradient stripe, step navigation buttons,
 * scope indicator pill, and prominent multiscale Persian duration figures.
 * Supports smooth collapse on scroll when driven by a CollapsingHeaderState.
 */
@Composable
fun HistoryDurationCard(
    model: DurationCardPR,
    onStepOlder: () -> Unit,
    onStepNewer: () -> Unit,
    modifier: Modifier = Modifier,
    collapseProgress: () -> Float = { 0f },
) {
    val colors = LocalTaminColors.current
    val cardShape = remember { RoundedCornerShape(HistoryDimens.durationCardCorner) }
    val cardBg = remember {
        Brush.linearGradient(listOf(colors.historyCardBgStart, colors.bgSurface))
    }
    val stripeBrush = remember {
        Brush.horizontalGradient(listOf(TaminHistoryDurationStripeStart, TaminHistoryDurationStripeEnd))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 14.dp, shape = cardShape, spotColor = colors.historyCardShadow)
            .clip(cardShape)
            .background(cardBg)
            .border(HistoryDimens.hairline, colors.historyCardBorder, cardShape),
    ) {
        // Top accent line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HistoryDimens.durationStripeHeight)
                .background(stripeBrush),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Both edges close together, so the folded bar keeps its figures centered instead
                // of leaving them against its top with the closed padding all below.
                .collapsingVerticalPadding(
                    progress = collapseProgress,
                    expanded = HistoryDimens.durationCardPaddingV,
                    collapsed = HistoryDimens.durationCardPaddingVCollapsed,
                )
                .padding(horizontal = HistoryDimens.durationCardPaddingH),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // In RTL this is the row's first child, so it is the button on the *right*: the one
            // that steps toward the newer end of the career.
            //
            // The chevrons are the plain, non-mirrored pair on purpose. They point at the physical
            // edge each button sits against, so the pair faces outwards; the `AutoMirrored` pair
            // flips with the reading direction and turns both of them inwards on an RTL page.
            Box(modifier = Modifier.collapseAway(collapseProgress)) {
                NavStepButton(
                    icon = Icons.Filled.ChevronRight,
                    enabled = model.hasNewer,
                    onClick = onStepNewer,
                    contentDescription = stringResource(CoreRes.string.history_step_newer),
                )
            }

            // Center details
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // Scope pill
                Box(
                    modifier = Modifier
                        // Closes upward, giving its height back to the bar.
                        .collapseHeightAway(collapseProgress, rate = 1.4f)
                        .clip(CircleShape)
                        .background(colors.blueBg)
                        .border(HistoryDimens.hairline, colors.historyCardBorder, CircleShape)
                        .padding(horizontal = 11.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = model.pillText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = colors.historyAccent,
                    )
                }

                DurationFigures(
                    model = model,
                    collapseProgress = collapseProgress,
                    modifier = Modifier
                        .collapsingBottomPadding(
                            progress = collapseProgress,
                            expanded = HistoryDimens.durationFiguresGap,
                            collapsed = 0.dp,
                        )
                        // What survives the fold: the person's own record, in one line, smaller.
                        // Measures unbounded, scales as the head folds, and reports the scaled size
                        // so the card closes with it.
                        .scaleOnCollapse(
                            progress = collapseProgress,
                            minScale = HistoryDimens.durationFiguresCollapsedScale,
                            rtl = true,
                        ),
                )
            }

            // The row's last child, so the button on the *left*: it steps back toward the oldest year.
            Box(modifier = Modifier.collapseAway(collapseProgress)) {
                NavStepButton(
                    icon = Icons.Filled.ChevronLeft,
                    enabled = model.hasOlder,
                    onClick = onStepOlder,
                    contentDescription = stringResource(CoreRes.string.history_step_older),
                )
            }
        }
    }
}

@Composable
private fun NavStepButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
) {
    val colors = LocalTaminColors.current
    val alpha = if (enabled) 1f else 0.3f
    Box(
        modifier = Modifier
            .size(HistoryDimens.durationNavSize)
            .clip(CircleShape)
            .background(colors.blueBg.copy(alpha = alpha))
            .border(HistoryDimens.hairline, colors.historyCardBorder.copy(alpha = alpha), CircleShape)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = colors.historyAccent.copy(alpha = alpha),
            modifier = Modifier.size(HistoryDimens.durationNavIconSize),
        )
    }
}

/**
 * «۱۷ سال · ۱۱ ماه · ۳ روز» — one figure assembled from up to eight pieces at three sizes.
 *
 * Laid out here rather than by a `Row` for one reason: every piece must be measured at the width it
 * actually wants. Inside a `Row` each piece is measured against whatever the pieces before it left
 * over, and a two-digit number that does not fit its share does not shrink — it wraps, so ۱۷ is
 * drawn as ۱ above ۷. Capping the lines only turns that into ۱۷ clipped to ۱, which is worse: it
 * silently shows a different number. Measuring each piece with [Constraints] and placing them here
 * makes both impossible.
 *
 * The pieces sit on one shared baseline, which is what makes three different sizes read as a single
 * line rather than as three labels at three heights. Spacing is each piece's own padding, so the
 * placement stays a simple left-to-right walk.
 */
@Composable
private fun DurationFigures(
    model: DurationCardPR,
    modifier: Modifier = Modifier,
    collapseProgress: () -> Float = { 0f },
) {
    val colors = LocalTaminColors.current
    // Every part is composed at the *largest* style and scaled down to its own while the card is
    // open. That is what lets the fold end with all three at one size, which is what the collapsed
    // bar wants: it is a summary, and a summary whose months and days have shrunk to a footnote
    // beside the years is one nobody can read. Composing three styles and swapping them at the end
    // would be the obvious way round, and it would cost a recomposition on every frame of the fold;
    // scaling costs a re-layout of this one line and nothing else.
    val figureStyle = MaterialTheme.typography.headlineLarge.copy(
        fontSize = HistoryDimens.durationTextLarge,
        fontWeight = FontWeight.ExtraBold,
        lineHeight = HistoryDimens.durationTextLarge,
    )
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    // What each part measures against the first one, open. Read in the layout block below, where
    // the fold's progress may be read for free.
    val openRatios = remember {
        floatArrayOf(
            1f,
            HistoryDimens.durationTextMedium.value / HistoryDimens.durationTextLarge.value,
            HistoryDimens.durationTextSmall.value / HistoryDimens.durationTextLarge.value,
        )
    }

    Layout(
        modifier = modifier,
        content = {
            DurationFigure(model.part1, figureStyle, colors.historyAccent)
            model.part2?.let { part ->
                DurationSeparator()
                DurationFigure(part, figureStyle, colors.chipSelectedBg)
            }
            model.part3?.let { part ->
                DurationSeparator()
                DurationFigure(part, figureStyle, colors.historyFigureLeast)
            }
        },
    ) { measurables, _ ->
        // Unbounded, always: a figure is never asked to fit, only ever measured and then placed.
        val pieces = measurables.map { it.measure(Constraints()) }
        val t = collapseProgress().coerceIn(0f, 1f)

        // Children arrive as part1, [separator, part2], [separator, part3] — a separator takes the
        // scale of the part it introduces, so the gap keeps its proportion to what follows it.
        val scales = FloatArray(pieces.size) { index ->
            val part = when (index) {
                0 -> 0
                1, 2 -> 1
                else -> 2
            }
            lerp(openRatios[part], 1f, t)
        }

        val baseline = pieces.indices.maxOf { pieces[it][LastBaseline] * scales[it] }
        val below = pieces.indices.maxOf { (pieces[it].height - pieces[it][LastBaseline]) * scales[it] }
        val width = pieces.indices.sumOf { (pieces[it].width * scales[it]).roundToInt() }

        layout(width, (baseline + below).roundToInt()) {
            var x = 0
            pieces.forEachIndexed { index, piece ->
                val scale = scales[index]
                val pieceBaseline = piece[LastBaseline]
                piece.placeRelativeWithLayer(x, (baseline - pieceBaseline).roundToInt()) {
                    scaleX = scale
                    scaleY = scale
                    // Pivoted on the leading edge and on the piece's own baseline, so scaling a
                    // piece neither drifts it along the line nor lifts it off the shared baseline.
                    transformOrigin = TransformOrigin(
                        pivotFractionX = if (rtl) 1f else 0f,
                        pivotFractionY = if (piece.height == 0) 0f else pieceBaseline.toFloat() / piece.height,
                    )
                }
                x += (piece.width * scale).roundToInt()
            }
        }
    }
}

/**
 * One figure and the unit that follows it, as a single baseline-aligned piece.
 *
 * Both are drawn at the largest size and scaled as a pair by [DurationFigures], so a part keeps the
 * design's proportion between its number and its unit at every point of the fold.
 */
@Composable
private fun DurationFigure(
    part: DurationPart,
    figureStyle: TextStyle,
    figureColor: Color,
) {
    val colors = LocalTaminColors.current
    Row {
        Text(
            text = part.number,
            style = figureStyle,
            color = figureColor,
            modifier = Modifier.alignByBaseline(),
        )
        if (part.unit.isNotBlank()) {
            Text(
                text = part.unit,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = HistoryDimens.durationUnitLarge,
                    fontWeight = FontWeight.Bold,
                ),
                color = colors.textSecondary,
                modifier = Modifier.alignByBaseline().padding(start = HistoryDimens.durationUnitGap),
            )
        }
    }
}

/** The «·» the design sets between two figures. */
@Composable
private fun DurationSeparator() {
    val colors = LocalTaminColors.current
    Text(
        text = "·",
        style = MaterialTheme.typography.titleMedium.copy(
            fontSize = HistoryDimens.durationSeparator,
            fontWeight = FontWeight.Bold,
        ),
        color = colors.historyCardBorder,
        modifier = Modifier.padding(horizontal = HistoryDimens.durationSeparatorGap),
    )
}
