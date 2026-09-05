package com.tamin.taminhamrah.feature.treatment.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentDimens
import com.tamin.taminhamrah.feature.treatment.ui.medicalConfirmations.TreatmentConfirmationsDimens
import com.tamin.taminhamrah.model.treatment.MedicalConfirmationPR
import com.tamin.taminhamrah.model.treatment.ConfirmationStatus
import com.tamin.taminhamrah.model.treatment.confirmationStatus
import com.tamin.taminhamrah.ui.ABSENT_VALUE
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StaggeredEntranceState
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.components.startToEndGradient
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.pushBack
import com.tamin.taminhamrah.ui.pushForward
import com.tamin.taminhamrah.ui.theme.TaminTeal500
import com.tamin.taminhamrah.ui.theme.TaminTeal900
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminOnAccentInk
import com.tamin.taminhamrah.ui.theme.shimmer
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.confirmations_action_details
import taminx.core.core_ui.confirmations_branch_approval
import taminx.core.core_ui.confirmations_branch_named
import taminx.core.core_ui.confirmations_branch_reviewer_label
import taminx.core.core_ui.confirmations_branch_status_label
import taminx.core.core_ui.confirmations_default_branch
import taminx.core.core_ui.confirmations_default_center
import taminx.core.core_ui.confirmations_default_support_type
import taminx.core.core_ui.confirmations_download_image_action
import taminx.core.core_ui.confirmations_empty
import taminx.core.core_ui.confirmations_end_date_label
import taminx.core.core_ui.confirmations_filter_all
import taminx.core.core_ui.confirmations_filter_approved
import taminx.core.core_ui.confirmations_filter_empty
import taminx.core.core_ui.confirmations_filter_pending
import taminx.core.core_ui.confirmations_inpatient_days_count
import taminx.core.core_ui.confirmations_inpatient_rest_header
import taminx.core.core_ui.confirmations_medical_authority_label
import taminx.core.core_ui.confirmations_opinion_header
import taminx.core.core_ui.confirmations_outpatient_rest_label
import taminx.core.core_ui.confirmations_review_status_header
import taminx.core.core_ui.confirmations_saved_acknowledge
import taminx.core.core_ui.confirmations_saved_body
import taminx.core.core_ui.confirmations_saved_title
import taminx.core.core_ui.confirmations_start_date_label
import taminx.core.core_ui.confirmations_status_rejected
import taminx.core.core_ui.confirmations_unapproved_period
import taminx.core.core_ui.confirmations_unapproved_period_range
import taminx.core.core_ui.confirmations_unit_day
import taminx.core.core_ui.costs_action_operations
import taminx.core.core_ui.costs_send_to_inbox
import taminx.core.core_ui.costs_view_certificate
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_download
import taminx.core.core_ui.ic_tamin_medical_approvals
import taminx.core.core_ui.ic_tamin_medical_centers
import taminx.core.core_ui.ic_tamin_medical_records
import com.tamin.taminhamrah.ui.theme.Thickness


private const val FILTER_APPROVED = 1
private const val FILTER_PENDING = 2

/**
 * Every color and the fallback label a verdict carries, in one place.
 *
 * The card and the detail view drew the same chip independently, each with its own
 * `if (approved) … else …`, so a third tone had to be added twice, or it drifted.
 */
private data class ConfirmationTone(
    val accent: Color,
    val chipBackground: Color,
    val chipForeground: Color,
    /** Shown only when the service sends a blank verdict. */
    val fallbackLabel: String,
)

/**
 * Rejection is red, not the orange it used to share with pending -- «تایید نشده» and
 * «در انتظار تایید» reading identically is the sort of thing a user only notices too late.
 * A blank verdict keeps the pending tone, which is what it rendered as before.
 */
@Composable
private fun confirmationTone(status: ConfirmationStatus): ConfirmationTone {
    val colors = LocalTaminColors.current
    val approvedLabel = stringResource(Res.string.confirmations_filter_approved)
    val pendingLabel = stringResource(Res.string.confirmations_filter_pending)
    val rejectedLabel = stringResource(Res.string.confirmations_status_rejected)
    return remember(status, colors, approvedLabel, pendingLabel, rejectedLabel) {
        when (status) {
            ConfirmationStatus.APPROVED -> ConfirmationTone(
                accent = colors.teal,
                chipBackground = colors.greenBg,
                chipForeground = colors.greenText,
                fallbackLabel = approvedLabel,
            )

            ConfirmationStatus.REJECTED -> ConfirmationTone(
                accent = colors.dangerText,
                chipBackground = colors.dangerBg,
                chipForeground = colors.dangerText,
                fallbackLabel = rejectedLabel,
            )

            ConfirmationStatus.PENDING, ConfirmationStatus.UNKNOWN -> ConfirmationTone(
                accent = colors.orangeText,
                chipBackground = colors.orangeBg,
                chipForeground = colors.orangeText,
                fallbackLabel = pendingLabel,
            )
        }
    }
}

/** The chip both the card and the detail header draw, so the tone is applied once. */
@Composable
private fun ConfirmationStatusChip(
    statusDesc: String,
    tone: ConfirmationTone,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.chip))
            .background(tone.chipBackground)
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
    ) {
        Box(
            modifier = Modifier
                .size(TreatmentConfirmationsDimens.statusDotSize)
                .background(tone.chipForeground, CircleShape)
        )
        Spacer(modifier = Modifier.width(Spacing.xxs))
        Text(
            text = statusDesc.ifBlank { tone.fallbackLabel },
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = tone.chipForeground,
        )
    }
}

@Composable
internal fun ConfirmationsList(
    confirmations: ImmutableList<MedicalConfirmationPR>,
    isLoading: Boolean,
    error: String?,
    /** Which chip is on. Held by the screen: the list is torn down while a detail is open. */
    selectedFilterIndex: Int,
    onFilterSelected: (Int) -> Unit,
    staggerState: StaggeredEntranceState,
    onSelectDetail: (MedicalConfirmationPR) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        when {
            isLoading && confirmations.isEmpty() -> ConfirmationsShimmerSkeleton()

            error != null -> ConfirmationsErrorState(message = error)

            confirmations.isEmpty() ->
                TaminEmptyState(message = stringResource(Res.string.confirmations_empty))

            else -> {
                // The chips sit above the swap rather than scrolling with the rows: the control
                // you just tapped has to stay under your thumb while its list travels.
                ConfirmationsFilterRow(
                    selectedIndex = selectedFilterIndex,
                    onSelect = onFilterSelected,
                )

                // Each pane filters for its own chip, which is the whole reason this reads as a
                // change of tab: the list on its way out keeps showing the rows it was showing,
                // instead of both halves rendering the same already-filtered result.
                AnimatedContent(
                    targetState = selectedFilterIndex,
                    transitionSpec = {
                        if (targetState > initialState) pushForward() else pushBack()
                    },
                    label = "confirmations-filter",
                    modifier = Modifier.weight(1f),
                ) { filterIndex ->
                    ConfirmationRows(
                        confirmations = confirmations,
                        filterIndex = filterIndex,
                        staggerState = staggerState,
                        onSelectDetail = onSelectDetail,
                    )
                }
            }
        }
    }
}

/** «همه» · «تأییدشده» · «در انتظار», the three the design offers. */
@Composable
private fun ConfirmationsFilterRow(selectedIndex: Int, onSelect: (Int) -> Unit) {
    val colors = LocalTaminColors.current
    val filterAll = stringResource(Res.string.confirmations_filter_all)
    val filterApproved = stringResource(Res.string.confirmations_filter_approved)
    val filterPending = stringResource(Res.string.confirmations_filter_pending)
    val filters = remember(filterAll, filterApproved, filterPending) {
        listOf(filterAll, filterApproved, filterPending)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page)
            .padding(top = Spacing.md, bottom = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        filters.forEachIndexed { idx, label ->
            val active = idx == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(CornerRadius.chip))
                    .background(
                        if (active) colors.teal.copy(alpha = 0.12f)
                        else colors.bgSurface
                    )
                    .border(
                        width = Thickness.border,
                        color = if (active) colors.teal else colors.border,
                        shape = RoundedCornerShape(CornerRadius.chip)
                    )
                    .clickable { onSelect(idx) }
                    .padding(vertical = Spacing.sm),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                    color = if (active) colors.teal else colors.textSecondary,
                )
            }
        }
    }
}

/** The rows one chip selects, as their own scrolling list so a chip's pane can travel whole. */
@Composable
private fun ConfirmationRows(
    confirmations: ImmutableList<MedicalConfirmationPR>,
    filterIndex: Int,
    staggerState: StaggeredEntranceState,
    onSelectDetail: (MedicalConfirmationPR) -> Unit,
) {
    val colors = LocalTaminColors.current
    // Filtered on the status itself. Filtering on "not approved" put rejected rows under «در انتظار».
    val rows = remember(confirmations, filterIndex) {
        when (filterIndex) {
            FILTER_APPROVED -> confirmations
                .filter { it.confirmationStatus == ConfirmationStatus.APPROVED }
                .toImmutableList()

            FILTER_PENDING -> confirmations
                .filter { it.confirmationStatus == ConfirmationStatus.PENDING }
                .toImmutableList()

            else -> confirmations
        }
    }

    if (rows.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.xl),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.confirmations_filter_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textMuted,
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        overscrollEffect = rememberJellyOverscroll(),
    ) {
        itemsIndexed(
            items = rows,
            key = { _, item -> item.listKey }
        ) { index, item ->
            MedicalConfirmationCard(
                item = item,
                onSelectDetail = { onSelectDetail(item) },
                modifier = Modifier
                    .staggeredItemEntrance(index = index, key = item.listKey, state = staggerState)
                    .padding(horizontal = Spacing.page)
                    .padding(
                        top = Spacing.xs,
                        bottom = if (index == rows.lastIndex) {
                            Spacing.md
                        } else {
                            Spacing.cardGap
                        },
                    ),
            )
        }
    }
}

@Composable
private fun MedicalConfirmationCard(
    item: MedicalConfirmationPR,
    onSelectDetail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val tone = confirmationTone(item.confirmationStatus)

    val defaultSupportType = stringResource(Res.string.confirmations_default_support_type)
    val defaultCenter = stringResource(Res.string.confirmations_default_center)
    val defaultBranch = stringResource(Res.string.confirmations_default_branch)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.cardCompact)
            .accentStripe(tone.accent, width = TreatmentConfirmationsDimens.verdictBarWidth)
            .border(
                width = Thickness.border,
                color = colors.border.copy(alpha = 0.5f),
                shape = RoundedCornerShape(CornerRadius.cardCompact),
            )
            .padding(
                start = TreatmentConfirmationsDimens.cardPaddingHorizontal,
                end = TreatmentConfirmationsDimens.cardPaddingHorizontal,
                top = TreatmentConfirmationsDimens.cardPaddingTop,
                bottom = TreatmentConfirmationsDimens.cardPaddingBottom,
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                modifier = Modifier
                    .clip(RoundedCornerShape(CornerRadius.chip))
                    .background(colors.teal.copy(alpha = 0.12f))
                    .padding(horizontal = Spacing.md, vertical = Spacing.xs),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_medical_records),
                    contentDescription = null,
                    tint = colors.teal,
                    modifier = Modifier.size(TreatmentConfirmationsDimens.iconSizeMedium),
                )
                Text(
                    text = item.supportType.ifBlank { defaultSupportType },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.teal,
                )
            }

            ConfirmationStatusChip(statusDesc = item.statusDesc, tone = tone)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.cardGap),
            verticalArrangement = Arrangement.spacedBy(TreatmentConfirmationsDimens.medicalCenterSpacing),
        ) {
            Text(
                text = stringResource(Res.string.confirmations_medical_authority_label),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
            Text(
                text = item.treatmentCenter.ifBlank { defaultCenter },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        RestPanel(
            label = stringResource(Res.string.confirmations_outpatient_rest_label),
            days = item.numberOfOutpatientDays,
            fromDate = item.outpatientRestStartDate,
            toDate = item.outpatientRestEndDate,
            accent = colors.teal,
            modifier = Modifier.padding(top = TreatmentConfirmationsDimens.cardSectionSpacing),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = TreatmentConfirmationsDimens.cardSectionSpacing),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_medical_centers),
                    contentDescription = null,
                    tint = colors.textMuted,
                    modifier = Modifier.size(TreatmentConfirmationsDimens.iconSizeSmall),
                )
                Text(
                    text = stringResource(
                        Res.string.confirmations_branch_named,
                        item.branchName.ifBlank { defaultBranch },
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary,
                )
            }

            Row(
                modifier = Modifier
                    .height(TreatmentConfirmationsDimens.detailsButtonHeight)
                    .clip(RoundedCornerShape(CornerRadius.chip))
                    .background(tealSweep())
                    .clickable(onClick = onSelectDetail)
                    .padding(horizontal = Spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(Res.string.confirmations_action_details),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }
    }
}

/**
 * The one teal sweep the design reuses for «جزئیات», the rest timeline's bar and the
 * saved-to-inbox modal — the same two stops in the same order in all three places.
 */
@Composable
private fun tealSweep(): Brush = startToEndGradient(listOf(TaminTeal500, TaminTeal900))

/**
 * The framed rest period: how many days, and a timeline running between the two dates.
 *
 * The two ends are fixed-width columns so each date sits centred over its own marker; the bar
 * between them is inset by half a column at each side, which puts its ends underneath the markers
 * rather than stopping short of them.
 */
@Composable
private fun RestPanel(
    label: String,
    days: String,
    fromDate: String,
    toDate: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.xl))
            .background(colors.bgPage)
            .border(
                width = Thickness.border,
                color = colors.border.copy(alpha = 0.4f),
                shape = RoundedCornerShape(CornerRadius.xl),
            )
            .padding(
                start = TreatmentConfirmationsDimens.restPanelPaddingHorizontal,
                end = TreatmentConfirmationsDimens.restPanelPaddingHorizontal,
                top = TreatmentConfirmationsDimens.restPanelPaddingTop,
                bottom = TreatmentConfirmationsDimens.restPanelPaddingBottom,
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = accent,
            )
            Row(verticalAlignment = Alignment.Bottom) {
                NumericText(
                    text = days.toPersianDigits(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = accent,
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
                Text(
                    text = stringResource(Res.string.confirmations_unit_day),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                )
            }
        }

        RestTimeline(
            fromDate = fromDate,
            toDate = toDate,
            accent = accent,
            modifier = Modifier.padding(top = Spacing.md),
        )
    }
}

/**
 * The dated bar between the start and end of a rest period. Shared by the list card and the
 * detail view, which draw it identically apart from the gap under each date.
 */
@Composable
internal fun RestTimeline(
    fromDate: String,
    toDate: String,
    accent: Color,
    modifier: Modifier = Modifier,
    markerSpacing: Dp = TreatmentConfirmationsDimens.restEndpointSpacing,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        // Drawn before the columns so the markers sit on top of the bar, not the other way round.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    horizontal = TreatmentConfirmationsDimens.restBarInset,
                    vertical = TreatmentConfirmationsDimens.restBarBottomInset,
                )
                .height(TreatmentConfirmationsDimens.restBarHeight)
                .clip(RoundedCornerShape(CornerRadius.sm))
                .background(tealSweep()),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            RestEndpoint(date = fromDate, markerSpacing = markerSpacing) {
                RestStartMarker(color = accent)
            }
            RestEndpoint(date = toDate, markerSpacing = markerSpacing) {
                RestEndPin(color = accent)
            }
        }
    }
}

@Composable
private fun RestEndpoint(date: String, markerSpacing: Dp, marker: @Composable () -> Unit) {
    Column(
        modifier = Modifier.width(TreatmentConfirmationsDimens.restEndpointWidth),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(markerSpacing),
    ) {
        // Already «۱۴۰۱/۱۰/۱۱» by the time it gets here -- the presentation mapper turns the
        // service's unseparated `14011011` into a readable date, so nothing reformats it twice.
        NumericText(
            text = date,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = LocalTaminColors.current.textPrimary,
        )
        marker()
    }
}

/**
 * The start of the period: a filled disc with a white core, ringed by a soft halo.
 *
 * The halo is drawn rather than laid out, matching the `box-shadow` spread it comes from — it
 * has to stay out of the measured size or the disc's center would no longer meet the bar.
 */
@Composable
private fun RestStartMarker(color: Color) {
    val halo = color.copy(alpha = TreatmentConfirmationsDimens.MARKER_HALO_ALPHA)
    Box(
        modifier = Modifier
            .size(TreatmentConfirmationsDimens.restStartMarkerSize)
            .drawBehind {
                drawCircle(
                    color = halo,
                    radius = size.minDimension / 2 + TreatmentConfirmationsDimens.restStartMarkerRing.toPx(),
                )
            }
            .background(color, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(TreatmentConfirmationsDimens.statusDotSize)
                .background(Color.White, CircleShape),
        )
    }
}

/** The end of the period: a map pin, traced from the design's 24x30 path. */
@Composable
private fun RestEndPin(color: Color) {
    Canvas(
        modifier = Modifier.size(
            width = TreatmentConfirmationsDimens.restEndPinWidth,
            height = TreatmentConfirmationsDimens.restEndPinHeight,
        )
    ) {
        val sx = size.width / TreatmentConfirmationsDimens.PIN_VIEWPORT_WIDTH
        val sy = size.height / TreatmentConfirmationsDimens.PIN_VIEWPORT_HEIGHT
        fun x(v: Float) = v * sx
        fun y(v: Float) = v * sy

        val pin = Path().apply {
            moveTo(x(12f), y(1.5f))
            cubicTo(x(7f), y(1.5f), x(3f), y(5.5f), x(3f), y(10.5f))
            cubicTo(x(3f), y(16.9f), x(12f), y(28.5f), x(12f), y(28.5f))
            cubicTo(x(12f), y(28.5f), x(21f), y(16.9f), x(21f), y(10.5f))
            cubicTo(x(21f), y(5.5f), x(17f), y(1.5f), x(12f), y(1.5f))
            close()
        }
        drawPath(pin, color)
        drawCircle(
            color = Color.White,
            radius = TreatmentConfirmationsDimens.PIN_CORE_RADIUS * sx,
            center = Offset(x(12f), y(10.5f)),
        )
    }
}

/**
 * The design's acknowledgement that the certificate reached the personal inbox.
 *
 * Not [com.tamin.taminhamrah.ui.components.TaminConfirmationDialog]: that one asks a question --
 * two buttons, a square tinted icon tile. This one only reports, so it carries the design's
 * gradient disc and a single dismiss.
 */
@Composable
internal fun ConfirmationSavedDialog(onDismiss: () -> Unit) {
    val colors = LocalTaminColors.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = TreatmentConfirmationsDimens.modalMaxWidth)
                .fillMaxWidth()
                .taminSurface(CornerRadius.card)
                .padding(
                    start = TreatmentConfirmationsDimens.modalPaddingHorizontal,
                    end = TreatmentConfirmationsDimens.modalPaddingHorizontal,
                    top = TreatmentConfirmationsDimens.modalPaddingTop,
                    bottom = TreatmentConfirmationsDimens.modalPaddingBottom,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(TreatmentConfirmationsDimens.modalIconSize)
                    .background(tealSweep(), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_check),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(TreatmentConfirmationsDimens.modalIconGlyphSize),
                )
            }

            Spacer(modifier = Modifier.height(TreatmentConfirmationsDimens.modalIconBottomSpacing))

            Text(
                text = stringResource(Res.string.confirmations_saved_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(TreatmentConfirmationsDimens.modalTextSpacing))

            Text(
                text = stringResource(Res.string.confirmations_saved_body),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(TreatmentConfirmationsDimens.modalButtonSpacing))

            TaminFilledButton(
                text = stringResource(Res.string.confirmations_saved_acknowledge),
                onClick = onDismiss,
                height = TreatmentConfirmationsDimens.modalButtonHeight,
                shape = RoundedCornerShape(TreatmentConfirmationsDimens.modalButtonCornerRadius),
                background = tealSweep(),
                textStyle = MaterialTheme.typography.bodyMedium,
                shadowColor = TaminTeal900.copy(alpha = TreatmentConfirmationsDimens.MODAL_BUTTON_SHADOW_ALPHA),
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MedicalConfirmationDetailView(
    item: MedicalConfirmationPR,
    onOpenCertificate: (String) -> Unit,
    onSendToInbox: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var showOperationsSheet by remember { mutableStateOf(false) }
    val tone = confirmationTone(item.confirmationStatus)

    val defaultSupportType = stringResource(Res.string.confirmations_default_support_type)
    val defaultCenter = stringResource(Res.string.confirmations_default_center)
    val defaultBranch = stringResource(Res.string.confirmations_default_branch)
    val branchApproval = stringResource(Res.string.confirmations_branch_approval)

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bgPage)
            .verticalScroll(scrollState)
            .padding(horizontal = Spacing.page, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .taminSurface(CornerRadius.cardCompact)
                .accentStripe(tone.accent, width = TreatmentConfirmationsDimens.verdictBarWidth)
                .border(
                    width = Thickness.border,
                    color = colors.border.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(CornerRadius.cardCompact),
                )
                .padding(
                    start = TreatmentConfirmationsDimens.detailCardPaddingHorizontal,
                    end = TreatmentConfirmationsDimens.detailCardPaddingHorizontal,
                    top = TreatmentConfirmationsDimens.detailCardPaddingTop,
                    bottom = TreatmentConfirmationsDimens.detailCardPaddingBottom,
                ),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    modifier = Modifier
                        .clip(RoundedCornerShape(CornerRadius.chip))
                        .background(colors.teal.copy(alpha = 0.12f))
                        .padding(horizontal = Spacing.md, vertical = Spacing.xs),
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_tamin_medical_records),
                        contentDescription = null,
                        tint = colors.teal,
                        modifier = Modifier.size(TreatmentConfirmationsDimens.iconSizeMedium),
                    )
                    Text(
                        text = item.supportType.ifBlank { defaultSupportType },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.teal,
                    )
                }

                ConfirmationStatusChip(statusDesc = item.statusDesc, tone = tone)
            }

            Column(verticalArrangement = Arrangement.spacedBy(TreatmentConfirmationsDimens.medicalCenterSpacing)) {
                Text(
                    text = stringResource(Res.string.confirmations_medical_authority_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                )
                Text(
                    text = item.treatmentCenter.ifBlank { defaultCenter },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .taminSurface(CornerRadius.cardCompact)
                .border(
                    width = Thickness.border,
                    color = colors.border.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(CornerRadius.cardCompact),
                )
                .padding(
                    start = TreatmentConfirmationsDimens.detailRestPanelPaddingHorizontal,
                    end = TreatmentConfirmationsDimens.detailRestPanelPaddingHorizontal,
                    top = TreatmentConfirmationsDimens.detailRestPanelPaddingTop,
                    bottom = TreatmentConfirmationsDimens.detailRestPanelPaddingBottom,
                ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.confirmations_outpatient_rest_label),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.teal,
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    NumericText(
                        text = item.numberOfOutpatientDays.toPersianDigits(),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = colors.teal,
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(
                        text = stringResource(Res.string.confirmations_unit_day),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                    )
                }
            }

            RestTimeline(
                fromDate = item.outpatientRestStartDate,
                toDate = item.outpatientRestEndDate,
                accent = colors.teal,
                markerSpacing = TreatmentConfirmationsDimens.detailRestMarkerSpacing,
                modifier = Modifier.padding(top = TreatmentConfirmationsDimens.cardSectionSpacing),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(
                text = stringResource(Res.string.confirmations_inpatient_rest_header),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .taminSurface(CornerRadius.card)
                    .border(
                        width = Thickness.border,
                        color = colors.border.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(CornerRadius.card),
                    )
                    .padding(horizontal = Spacing.md),
            ) {
                DetailRow(
                    label = stringResource(Res.string.confirmations_start_date_label),
                    value = item.inpatientRestStartDate.ifBlank { ABSENT_VALUE },
                )
                DetailRow(
                    label = stringResource(Res.string.confirmations_end_date_label),
                    value = item.inpatientRestEndDate.ifBlank { ABSENT_VALUE },
                )
                DetailRow(
                    label = stringResource(Res.string.confirmations_inpatient_days_count),
                    value = if (item.hasInpatientRest) item.numberOfInpatientDays else ABSENT_VALUE,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(
                text = stringResource(Res.string.confirmations_review_status_header),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .taminSurface(CornerRadius.card)
                    .border(
                        width = Thickness.border,
                        color = colors.border.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(CornerRadius.card),
                    )
                    .padding(horizontal = Spacing.md),
            ) {
                DetailRow(
                    label = stringResource(Res.string.confirmations_branch_reviewer_label),
                    value = item.branchName.ifBlank { defaultBranch },
                )
                DetailRow(
                    label = stringResource(Res.string.confirmations_branch_status_label),
                    value = item.branchStatus.ifBlank { branchApproval },
                )
                DetailRow(
                    label = stringResource(Res.string.confirmations_unapproved_period),
                    value = if (item.hasUnapprovedPeriod) {
                        stringResource(
                            Res.string.confirmations_unapproved_period_range,
                            item.unapprovedFromDate.ifBlank { ABSENT_VALUE },
                            item.unapprovedToDate.ifBlank { ABSENT_VALUE },
                        )
                    } else {
                        ABSENT_VALUE
                    },
                )
            }
        }

        if (item.description.isNotBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = stringResource(Res.string.confirmations_opinion_header),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .taminSurface(CornerRadius.card)
                        .border(
                            width = Thickness.border,
                            color = colors.border.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(CornerRadius.card),
                        )
                        .padding(Spacing.md),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        Box(
                            modifier = Modifier
                                .width(TreatmentConfirmationsDimens.verdictBarWidth)
                                .height(TreatmentConfirmationsDimens.verdictBarHeightExpanded)
                                .background(colors.teal, CircleShape)
                        )
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textPrimary,
                            lineHeight = TreatmentConfirmationsDimens.verdictLineHeight,
                        )
                    }
                }
            }
        }

        // Both operations address the row by repId, and this endpoint sends none. Offering the
        // button anyway would post a blank id and fail after the user had already tapped it.
        if (item.hasCertificate) {
            Spacer(modifier = Modifier.height(Spacing.xs))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CornerRadius.chip))
                    .background(colors.teal)
                    .clickable { showOperationsSheet = true }
                    .padding(vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                // On the teal fill in both themes, so the ink is white in both. `bgSurface` reads
                // white in light and navy in dark, which is how this went unnoticed.
                Text(
                    text = stringResource(Res.string.confirmations_download_image_action),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TaminOnAccentInk,
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_download),
                    contentDescription = null,
                    tint = TaminOnAccentInk,
                    modifier = Modifier.size(TreatmentConfirmationsDimens.iconSizeMedium),
                )
            }
        }
    }

    if (showOperationsSheet) {
        ConfirmationsOperationsSheet(
            onDismiss = { showOperationsSheet = false },
            onOpenCertificate = {
                showOperationsSheet = false
                onOpenCertificate(item.repId)
            },
            onSendToInbox = {
                showOperationsSheet = false
                onSendToInbox(item.repId)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfirmationsOperationsSheet(
    onDismiss: () -> Unit,
    onOpenCertificate: () -> Unit,
    onSendToInbox: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.bgSurface,
        shape = RoundedCornerShape(topStart = TreatmentDimens.sheetCornerRadius, topEnd = TreatmentDimens.sheetCornerRadius),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = Spacing.sm, bottom = Spacing.xs)
                    .width(TreatmentDimens.sheetHandleWidth)
                    .height(TreatmentDimens.sheetHandleHeight)
                    .background(colors.border, CircleShape),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = stringResource(Res.string.costs_action_operations),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                modifier = Modifier.padding(bottom = Spacing.xs),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CornerRadius.chip))
                    .background(colors.teal.copy(alpha = 0.1f))
                    .clickable(onClick = onOpenCertificate)
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_download),
                    contentDescription = null,
                    tint = colors.teal,
                    modifier = Modifier.size(IconSize.medium),
                )
                Text(
                    text = stringResource(Res.string.costs_view_certificate),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.teal,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CornerRadius.chip))
                    .background(colors.blueBg)
                    .clickable(onClick = onSendToInbox)
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_medical_approvals),
                    contentDescription = null,
                    tint = colors.blueText,
                    modifier = Modifier.size(IconSize.medium),
                )
                Text(
                    text = stringResource(Res.string.costs_send_to_inbox),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.blueText,
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = LocalTaminColors.current.textPrimary,
) {
    val colors = LocalTaminColors.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = TreatmentConfirmationsDimens.detailRowPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                modifier = Modifier.weight(1f),
            )
            NumericText(
                text = value.toPersianDigits(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End
                ),
                color = valueColor,
                modifier = Modifier.weight(1f),
            )
        }
        DashedDivider(
            modifier = Modifier.fillMaxWidth(),
            color = colors.border.copy(alpha = 0.5f),
        )
    }
}

@Composable
private fun DashedDivider(
    modifier: Modifier = Modifier,
    color: Color = LocalTaminColors.current.border,
    strokeWidth: Dp = TreatmentConfirmationsDimens.dashedStrokeWidth,
    dashLength: Dp = TreatmentConfirmationsDimens.dashedDashLength,
    gapLength: Dp = TreatmentConfirmationsDimens.dashedGapLength,
) {
    Canvas(modifier = modifier.height(strokeWidth)) {
        val strokeWidthPx = strokeWidth.toPx()
        val dashPx = dashLength.toPx()
        val gapPx = gapLength.toPx()
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashPx, gapPx), 0f)
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = strokeWidthPx,
            pathEffect = pathEffect,
        )
    }
}

@Composable
private fun ConfirmationsErrorState(message: String) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(Spacing.page),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_cross),
            contentDescription = null,
            tint = colors.dangerText,
            modifier = Modifier.size(IconSize.large),
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ConfirmationsShimmerSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        repeat(3) {
            ConfirmationsShimmerCard()
        }
    }
}

@Composable
private fun ConfirmationsShimmerCard() {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            // Same frame as MedicalConfirmationCard, or the list visibly reshapes when it lands.
            .taminSurface(CornerRadius.cardCompact)
            .border(
                width = Thickness.border,
                color = colors.border.copy(alpha = 0.5f),
                shape = RoundedCornerShape(CornerRadius.cardCompact),
            )
            .padding(
                start = TreatmentConfirmationsDimens.cardPaddingHorizontal,
                end = TreatmentConfirmationsDimens.cardPaddingHorizontal,
                top = TreatmentConfirmationsDimens.cardPaddingTop,
                bottom = TreatmentConfirmationsDimens.cardPaddingBottom,
            ),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .width(TreatmentConfirmationsDimens.shimmerFilterPillWidth)
                    .height(TreatmentConfirmationsDimens.skeletonChipHeight)
                    .clip(RoundedCornerShape(CornerRadius.chip))
                    .shimmer(),
            )
            Box(
                modifier = Modifier
                    .width(TreatmentConfirmationsDimens.shimmerStatusPillWidth)
                    .height(TreatmentConfirmationsDimens.skeletonChipHeight)
                    .clip(RoundedCornerShape(CornerRadius.chip))
                    .shimmer(),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
            Box(
                modifier = Modifier
                    .width(TreatmentConfirmationsDimens.shimmerLabelWidth)
                    .height(TreatmentConfirmationsDimens.skeletonLineShort)
                    .clip(RoundedCornerShape(TreatmentConfirmationsDimens.skeletonLineCorner))
                    .shimmer(),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(TreatmentConfirmationsDimens.skeletonLineMedium)
                    .clip(RoundedCornerShape(TreatmentConfirmationsDimens.skeletonLineCorner))
                    .shimmer(),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(TreatmentConfirmationsDimens.shimmerTimelineHeight)
                .clip(RoundedCornerShape(CornerRadius.md))
                .shimmer(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .width(TreatmentConfirmationsDimens.shimmerBranchWidth)
                    .height(TreatmentConfirmationsDimens.skeletonLineTall)
                    .clip(RoundedCornerShape(TreatmentConfirmationsDimens.skeletonLineCorner))
                    .shimmer(),
            )
            Box(
                modifier = Modifier
                    .width(TreatmentConfirmationsDimens.shimmerDetailBtnWidth)
                    .height(TreatmentConfirmationsDimens.shimmerButtonHeight)
                    .clip(RoundedCornerShape(CornerRadius.chip))
                    .shimmer(),
            )
        }
    }
}

