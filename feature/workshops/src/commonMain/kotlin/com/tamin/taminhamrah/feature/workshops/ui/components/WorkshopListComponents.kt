package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopStats
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.workshop_code
import taminx.core.core_ui.workshop_copy_code
import taminx.core.core_ui.workshop_details_and_actions
import taminx.core.core_ui.workshop_employer_type
import taminx.core.core_ui.workshop_filter
import taminx.core.core_ui.workshop_list_title
import taminx.core.core_ui.workshop_stat_active
import taminx.core.core_ui.workshop_stat_inactive
import taminx.core.core_ui.workshop_stat_total
import taminx.core.core_ui.workshop_start_activity_date

/**
 * The three figures over the list, riding up into the gradient header.
 *
 * [stats] null means they have not been counted yet, and each figure shimmers rather than reading
 * a zero that is about to change.
 */
@Composable
fun WorkshopStatsCard(
    stats: WorkshopStats?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(StatsCardCorner)
            .padding(vertical = StatsCardVerticalPadding, horizontal = StatsCardHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Three equal columns, no rules between them, and every figure in the same blue — the
        // design does not single out the active count.
        StatColumn(
            label = stringResource(Res.string.workshop_stat_total),
            value = stats?.total,
            valueColor = colors.blueText,
            modifier = Modifier.weight(1f),
        )
        StatColumn(
            label = stringResource(Res.string.workshop_stat_active),
            value = stats?.active,
            valueColor = colors.blueText,
            modifier = Modifier.weight(1f),
        )
        StatColumn(
            label = stringResource(Res.string.workshop_stat_inactive),
            value = stats?.inactive,
            valueColor = colors.blueText,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatColumn(
    label: String,
    value: Int?,
    modifier: Modifier = Modifier,
    valueColor: Color = LocalTaminColors.current.textPrimary,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        if (value == null) {
            ShimmerBlock(modifier = Modifier.width(StatShimmerWidth).height(StatShimmerHeight))
        } else {
            NumericText(
                text = value.toString().toPersianDigits(),
                style = MaterialTheme.typography.titleLarge,
                color = valueColor,
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = LocalTaminColors.current.textSecondary,
        )
    }
}

/** «لیست کارگاه‌ها» with its count, and the filter chip that opens the status sheet. */
@Composable
fun WorkshopSectionHeader(
    count: Int,
    isFilterActive: Boolean,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = stringResource(Res.string.workshop_list_title),
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
            )
            NumericText(
                text = count.toString().toPersianDigits(),
                style = MaterialTheme.typography.labelMedium,
                color = colors.blueText,
                modifier = Modifier
                    .background(colors.blueBg, CircleShape)
                    .padding(horizontal = Spacing.sm, vertical = CountBadgePadding),
            )
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(CornerRadius.chip))
                .clickable(onClick = onFilterClick)
                .background(if (isFilterActive) colors.blueBg else colors.chipBg)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = stringResource(Res.string.workshop_filter),
                style = MaterialTheme.typography.labelMedium,
                color = if (isFilterActive) colors.blueText else colors.textSecondary,
            )
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                tint = if (isFilterActive) colors.blueText else colors.textSecondary,
                modifier = Modifier.height(IconSize.small),
            )
        }
    }
}

/**
 * One کارگاه.
 *
 * Details and actions sit behind a single button, as the design has it: on the old screen the
 * actions only existed once a card had been expanded, which hid the whole point of the list.
 */
@Composable
fun WorkshopCard(
    workshop: WorkshopPR,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val (pillBackground, pillForeground) = workshop.status.tint.colors()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.lg)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = workshop.name,
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f, fill = false),
            )
            StatusPill(
                text = workshop.statusLabel,
                containerColor = pillBackground,
                contentColor = pillForeground,
                icon = Icons.Default.Circle,
                // The design outlines each pill in a paler shade of its own text colour
                // (#BFE6CF on green, #F0DCA8 on orange, #F3C9C4 on red). Deriving it from the
                // content colour reproduces those without three more palette entries, and keeps
                // working in dark theme where fixed pastels would not.
                borderColor = pillForeground.copy(alpha = StatusPillBorderAlpha),
            )
        }

        WorkshopCodeRow(workshop = workshop)

        DetailRow(
            label = stringResource(Res.string.workshop_employer_type),
            value = workshop.employerType,
            // The design draws this in its darker green (--tm-green-strong), not the lighter
            // success green the status word uses.
            valueColor = colors.springGreenText,
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.workshop_start_activity_date),
            value = workshop.startDate,
        )

        TaminPrimaryButton(
            text = stringResource(Res.string.workshop_details_and_actions),
            onClick = onOpenDetails,
            background = colors.buttonGradient,
        )
    }
}

/**
 * The workshop code, on a tinted chip with a copy control.
 *
 * What lands on the clipboard is the raw ASCII code, not the Persian-digit label: it is pasted
 * into forms and searches, where Persian digits would not match.
 */
@Composable
private fun WorkshopCodeRow(
    workshop: WorkshopPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val clipboard = LocalClipboardManager.current
    val code = workshop.workshopId

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.workshop_code),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(CornerRadius.chip))
                .clickable(enabled = code.isNotBlank()) {
                    clipboard.setText(AnnotatedString(code))
                }
                .background(colors.blueBg)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            // Code first so that right-to-left puts it on the right and the copy glyph on the
            // left, which is where the design draws it.
            NumericText(
                text = workshop.codeLabel,
                style = MaterialTheme.typography.labelMedium,
                color = colors.blueText,
            )
            Icon(
                imageVector = Icons.Outlined.ContentCopy,
                contentDescription = stringResource(Res.string.workshop_copy_code),
                tint = colors.blueText,
                modifier = Modifier.height(IconSize.small),
            )
        }
    }
}

/** The design marks a status with a dot, which is smaller than any icon in the scale. */
private val StatusDotSize = 6.dp
/**
 * How strongly the pill's outline shows through.
 *
 * Tuned so the derived border lands on the design's own values — #03794A at this alpha over the
 * green fill reads as #BFE6CF, which is what the design draws.
 */
/** The stats strip's own geometry: `border-radius:18px; padding:13px 6px` in the design. */
private val StatsCardCorner = 18.dp
private val StatsCardVerticalPadding = 13.dp
private val StatsCardHorizontalPadding = 6.dp

private const val StatusPillBorderAlpha = 0.20f

private val StatusPillVerticalPadding = 5.dp

private val StatShimmerWidth = 28.dp
private val StatShimmerHeight = 20.dp
private val StatDividerHeight = 32.dp
private val CountBadgePadding = 2.dp
