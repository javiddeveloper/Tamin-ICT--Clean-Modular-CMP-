package com.tamin.taminhamrah.feature.treatment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_user
import taminx.core.core_ui.records_details
import taminx.core.core_ui.search_advanced_cd
import taminx.core.core_ui.share_yours
import taminx.core.core_ui.unit_rial
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.components.StatTile
import com.tamin.taminhamrah.ui.components.StatTileStyle
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

import com.tamin.taminhamrah.feature.treatment.ui.TreatmentDimens
import kotlinx.collections.immutable.ImmutableList

/**
 * Components for the medical-records timeline: the record card and its date-group
 * header, the category filter strip, the in-header filter bar, and the pinned cost
 * totals footer.
 */

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
    /** `null` until this record's price arrives, which is a separate request from the list. */
    shareAmount: String?,
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
            .raisedCard(CornerRadius.card)
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
        TaminDivider(modifier = Modifier.padding(top = Spacing.md))
        Spacer(modifier = Modifier.height(Spacing.sm))
        MedicalRecordFooter(shareAmount = shareAmount)
    }
}

/**
 * Paints the category stripe down the card's leading edge — the right side under
 * the app's right-to-left layout, the left side if it is ever rendered left-to-right.
 *
 * [width] defaults to the records card's 4dp; the design draws a narrower 3dp stripe on the
 * confirmations cards, so the caller decides rather than every card sharing one number.
 */
internal fun Modifier.accentStripe(
    color: Color,
    width: Dp = TreatmentDimens.accentBarWidth,
): Modifier = drawBehind {
    val barWidth = width.toPx()
    val x = if (layoutDirection == LayoutDirection.Rtl) size.width - barWidth else 0f
    drawRect(
        color = color,
        topLeft = Offset(x, 0f),
        size = Size(barWidth, size.height),
    )
}

@Composable
private fun MedicalRecordFooter(shareAmount: String?) {
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
                text = stringResource(Res.string.share_yours),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textMuted,
            )
            // Number and unit are separate so «ریال» stays left of the digits: in this RTL row the
            // number is the right child, «ریال» the left one.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                if (shareAmount == null) {
                    ShimmerBlock(
                        modifier = Modifier
                            .width(TreatmentDimens.recordShareShimmerWidth)
                            .height(TreatmentDimens.recordShareShimmerHeight),
                    )
                } else {
                    NumericText(
                        text = shareAmount,
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.textPrimary,
                    )
                }
                Text(
                    text = stringResource(Res.string.unit_rial),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textPrimary,
                )
            }
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(CornerRadius.chip))
                .background(colors.medicalGradient)
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = stringResource(Res.string.records_details),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
            )
            // Points toward the detail screen; autoMirrored, so it sits on the left in RTL.
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(IconSize.small),
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
            .then(
                if (selected) Modifier.background(colors.medicalGradient)
                else Modifier.background(colors.bgSurface).border(1.dp, colors.border, CircleShape)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) Color.White else colors.textSecondary,
        )
    }
}

/** Horizontally scrollable strip of category filters above the timeline with auto-scrolling. */
@Composable
fun TreatmentFilterChipRow(
    categories: ImmutableList<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedIndex) {
        if (selectedIndex in categories.indices) {
            lazyListState.animateScrollToItem(selectedIndex)
        }
    }

    LazyRow(
        state = lazyListState,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        itemsIndexed(categories) { index, label ->
            TreatmentFilterChip(
                label = label,
                selected = index == selectedIndex,
                onClick = {
                    onSelect(index)
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(index)
                    }
                },
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
    // Each chooser's menu is composed beside the chip that opens it, so the menu anchors there
    // instead of floating somewhere the trigger has no relationship with. The chip's own width is
    // handed over so the menu can be sized against it.
    personMenu: @Composable (anchorWidth: Dp) -> Unit = {},
    dateMenu: @Composable (anchorWidth: Dp) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // BoxWithConstraints rather than onSizeChanged: the chip's width is already fixed by the
        // weight, so it can be read during composition instead of written back as state after
        // layout — which would cost a recomposition and a frame every time the bar is laid out.
        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            FilterTrigger(
                label = personLabel,
                leadingIcon = vectorResource(Res.drawable.ic_tamin_user),
                trailingIcon = dropdownIcon,
                onClick = onPersonClick,
                modifier = Modifier.fillMaxWidth(),
                expanded = personExpanded,
            )
            personMenu(maxWidth)
        }
        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            FilterTrigger(
                label = dateLabel,
                leadingIcon = vectorResource(Res.drawable.ic_tamin_calendar),
                trailingIcon = dropdownIcon,
                onClick = onDateClick,
                modifier = Modifier.fillMaxWidth(),
                expanded = dateExpanded,
            )
            dateMenu(maxWidth)
        }
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
                contentDescription = stringResource(Res.string.search_advanced_cd),
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
    leadingIcon: ImageVector? = null,
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
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(IconSize.small),
            )
        }
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
    /** `null` for a figure still being fetched; that tile shimmers on its own. */
    insuredShareAmount: String?,
    organizationShareLabel: String,
    organizationShareAmount: String?,
    totalLabel: String,
    totalAmount: String?,
    modifier: Modifier = Modifier,
) {
    TaminBottomBar(modifier = modifier) {
        CostSplitTiles(
            insuredShareLabel = insuredShareLabel,
            insuredShareAmount = insuredShareAmount,
            organizationShareLabel = organizationShareLabel,
            organizationShareAmount = organizationShareAmount,
            totalLabel = totalLabel,
            totalAmount = totalAmount,
        )
    }
}

/**
 * The three-figure cost split — insured share, organization share, total — as one row of tiles.
 *
 * One definition for all three places it appears: pinned under the timeline, as the detail
 * screen's total, and [dense] inside a single prescribed item. The colors carry the meaning, so
 * they must not drift between those: green is what the person pays, blue what the organization
 * pays, orange the two added up.
 *
 * Under the app's right-to-left layout the first child renders rightmost, so the order below reads
 * on screen as total, organization, insured — left to right.
 */
@Composable
fun CostSplitTiles(
    insuredShareLabel: String,
    /** `null` for a figure still being fetched; that tile shimmers on its own. */
    insuredShareAmount: String?,
    organizationShareLabel: String,
    organizationShareAmount: String?,
    totalLabel: String,
    totalAmount: String?,
    modifier: Modifier = Modifier,
    dense: Boolean = false,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(if (dense) Spacing.xs else Spacing.sm),
    ) {
        StatTile(
            label = insuredShareLabel,
            amount = insuredShareAmount,
            containerColor = colors.greenBg,
            contentColor = colors.greenText,
            modifier = Modifier.weight(1f),
            style = if (dense) StatTileStyle.Dense else StatTileStyle.Standard,
        )
        StatTile(
            label = organizationShareLabel,
            amount = organizationShareAmount,
            containerColor = colors.blueBg,
            contentColor = colors.blueText,
            modifier = Modifier.weight(1f),
            style = if (dense) StatTileStyle.Dense else StatTileStyle.Standard,
        )
        StatTile(
            label = totalLabel,
            amount = totalAmount,
            containerColor = colors.orangeBg,
            contentColor = colors.orangeText,
            modifier = Modifier.weight(1f),
            style = if (dense) StatTileStyle.Dense else StatTileStyle.Standard,
        )
    }
}
