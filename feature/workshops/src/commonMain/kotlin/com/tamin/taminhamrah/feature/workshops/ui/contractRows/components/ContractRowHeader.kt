package com.tamin.taminhamrah.feature.workshops.ui.contractRows.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.contract.ContractRowTab
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.ui.components.TaminSegmentedTabs
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Duration
import com.tamin.taminhamrah.ui.theme.Easing
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_rows_filter_change
import taminx.core.core_ui.contract_rows_read_only
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.workshop_filter_clear
import kotlin.math.roundToInt

/**
 * The two services, as a segmented control.
 *
 * [ContractRowTab] declares the order and the copy, so this draws whatever the table holds rather
 * than naming either tab itself. The selected tab wears the button gradient, as the
 * اشخاص حقوقی / اشخاص حقیقی switch does.
 *
 * The gradient pill slides between the tabs rather than jumping, the way [TaminSegmentedTabs] moves
 * its own — that strip has no hint line, so the motion is borrowed rather than the component. The
 * position is read inside `layout {}`, so a frame of the slide relays out the pill alone; it is
 * placed with `placeRelative`, so on the RTL page the first tab sits rightmost with no mirroring.
 */
@Composable
fun ContractRowTabs(
    selected: ContractRowTab,
    onSelect: (ContractRowTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val stripShape = remember { RoundedCornerShape(CornerRadius.xl) }
    val tabShape = remember { RoundedCornerShape(CornerRadius.listRow) }
    val tabs = ContractRowTab.entries
    val position by animateFloatAsState(
        targetValue = tabs.indexOf(selected).coerceAtLeast(0).toFloat(),
        animationSpec = tween(durationMillis = Duration.normal, easing = Easing.standard),
        label = "contractRowTabsPill",
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(stripShape)
            .background(colors.bgPage)
            .border(Thickness.border, colors.border, stripShape)
            .padding(WorkshopDimens.contractRowTabStripPadding),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .layout { measurable, constraints ->
                    val width = if (constraints.hasBoundedWidth) constraints.maxWidth else 0
                    val gap = WorkshopDimens.contractRowTabGap.roundToPx()
                    val count = tabs.size.coerceAtLeast(1)
                    val segment = ((width - gap * (count - 1)) / count).coerceAtLeast(0)
                    val pill = measurable.measure(
                        constraints.copy(minWidth = segment, maxWidth = segment),
                    )
                    layout(width, pill.height) {
                        pill.placeRelative(x = ((segment + gap) * position).roundToInt(), y = 0)
                    }
                }
                .clip(tabShape)
                .background(colors.buttonGradient),
        )
        Row(
            modifier = Modifier.fillMaxWidth().selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.contractRowTabGap),
        ) {
            tabs.forEach { tab ->
                val isSelected = tab == selected
                val labelColor by animateColorAsState(
                    targetValue = if (isSelected) colors.onGradient else colors.textMuted,
                    animationSpec = tween(durationMillis = Duration.normal, easing = Easing.standard),
                    label = "contractRowTabLabel",
                )
                val hintColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        colors.onGradient.copy(alpha = SELECTED_HINT_ALPHA)
                    } else {
                        colors.textMuted
                    },
                    animationSpec = tween(durationMillis = Duration.normal, easing = Easing.standard),
                    label = "contractRowTabHint",
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(WorkshopDimens.contractRowTabHeight)
                        .clip(tabShape)
                        .selectable(
                            selected = isSelected,
                            role = Role.Tab,
                            onClick = { onSelect(tab) },
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(tab.label),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = labelColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = stringResource(tab.hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = hintColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

/** The hint under a selected tab stays quieter than its label, on the gradient as off it. */
private const val SELECTED_HINT_ALPHA = 0.8f

/**
 * Which workshop is in force, and what the list is.
 *
 * The «این فهرست فقط برای مشاهده است.» line is the design's own, and it is the only thing on the
 * screen that says the rows are inert — the cards carry no chevron and no ripple, so without it a
 * user would be left tapping to find out.
 */
@Composable
fun ContractRowFilterBar(
    filterText: String,
    countText: String,
    onChange: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    /** Why the visible tab is not the one that was asked for. Null when the user chose it. */
    notice: String? = null,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.chipGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(colors.blueBg)
                    .border(Thickness.border, colors.blueBorder, CircleShape)
                    .padding(
                        horizontal = WorkshopDimens.chipHorizontalPadding,
                        vertical = WorkshopDimens.chipVerticalPadding,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Text(
                    text = filterText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.blueText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onClear)
                        .padding(Spacing.xxs),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_tamin_cross),
                        contentDescription = stringResource(Res.string.workshop_filter_clear),
                        tint = colors.blueText,
                        modifier = Modifier.size(WorkshopDimens.chipCrossSize),
                    )
                }
            }
            Text(
                text = stringResource(Res.string.contract_rows_filter_change),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textSecondary,
                maxLines = 1,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.bgSurface)
                    .border(Thickness.border, colors.border, CircleShape)
                    .clickable(onClick = onChange)
                    .padding(
                        horizontal = WorkshopDimens.chipHorizontalPadding,
                        vertical = WorkshopDimens.chipVerticalPadding,
                    ),
            )
        }

        if (notice != null) {
            Text(
                text = notice,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.blueText,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.chipGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = colors.textMuted,
                modifier = Modifier.size(IconSize.small),
            )
            Text(
                text = stringResource(Res.string.contract_rows_read_only),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = countText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textSecondary,
            )
        }
    }
}
