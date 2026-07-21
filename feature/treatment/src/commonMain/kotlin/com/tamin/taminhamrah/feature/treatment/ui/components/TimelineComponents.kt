package com.tamin.taminhamrah.feature.treatment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.rotate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.components.StatTile
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * Components for the medical-records timeline: the record card and its date-group
 * header, the category filter strip, the in-header filter bar, and the pinned cost
 * totals footer.
 */

private val ACCENT_BAR_WIDTH = 4.dp

/**
 * One entry in the medical-records timeline. The accent stripe on the leading edge and
 * the matching category badge encode the record type (prescription, visit, paraclinic).
 */
@Composable
fun MedicalRecordCard(
    category: String,
    date: String,
    title: String,
    subtitle: String,
    shareAmount: String,
    accentColor: Color,
    accentContainerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    categoryIcon: ImageVector? = null,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.card)
            .accentStripe(accentColor)
            .clickable(onClick = onClick)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            StatusPill(
                text = category,
                containerColor = accentContainerColor,
                contentColor = accentColor,
                icon = categoryIcon,
            )
            NumericText(
                text = date,
                style = MaterialTheme.typography.labelMedium,
                color = colors.textMuted,
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textTertiary,
        )
        TaminDivider(modifier = Modifier.padding(top = Spacing.sm))
        MedicalRecordFooter(shareAmount = shareAmount)
    }
}

/**
 * Paints the 4dp category stripe down the card's leading edge — the right side under
 * the app's right-to-left layout, the left side if it is ever rendered left-to-right.
 */
private fun Modifier.accentStripe(color: Color): Modifier = drawBehind {
    val barWidth = ACCENT_BAR_WIDTH.toPx()
    val x = if (layoutDirection == LayoutDirection.Rtl) size.width - barWidth else 0f
    drawRect(
        color = color,
        topLeft = Offset(x, 0f),
        size = Size(barWidth, size.height),
    )
}

@Composable
private fun MedicalRecordFooter(shareAmount: String) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = "سهم شما",
                style = MaterialTheme.typography.labelMedium,
                color = colors.textMuted,
            )
            NumericText(
                text = shareAmount,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
            )
            Text(
                text = "ریال",
                style = MaterialTheme.typography.labelMedium,
                color = colors.textMuted,
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(CornerRadius.chip))
                .background(colors.medicalGradient)
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        ) {
            Text(
                text = "جزئیات",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
            )
        }
    }
}

/** Sticky date bucket label separating the timeline into months. */
@Composable
fun RecordGroupHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    SectionLabel(
        text = text,
        modifier = modifier.padding(
            horizontal = Spacing.page,
            vertical = Spacing.sm,
        ),
    )
}

/** Single selectable pill in the category filter strip. */
@Composable
fun TreatmentFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(if (selected) colors.teal else colors.bgSurface)
            .border(
                width = 1.dp,
                color = if (selected) Color.Transparent else colors.border,
                shape = CircleShape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) Color.White else colors.textTertiary,
        )
    }
}

/** Horizontally scrollable strip of category filters above the timeline. */
@Composable
fun TreatmentFilterChipRow(
    categories: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = Spacing.page, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        categories.forEachIndexed { index, label ->
            TreatmentFilterChip(
                label = label,
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
            )
        }
    }
}

/**
 * The person / date-range / search row that sits inside the teal header on the
 * timeline screen. Designed for [TaminTopAppBar]'s content slot.
 */
@Composable
fun TimelineFilterBar(
    personLabel: String,
    dateLabel: String,
    dropdownIcon: ImageVector,
    searchIcon: ImageVector,
    onPersonClick: () -> Unit,
    onDateClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
    // Which chooser is open, so its chevron can point the other way.
    personExpanded: Boolean = false,
    dateExpanded: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilterTrigger(
            label = personLabel,
            trailingIcon = dropdownIcon,
            onClick = onPersonClick,
            modifier = Modifier.weight(1f),
            expanded = personExpanded,
        )
        FilterTrigger(
            label = dateLabel,
            trailingIcon = dropdownIcon,
            onClick = onDateClick,
            modifier = Modifier.weight(1f),
            expanded = dateExpanded,
        )
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(Color.White.copy(alpha = 0.1f))
                .clickable(onClick = onSearchClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = searchIcon,
                contentDescription = "جست‌وجوی پیشرفته",
                tint = Color.White,
                modifier = Modifier.size(IconSize.small),
            )
        }
    }
}

/** Translucent dropdown trigger used inside the teal header. */
@Composable
private fun FilterTrigger(
    label: String,
    trailingIcon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
) {
    // The chevron points down when closed and up when open, animated so the flip reads as one
    // control rather than two icons swapping.
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) CHEVRON_ROTATION_EXPANDED else CHEVRON_ROTATION_COLLAPSED,
        label = "filterChevron",
    )
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(Color.White.copy(alpha = 0.1f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.18f),
                shape = RoundedCornerShape(CornerRadius.lg),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = trailingIcon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(IconSize.small)
                .rotate(chevronRotation),
        )
    }
}

/** The supplied chevron points back/right, so closed rotates it down and open rotates it up. */
private const val CHEVRON_ROTATION_COLLAPSED = 90f
private const val CHEVRON_ROTATION_EXPANDED = -90f

/**
 * Pinned footer summarizing the filtered timeline: the insured person's share, the
 * organization's share and the combined total.
 */
@Composable
fun CostTotalsBar(
    insuredShareLabel: String,
    insuredShareAmount: String,
    organizationShareLabel: String,
    organizationShareAmount: String,
    totalLabel: String,
    totalAmount: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    TaminBottomBar(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            StatTile(
                label = insuredShareLabel,
                amount = insuredShareAmount,
                containerColor = colors.greenBg,
                contentColor = colors.greenText,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = organizationShareLabel,
                amount = organizationShareAmount,
                containerColor = colors.blueBg,
                contentColor = colors.blueText,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = totalLabel,
                amount = totalAmount,
                containerColor = colors.orangeBg,
                contentColor = colors.orangeText,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
