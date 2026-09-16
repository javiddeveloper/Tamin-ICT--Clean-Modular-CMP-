package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Duration
import com.tamin.taminhamrah.ui.theme.Easing
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_tab_active
import taminx.core.core_ui.assigner_tab_finished
import kotlin.math.roundToInt

/** The gradient tab height `EmployerInfoHero` already draws its own two tabs at. */
private val SegmentHeight = 40.dp
private val StripShape = RoundedCornerShape(CornerRadius.xl)
private val SegmentShape = RoundedCornerShape(CornerRadius.lg)

/**
 * A few mutually exclusive options as one strip, the chosen one lifted onto the brand gradient.
 *
 * Generic over the option type so each caller keeps its own enum as the one table of order and
 * copy. Hand it that enum's entries hoisted into a single [ImmutableList]: a list built at the call
 * site is a new instance on every recomposition, and the strip could never skip.
 *
 * The gradient pill slides between options instead of jumping. Its position is read inside
 * `layout {}`, so a frame of the slide relays out the pill alone and recomposes nothing; it is placed
 * with `placeRelative`, so on an RTL page the first option sits rightmost and the pill travels left
 * with no mirroring here.
 */
@Composable
fun <T> TaminSegmentedTabs(
    options: ImmutableList<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: @Composable (T) -> String,
    modifier: Modifier = Modifier,
    /**
     * A short count beside each option's label — «جاری ۶». Null, the default, draws the labels
     * alone.
     */
    badge: (@Composable (T) -> String)? = null,
) {
    val colors = LocalTaminColors.current
    val position by animateFloatAsState(
        targetValue = options.indexOf(selected).coerceAtLeast(0).toFloat(),
        animationSpec = tween(durationMillis = Duration.normal, easing = Easing.standard),
        label = "segmentedTabsPill",
    )
    val count = options.size.coerceAtLeast(1)

    Box(
        modifier = modifier
            .clip(StripShape)
            .background(colors.bgPage)
            .border(Thickness.border, colors.border, StripShape)
            .padding(Spacing.xs),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .layout { measurable, constraints ->
                    // Only the width is pinned. An intrinsic query — a parent's
                    // `height(IntrinsicSize.Min)` — arrives with an unbounded height, and fixing
                    // the pill to that height throws; the height passes through untouched instead,
                    // which in the real pass is the strip's own, set by `matchParentSize`.
                    val width = if (constraints.hasBoundedWidth) constraints.maxWidth else 0
                    val gap = Spacing.xs.roundToPx()
                    val segment = ((width - gap * (count - 1)) / count).coerceAtLeast(0)
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
            modifier = Modifier.fillMaxWidth().selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            options.forEach { option ->
                val isSelected = option == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(SegmentHeight)
                        .clip(SegmentShape)
                        .selectable(
                            selected = isSelected,
                            role = Role.Tab,
                            onClick = { onSelect(option) },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    val textColor = if (isSelected) colors.onGradient else colors.textSecondary
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = label(option),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        if (badge != null) {
                            Text(
                                text = badge(option),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = textColor,
                                maxLines = 1,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isSelected) colors.glassIconTileBg else colors.border)
                                    .padding(horizontal = Spacing.xs, vertical = Spacing.xxs),
                            )
                        }
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------------------------- previews

private enum class PreviewTab { Active, Finished }

private val PreviewTabs = persistentListOf(PreviewTab.Active, PreviewTab.Finished)

@PreviewRtlTheme
@Composable
private fun TaminSegmentedTabsPreview() = PreviewRtlThemeContent {
    TaminSegmentedTabs(
        options = PreviewTabs,
        selected = PreviewTab.Finished,
        onSelect = {},
        label = {
            stringResource(
                if (it == PreviewTab.Active) Res.string.assigner_tab_active else Res.string.assigner_tab_finished
            )
        },
    )
}
