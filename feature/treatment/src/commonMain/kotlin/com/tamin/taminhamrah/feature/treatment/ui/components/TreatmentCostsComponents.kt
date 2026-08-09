package com.tamin.taminhamrah.feature.treatment.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentDimens
import com.tamin.taminhamrah.feature.treatment.ui.model.isFileSettled
import com.tamin.taminhamrah.feature.treatment.ui.model.isPaid
import com.tamin.taminhamrah.model.treatment.TreatmentCostPR
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.shimmer
import com.tamin.taminhamrah.ui.toRialAmount
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_hide_details
import taminx.core.core_ui.action_show_details
import taminx.core.core_ui.category_misc_claims
import taminx.core.core_ui.costs_action_operations
import taminx.core.core_ui.costs_admission_label
import taminx.core.core_ui.costs_center_name
import taminx.core.core_ui.costs_empty
import taminx.core.core_ui.costs_file_payment
import taminx.core.core_ui.costs_file_status
import taminx.core.core_ui.costs_list_section_title
import taminx.core.core_ui.costs_main_insured
import taminx.core.core_ui.costs_official_doc_subtitle
import taminx.core.core_ui.costs_other_services_payment
import taminx.core.core_ui.costs_patient_national_code
import taminx.core.core_ui.costs_refund_date
import taminx.core.core_ui.costs_return_reason
import taminx.core.core_ui.costs_send_to_inbox
import taminx.core.core_ui.costs_view_certificate
import taminx.core.core_ui.error_pull_to_retry
import taminx.core.core_ui.ic_check
import taminx.core.core_ui.ic_setting
import taminx.core.core_ui.ic_share
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_download
import taminx.core.core_ui.ic_tamin_medical_records
import taminx.core.core_ui.ic_tamin_misc_claims

/** Shown where the service sent nothing, matching the previous app's placeholder. */
private const val ABSENT_VALUE = "-"

@Composable
internal fun CertificateList(
    certificates: ImmutableList<TreatmentCostPR>,
    isLoading: Boolean,
    error: String?,
    onOpenCertificate: (String) -> Unit,
    onSendToInbox: (String) -> Unit,
) {
    val colors = LocalTaminColors.current
    val staggerState = rememberStaggeredEntranceState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading && certificates.isEmpty() -> item { CostsShimmerSkeleton() }

            error != null -> item { CostsErrorState(message = error) }

            certificates.isEmpty() -> item {
                TaminEmptyState(message = stringResource(Res.string.costs_empty))
            }

            else -> {
                item {
                    Text(
                        text = stringResource(Res.string.costs_list_section_title),
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.textMuted,
                        modifier = Modifier
                            .padding(horizontal = Spacing.page)
                            .padding(top = Spacing.md, bottom = Spacing.xs),
                    )
                }

                itemsIndexed(certificates) { index, item ->
                    CertificateCard(
                        item = item,
                        index = index,
                        totalCount = certificates.size,
                        onOpenCertificate = onOpenCertificate,
                        onSendToInbox = onSendToInbox,
                        modifier = Modifier
                            .staggeredItemEntrance(index = index, key = item.repId, state = staggerState)
                            .padding(horizontal = Spacing.page)
                            .padding(
                                top = Spacing.xs,
                                bottom = if (index == certificates.lastIndex) {
                                    Spacing.md
                                } else {
                                    Spacing.cardGap
                                },
                            ),
                    )
                }
            }
        }
    }
}

// Read from the design's document card; see docs/superpowers/specs/2026-08-05-treatment-costs-card.md
private val CardPaddingHorizontal = 20.dp
private val CardPaddingTop = 13.dp
private val CardPaddingBottom = 16.dp
private val DetailRowPadding = 12.dp
private val IconTileSize = 44.dp
private val StatusCheckSize = 21.dp
private val AdmissionLetterSpacing = 1.sp

private const val TILE_GRADIENT_TOP_ALPHA = 0.10f
private const val TILE_GRADIENT_BOTTOM_ALPHA = 0.22f
private const val TILE_BORDER_ALPHA = 0.24f

/**
 * One refund certificate card matching the new treatment cost screen design.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CertificateCard(
    item: TreatmentCostPR,
    index: Int,
    totalCount: Int,
    onOpenCertificate: (String) -> Unit,
    onSendToInbox: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    @Suppress("DEPRECATION")
    val clipboardManager = LocalClipboardManager.current
    var expanded by remember(item.repId) { mutableStateOf(true) }
    var showOperationsSheet by remember { mutableStateOf(false) }

    val paid = item.isPaid
    val accent = if (paid) colors.teal else colors.orangeText
    val fileSettled = item.isFileSettled

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.card)
            .accentStripe(accent)
            .border(
                width = 1.dp,
                color = colors.border.copy(alpha = 0.5f),
                shape = RoundedCornerShape(CornerRadius.card),
            )
            .padding(
                start = CardPaddingHorizontal,
                end = CardPaddingHorizontal,
                top = CardPaddingTop,
                bottom = CardPaddingBottom,
            ),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        // 1. Top Meta Row: Official Doc Subtitle & Index Pagination
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(Res.string.costs_official_doc_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
            NumericText(
                text = "${(index + 1).toString().padStart(2, '0').toPersianDigits()} / ${totalCount.toString().padStart(2, '0').toPersianDigits()}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
        }

        // 2. Main Header Row: Icon, Patient Name, Category Pill, Date & Status Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .size(IconTileSize)
                        .clip(RoundedCornerShape(CornerRadius.md))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    accent.copy(alpha = TILE_GRADIENT_TOP_ALPHA),
                                    accent.copy(alpha = TILE_GRADIENT_BOTTOM_ALPHA),
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = accent.copy(alpha = TILE_BORDER_ALPHA),
                            shape = RoundedCornerShape(CornerRadius.md),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_tamin_misc_claims),
                        contentDescription = null,
                        tint = colors.teal,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                    Text(
                        text = item.nameFamil,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        StatusPill(
                            text = stringResource(Res.string.category_misc_claims),
                            containerColor = colors.teal.copy(alpha = 0.1f),
                            contentColor = colors.teal,
                        )
                        NumericText(
                            text = item.serviceDate.toPersianDigits(),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted,
                        )
                    }
                }
            }

            // Status Checkmark Badge
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_check),
                    contentDescription = null,
                    tint = if (paid) colors.greenText else colors.orangeText,
                    modifier = Modifier.size(StatusCheckSize),
                )
                Text(
                    text = item.payStatusDesc,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (paid) colors.greenText else colors.orangeText,
                )
            }
        }

        // 3. Admission Number Row with Dashed Divider and Copy Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_medical_records),
                    contentDescription = null,
                    tint = colors.textMuted,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = stringResource(Res.string.costs_admission_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                )
            }

            DashedDivider(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Spacing.sm),
                color = colors.border.copy(alpha = 0.6f),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_share),
                    contentDescription = "کپی",
                    tint = colors.textMuted,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable {
                            clipboardManager.setText(AnnotatedString(item.noPazir))
                        },
                )
                NumericText(
                    text = item.noPazir.toPersianDigits(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textPrimary,
                )
            }
        }

        // 4. Details Section (Key-Value List)
        AnimatedVisibility(visible = expanded) {
            Column {
                DoubleRule(modifier = Modifier.padding(top = Spacing.sm, bottom = Spacing.xxs))

                DetailRow(
                    label = stringResource(Res.string.costs_patient_national_code),
                    value = item.maliCode,
                    divider = RowDivider.Solid,
                )
                DetailRow(
                    label = stringResource(Res.string.costs_main_insured),
                    value = item.nameAsli,
                )
                DetailRow(
                    label = stringResource(Res.string.costs_file_status),
                    value = item.statusDesc,
                    valueColor = if (fileSettled) colors.greenText else colors.dangerText,
                    valueBold = true,
                )
                DetailRow(
                    label = stringResource(Res.string.costs_return_reason),
                    value = item.returnReason,
                )
                DetailRow(
                    label = stringResource(Res.string.costs_refund_date),
                    value = item.estimatePayDate,
                    valueColor = colors.teal,
                )
                DetailRow(
                    label = stringResource(Res.string.costs_center_name),
                    value = item.healthcenterName,
                )
                DetailRow(
                    label = stringResource(Res.string.costs_file_payment),
                    value = item.payService.toRialAmount(ABSENT_VALUE),
                )
                DetailPillRow(
                    label = stringResource(Res.string.costs_other_services_payment),
                    amount = item.payOtherService.toRialAmount(ABSENT_VALUE),
                )
            }
        }

        // 5. Card Footer Actions: Toggle & Operations Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Expand/Collapse Details Button
            val rotation by animateFloatAsState(
                targetValue = if (expanded) {
                    TreatmentDimens.chevronOpenDegrees
                } else {
                    TreatmentDimens.chevronClosedDegrees
                },
                label = "certificate-chevron",
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(CornerRadius.chip))
                    .background(colors.bgPage)
                    .clickable { expanded = !expanded }
                    .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                    contentDescription = null,
                    tint = colors.teal,
                    modifier = Modifier.size(IconSize.small).rotate(rotation),
                )
                Text(
                    text = stringResource(
                        if (expanded) Res.string.action_hide_details else Res.string.action_show_details,
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.teal,
                )
            }

            // Operations Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(CornerRadius.chip))
                    .background(colors.teal)
                    .clickable { showOperationsSheet = true }
                    .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_setting),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(IconSize.small),
                )
                Text(
                    text = stringResource(Res.string.costs_action_operations),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
            }
        }
    }

    if (showOperationsSheet) {
        OperationsBottomSheet(
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

/** Operations action sheet displayed when clicking "عملیات" */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OperationsBottomSheet(
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
                    imageVector = vectorResource(Res.drawable.ic_tamin_misc_claims),
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

/** Which rule a detail row draws beneath itself. The design's first row is solid, the rest are
 *  dashed, and the last carries none. */
private enum class RowDivider { Solid, Dashed, None }

/**
 * The 12dp-above-and-below rhythm every detail row shares, plus its trailing rule.
 *
 * Holding the padding and the divider here rather than spacing the rows from the parent is what
 * lets a rule sit flush between two rows instead of floating in the gap.
 */
@Composable
private fun DetailRowContainer(divider: RowDivider, content: @Composable () -> Unit) {
    val colors = LocalTaminColors.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = DetailRowPadding)) { content() }
        when (divider) {
            RowDivider.Solid -> TaminDivider()
            RowDivider.Dashed -> DashedDivider(
                modifier = Modifier.fillMaxWidth(),
                color = colors.divider,
            )
            RowDivider.None -> Unit
        }
    }
}

/** The design's 3px double rule: a solid hairline over a lighter one. */
@Composable
private fun DoubleRule(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(thickness = 1.dp, color = colors.border)
        Spacer(Modifier.height(1.dp))
        HorizontalDivider(thickness = 1.dp, color = colors.divider)
    }
}

/** Dashed horizontal divider for admission number row */
@Composable
private fun DashedDivider(
    modifier: Modifier = Modifier,
    color: Color = LocalTaminColors.current.border,
    strokeWidth: Dp = 1.dp,
    dashLength: Dp = 4.dp,
    gapLength: Dp = 4.dp,
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

/** Key/Value detail line */
@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = LocalTaminColors.current.textPrimary,
    valueBold: Boolean = false,
    divider: RowDivider = RowDivider.Dashed,
) = DetailRowContainer(divider) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value.ifBlank { ABSENT_VALUE }.toPersianDigits(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (valueBold) FontWeight.SemiBold else FontWeight.Normal,
            color = valueColor,
            textAlign = TextAlign.End,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

/** Key/Value detail line where value is formatted inside a green pill/badge container */
@Composable
private fun DetailPillRow(
    label: String,
    amount: String,
    divider: RowDivider = RowDivider.None,
) = DetailRowContainer(divider) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(CornerRadius.chip))
                .background(colors.greenBg)
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        ) {
            Text(
                text = amount.toPersianDigits(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.greenText,
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
private fun CostsErrorState(message: String) {
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
        Text(
            text = stringResource(Res.string.error_pull_to_retry),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CostsShimmerSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        repeat(TreatmentDimens.certificateSkeletonRows) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TreatmentDimens.certificateSkeletonHeight)
                    .taminSurface(CornerRadius.card)
                    .shimmer(),
            )
        }
    }
}
