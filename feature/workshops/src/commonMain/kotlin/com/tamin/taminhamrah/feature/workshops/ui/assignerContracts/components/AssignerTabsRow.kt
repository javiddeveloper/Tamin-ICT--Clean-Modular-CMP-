package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractTab
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Duration
import com.tamin.taminhamrah.ui.theme.Easing
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_count_label
import kotlin.math.roundToInt

private val OuterStripShape = RoundedCornerShape(CornerRadius.xl)
private val SegmentShape = RoundedCornerShape(CornerRadius.lg)
private val SegmentHeight = 40.dp

/**
 * Combined filter strip for assigner contracts: جاری / خاتمه‌یافته tabs alongside a total count
 * tile («واگذارنده») wrapped in a single unified pill container.
 *
 * Designed specifically for the Assigner Contracts feature without altering shared core components.
 */
@Composable
fun AssignerTabsRow(
    selected: AssignerContractTab,
    count: Int,
    activeCount: Int,
    finishedCount: Int,
    onSelect: (AssignerContractTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val position by animateFloatAsState(
        targetValue = AssignerContractTab.all.indexOf(selected).coerceAtLeast(0).toFloat(),
        animationSpec = tween(durationMillis = Duration.normal, easing = Easing.standard),
        label = "assignerTabsPill",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(OuterStripShape)
            .background(colors.bgPage)
            .border(Thickness.border, colors.border, OuterStripShape)
            .padding(Spacing.xs),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Segmented tabs (جاری / خاتمه‌یافته) taking remaining width
            Box(
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .layout { measurable, constraints ->
                            val width = if (constraints.hasBoundedWidth) constraints.maxWidth else 0
                            val gap = Spacing.xs.roundToPx()
                            val countTabs = AssignerContractTab.all.size.coerceAtLeast(1)
                            val segment = ((width - gap * (countTabs - 1)) / countTabs).coerceAtLeast(0)
                            val pill = measurable.measure(
                                constraints.copy(minWidth = segment, maxWidth = segment),
                            )
                            layout(width, pill.height) {
                                pill.placeRelative(x = ((segment + gap) * position).roundToInt(), y = 0)
                            }
                        }
                        .clip(SegmentShape)
                        .background(colors.buttonGradient),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    AssignerContractTab.all.forEach { tab ->
                        val isSelected = tab == selected
                        val textColor = if (isSelected) colors.onGradient else colors.textSecondary
                        val badgeBg = if (isSelected) colors.glassIconTileBg else colors.border
                        val badgeText = (if (tab == AssignerContractTab.ACTIVE) activeCount else finishedCount)
                            .toString()
                            .toPersianDigits()

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(SegmentHeight)
                                .clip(SegmentShape)
                                .selectable(
                                    selected = isSelected,
                                    role = Role.Tab,
                                    onClick = { onSelect(tab) },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(tab.label),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = badgeText,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textColor,
                                    maxLines = 1,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(badgeBg)
                                        .padding(horizontal = Spacing.xs, vertical = Spacing.xxs),
                                )
                            }
                        }
                    }
                }
            }

            // Total count tile («واگذارنده»)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(SegmentShape)
                    .background(colors.blueBg)
                    .border(Thickness.border, colors.blueBorder, SegmentShape)
                    .padding(horizontal = Spacing.md),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = count.toString().toPersianDigits(),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                        ),
                        fontWeight = FontWeight.Bold,
                        color = colors.blueText,
                        maxLines = 1,
                    )
                    Text(
                        text = stringResource(Res.string.assigner_count_label),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            lineHeight = 13.sp,
                        ),
                        fontWeight = FontWeight.Medium,
                        color = colors.blueText,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

// ------------------------------------------------------------------------------- previews

@PreviewRtlTheme
@Composable
private fun AssignerTabsRowActivePreview() = PreviewRtlThemeContent {
    AssignerTabsRow(
        selected = AssignerContractTab.ACTIVE,
        count = 9,
        activeCount = 6,
        finishedCount = 3,
        onSelect = {},
    )
}

@PreviewRtlTheme
@Composable
private fun AssignerTabsRowFinishedPreview() = PreviewRtlThemeContent {
    AssignerTabsRow(
        selected = AssignerContractTab.FINISHED,
        count = 9,
        activeCount = 6,
        finishedCount = 3,
        onSelect = {},
    )
}
