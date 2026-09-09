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
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.history.ui.HistoryDimens
import com.tamin.taminhamrah.ui.components.collapseAway
import com.tamin.taminhamrah.ui.components.collapseHeightAway
import com.tamin.taminhamrah.ui.components.collapsingBottomPadding
import com.tamin.taminhamrah.ui.components.collapsingVerticalPadding
import com.tamin.taminhamrah.ui.components.scaleOnCollapse
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationCardBgEnd
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationCardBgStart
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationCardBorder
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationFigureLeast
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationFigureMajor
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationFigureMinor
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationNavBg
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationNavBorder
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationNavIcon
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationShadow
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationStripeEnd
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationStripeStart
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationUnit
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
    val cardShape = remember { RoundedCornerShape(HistoryDimens.durationCardCorner) }
    val cardBg = remember {
        Brush.linearGradient(listOf(TaminHistoryDurationCardBgStart, TaminHistoryDurationCardBgEnd))
    }
    val stripeBrush = remember {
        Brush.horizontalGradient(listOf(TaminHistoryDurationStripeStart, TaminHistoryDurationStripeEnd))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 14.dp, shape = cardShape, spotColor = TaminHistoryDurationShadow)
            .clip(cardShape)
            .background(cardBg)
            .border(HistoryDimens.hairline, TaminHistoryDurationCardBorder, cardShape),
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
                        .background(TaminHistoryDurationNavBg)
                        .border(HistoryDimens.hairline, TaminHistoryDurationNavBorder, CircleShape)
                        .padding(horizontal = 11.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = model.pillText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = TaminHistoryDurationNavIcon,
                    )
                }

                DurationFigures(
                    model = model,
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
    val alpha = if (enabled) 1f else 0.3f
    Box(
        modifier = Modifier
            .size(HistoryDimens.durationNavSize)
            .clip(CircleShape)
            .background(TaminHistoryDurationNavBg.copy(alpha = alpha))
            .border(HistoryDimens.hairline, TaminHistoryDurationNavBorder.copy(alpha = alpha), CircleShape)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = TaminHistoryDurationNavIcon.copy(alpha = alpha),
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
private fun DurationFigures(model: DurationCardPR, modifier: Modifier = Modifier) {
    val figureLarge = MaterialTheme.typography.headlineLarge.copy(
        fontSize = HistoryDimens.durationTextLarge,
        fontWeight = FontWeight.ExtraBold,
        lineHeight = HistoryDimens.durationTextLarge,
    )
    val figureMedium = MaterialTheme.typography.headlineMedium.copy(
        fontSize = HistoryDimens.durationTextMedium,
        fontWeight = FontWeight.ExtraBold,
        lineHeight = HistoryDimens.durationTextMedium,
    )
    val figureSmall = MaterialTheme.typography.titleLarge.copy(
        fontSize = HistoryDimens.durationTextSmall,
        fontWeight = FontWeight.Bold,
        lineHeight = HistoryDimens.durationTextSmall,
    )

    Layout(
        modifier = modifier,
        content = {
            DurationFigure(model.part1, figureLarge, HistoryDimens.durationUnitLarge, TaminHistoryDurationFigureMajor)
            model.part2?.let { part ->
                DurationSeparator()
                DurationFigure(part, figureMedium, HistoryDimens.durationUnitMedium, TaminHistoryDurationFigureMinor)
            }
            model.part3?.let { part ->
                DurationSeparator()
                DurationFigure(part, figureSmall, HistoryDimens.durationUnitSmall, TaminHistoryDurationFigureLeast)
            }
        },
    ) { measurables, _ ->
        // Unbounded, always: a figure is never asked to fit, only ever measured and then placed.
        val placeable = measurables.map { it.measure(Constraints()) }
        val baseline = placeable.maxOf { it[LastBaseline] }
        val below = placeable.maxOf { it.height - it[LastBaseline] }
        layout(placeable.sumOf { it.width }, baseline + below) {
            var x = 0
            placeable.forEach { piece ->
                piece.placeRelative(x, baseline - piece[LastBaseline])
                x += piece.width
            }
        }
    }
}

/** One figure and the unit that follows it, as a single baseline-aligned piece. */
@Composable
private fun DurationFigure(
    part: DurationPart,
    figureStyle: TextStyle,
    unitSize: TextUnit,
    figureColor: Color,
) {
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
                    fontSize = unitSize,
                    fontWeight = FontWeight.Bold,
                ),
                color = TaminHistoryDurationUnit,
                modifier = Modifier.alignByBaseline().padding(start = HistoryDimens.durationUnitGap),
            )
        }
    }
}

/** The «·» the design sets between two figures. */
@Composable
private fun DurationSeparator() {
    Text(
        text = "·",
        style = MaterialTheme.typography.titleMedium.copy(
            fontSize = HistoryDimens.durationSeparator,
            fontWeight = FontWeight.Bold,
        ),
        color = TaminHistoryDurationNavBorder,
        modifier = Modifier.padding(horizontal = HistoryDimens.durationSeparatorGap),
    )
}
