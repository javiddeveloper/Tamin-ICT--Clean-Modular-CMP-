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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.history.ui.HistoryDimens
import com.tamin.taminhamrah.ui.components.collapseAway
import com.tamin.taminhamrah.ui.components.collapseHeightAway
import com.tamin.taminhamrah.ui.components.collapsingBottomPadding
import com.tamin.taminhamrah.ui.components.scaleOnCollapse
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationCardBgEnd
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationCardBgStart
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationCardBorder
import com.tamin.taminhamrah.ui.theme.TaminHistoryDurationFigureLeast
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
                .collapsingBottomPadding(
                    progress = collapseProgress,
                    expanded = HistoryDimens.durationCardPaddingV,
                    collapsed = HistoryDimens.durationCardPaddingVCollapsed,
                )
                .padding(
                    start = HistoryDimens.durationCardPaddingH,
                    end = HistoryDimens.durationCardPaddingH,
                    top = HistoryDimens.durationCardPaddingV,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Older step button
            Box(modifier = Modifier.collapseAway(collapseProgress)) {
                NavStepButton(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
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

                // Digits row
                Row(
                    modifier = Modifier
                        .collapsingBottomPadding(
                            progress = collapseProgress,
                            expanded = HistoryDimens.durationFiguresGap,
                            collapsed = 0.dp,
                        )
                        // What survives the fold: the person's own record, in one line, smaller.
                        // One modifier measures the line unbounded (so «۱۷» keeps its ۷), scales it
                        // as the head folds, and reports the scaled size so the card closes with it.
                        .scaleOnCollapse(
                            progress = collapseProgress,
                            minScale = HistoryDimens.durationFiguresCollapsedScale,
                            rtl = true,
                        ),
                    // One baseline for all three figures and their units — aligning on the bottom
                    // edge instead left each piece sitting at its own height.
                    horizontalArrangement = Arrangement.Center,
                ) {
                    // Part 1 (Years / Main)
                    Text(
                        text = model.part1.number,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = HistoryDimens.durationTextLarge,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-1).sp,
                            lineHeight = HistoryDimens.durationTextLarge,
                        ),
                        color = TaminHistoryDurationNavIcon,
                        modifier = Modifier.alignByBaseline(),
                    )
                    if (model.part1.unit.isNotBlank()) {
                        Text(
                            text = model.part1.unit,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = HistoryDimens.durationUnitLarge,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = TaminHistoryDurationUnit,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.alignByBaseline().padding(start = 4.dp),
                        )
                    }

                    // Part 2 (Months)
                    model.part2?.let { p2 ->
                        Text(
                            text = "·",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = HistoryDimens.durationSeparator,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = TaminHistoryDurationNavBorder,
                            modifier = Modifier.alignByBaseline().padding(horizontal = 5.dp),
                        )
                        Text(
                            text = p2.number,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = HistoryDimens.durationTextMedium,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp,
                                lineHeight = HistoryDimens.durationTextMedium,
                            ),
                            color = TaminHistoryDurationFigureMinor,
                            modifier = Modifier.alignByBaseline(),
                        )
                        if (p2.unit.isNotBlank()) {
                            Text(
                                text = p2.unit,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = HistoryDimens.durationUnitMedium,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = TaminHistoryDurationUnit,
                                maxLines = 1,
                                softWrap = false,
                            modifier = Modifier.alignByBaseline().padding(start = 4.dp),
                            )
                        }
                    }

                    // Part 3 (Days)
                    model.part3?.let { p3 ->
                        Text(
                            text = "·",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = HistoryDimens.durationSeparator,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = TaminHistoryDurationNavBorder,
                            modifier = Modifier.alignByBaseline().padding(horizontal = 5.dp),
                        )
                        Text(
                            text = p3.number,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = HistoryDimens.durationTextSmall,
                                fontWeight = FontWeight.Bold,
                                lineHeight = HistoryDimens.durationTextSmall,
                            ),
                            color = TaminHistoryDurationFigureLeast,
                            modifier = Modifier.alignByBaseline(),
                        )
                        if (p3.unit.isNotBlank()) {
                            Text(
                                text = p3.unit,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = HistoryDimens.durationUnitSmall,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                                color = TaminHistoryDurationUnit,
                                maxLines = 1,
                                softWrap = false,
                            modifier = Modifier.alignByBaseline().padding(start = 4.dp),
                            )
                        }
                    }
                }
            }

            // Newer step button
            Box(modifier = Modifier.collapseAway(collapseProgress)) {
                NavStepButton(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
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
