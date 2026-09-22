package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopStats
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import com.tamin.taminhamrah.ui.components.CopyIconButton
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.dashedOutline
import com.tamin.taminhamrah.ui.components.rememberCopyAction
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_chevron_down
import taminx.core.core_ui.ic_tamin_copy
import taminx.core.core_ui.workshop_card_collapse
import taminx.core.core_ui.workshop_card_expand
import taminx.core.core_ui.workshop_code
import taminx.core.core_ui.workshop_copy_code
import taminx.core.core_ui.workshop_details_and_actions
import taminx.core.core_ui.workshop_employer_type
import taminx.core.core_ui.workshop_filter
import taminx.core.core_ui.workshop_list_title
import taminx.core.core_ui.workshop_start_activity_date
import taminx.core.core_ui.workshop_stat_active
import taminx.core.core_ui.workshop_stat_inactive
import taminx.core.core_ui.workshop_stat_total
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.ui.draw.shadow
import com.tamin.taminhamrah.ui.theme.Elevation

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
            .taminSurface(WorkshopDimens.statsCardCorner)
            .padding(vertical = WorkshopDimens.statsCardVerticalPadding, horizontal = WorkshopDimens.statsCardHorizontalPadding),
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
            ShimmerBlock(modifier = Modifier.width(WorkshopDimens.statShimmerWidth).height(WorkshopDimens.statShimmerHeight))
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

/**
 * The line above a card list: its heading, the count beside it, and the filter chip.
 *
 * [count] and [onFilterClick] are both optional because the design drops them per screen —
 * جزئیات کارگاه heads its card with «اطلاعات کارگاه» and neither a count nor a filter.
 */
@Composable
fun WorkshopSectionHeader(
    modifier: Modifier = Modifier,
    title: String = stringResource(Res.string.workshop_list_title),
    count: Int? = null,
    /**
     * Makes the heading copy this when tapped, and shows the copy glyph beside it.
     *
     * The raw value rather than the one the heading prints, for the reason [DetailRow] gives: a
     * code shown in Persian digits has to be copied in ASCII ones.
     */
    copyValue: String? = null,
    isFilterActive: Boolean = false,
    onFilterClick: (() -> Unit)? = null,
) {
    val colors = LocalTaminColors.current
    val copy = copyValue?.let { rememberCopyAction(it) }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = if (copy != null) Modifier.clickable(onClick = copy) else Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
            )
            if (copyValue != null) {
                CopyIconButton(value = copyValue, label = title, interactive = false)
            }
            if (count != null) {
                NumericText(
                    text = count.toString().toPersianDigits(),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.blueText,
                    modifier = Modifier
                        .background(colors.blueBg, CircleShape)
                        .padding(horizontal = Spacing.sm, vertical = WorkshopDimens.countBadgeVerticalPadding),
                )
            }
        }

        if (onFilterClick == null) return@Row

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
 * actions only existed once a card had been expanded, which hid the whole point of the list. The
 * card itself answers a tap the same way, since the whole of it reads as the workshop.
 *
 * Lifted off the page as the medical records cards are — the shadow cast before the surface, so it
 * falls outside the card rather than darkening its edge.
 */
@Composable
fun WorkshopCard(
    workshop: WorkshopPR,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = Elevation.lg, shape = WorkshopCardShape)
            .taminSurface(CornerRadius.lg)
            .clickable(onClick = onOpenDetails)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        WorkshopCardHeader(workshop = workshop)

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
 * A workshop's name with its activity status beside it — the head of both the list card and
 * the جزئیات کارگاه card, which the design draws identically.
 */
@Composable
internal fun WorkshopCardHeader(
    workshop: WorkshopPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val (pillBackground, pillForeground) = workshop.status.tint.colors()
    Row(
        modifier = modifier.fillMaxWidth(),
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
            // The design outlines each pill in a paler shade of its own text color
            // (#BFE6CF on green, #F0DCA8 on orange, #F3C9C4 on red). Deriving it from the
            // content color reproduces those without three more palette entries, and keeps
            // working in dark theme where fixed pastels would not.
            borderColor = pillForeground.copy(alpha = WorkshopDimens.statusPillBorderAlpha),
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
internal fun WorkshopCodeRow(
    workshop: WorkshopPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val code = workshop.workshopId
    // The raw ASCII code, not the Persian-digit label: it is pasted into forms and searches,
    // where Persian digits match nothing.
    // announce = false: the chip's own tick is the confirmation the design draws, and a toast on
    // top of it would say the same thing twice. It also keeps this row off LocalToaster, which has
    // no default, so the card still composes in a preview with no AppToastHost above it.
    val copy = rememberCopyAction(code, announce = false)
    var isCopied by remember { mutableStateOf(false) }
    LaunchedEffect(isCopied) {
        if (!isCopied) return@LaunchedEffect
        delay(WorkshopDimens.copiedFeedbackMillis.milliseconds)
        isCopied = false
    }

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
                .clip(CodeChipShape)
                .clickable(enabled = code.isNotBlank()) {
                    copy()
                    isCopied = true
                }
                .background(colors.blueBg)
                // The design pins the code behind a dashed outline, which is what marks it
                // as something to lift rather than a plain tinted label.
                .dashedOutline(colors.blueBorder, WorkshopDimens.codeChipCorner, WorkshopDimens.codeChipBorderWidth)
                .padding(
                    horizontal = WorkshopDimens.codeChipHorizontalPadding,
                    vertical = WorkshopDimens.codeChipVerticalPadding,
                ),
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
            // The design answers a copy by turning the glyph into a green tick for a beat,
            // then back.
            Icon(
                imageVector = vectorResource(
                    if (isCopied) Res.drawable.ic_tamin_check else Res.drawable.ic_tamin_copy,
                ),
                contentDescription = stringResource(Res.string.workshop_copy_code),
                tint = if (isCopied) colors.greenText else colors.blueText,
                modifier = Modifier.size(WorkshopDimens.codeChipGlyphSize),
            )
        }
    }
}

/**
 * How strongly the pill's outline shows through.
 *
 * Tuned so the derived border lands on the design's own values — #03794A at this alpha over the
 * green fill reads as #BFE6CF, which is what the design draws.
 */

/** «جزئیات بیشتر» / «بستن» — a dashed rule, a label, and a rotating chevron. */
@Composable
fun CardExpandToggle(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    expandLabel: StringResource = Res.string.workshop_card_expand,
    collapseLabel: StringResource = Res.string.workshop_card_collapse,
) {
    val colors = LocalTaminColors.current
    val rotation = animateFloatAsState(if (isExpanded) WorkshopDimens.toggleHalfTurn else 0f, label = "chevron")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = WorkshopDimens.toggleTopMargin)
            .drawBehind {
                drawLine(
                    color = colors.divider,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = Thickness.border.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(WorkshopDimens.toggleDashOn.toPx(), WorkshopDimens.toggleDashOff.toPx()),
                    ),
                )
            }
            .clickable(onClick = onToggle)
            .padding(vertical = WorkshopDimens.toggleVerticalPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(if (isExpanded) collapseLabel else expandLabel),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = colors.blueText,
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_down),
            contentDescription = null,
            tint = colors.blueText,
            modifier = Modifier
                .padding(start = Spacing.tabSelector)
                .size(WorkshopDimens.toggleChevronSize)
                .graphicsLayer { rotationZ = rotation.value },
        )
    }
}

private val CodeChipShape = RoundedCornerShape(WorkshopDimens.codeChipCorner)

private val WorkshopCardShape = RoundedCornerShape(CornerRadius.lg)


